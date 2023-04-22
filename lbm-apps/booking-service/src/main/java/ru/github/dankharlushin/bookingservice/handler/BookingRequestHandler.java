package ru.github.dankharlushin.bookingservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.github.dankharlushin.bookingservice.exception.ValidationException;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationResponse;
import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.lbmlib.data.service.LabUnitService;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(BookingRequestHandler.class);
    private static final String DEFAULT_ERROR_MESSAGE = "defaultErrorMessage";
    private static final String BOOKING_TIME_VALIDATION_ERROR_MESSAGE = "bookingTimeValidationErrorMessage";

    private final BookingService bookingService;
    private final UserService userService;
    private final LabUnitService labService;
    private final RestTemplate restTemplate;
    private final MessageSourceAccessor sourceAccessor;
    private final Long bookingTimeMinutes;

    public BookingRequestHandler(final BookingService bookingService,
                                 final UserService userService,
                                 final LabUnitService labService,
                                 final RestTemplate restTemplate,
                                 final MessageSourceAccessor sourceAccessor,
                                 @Value("${libs.data.service.booking.default.booking-time}") final Long bookingTimeMinutes) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.labService = labService;
        this.restTemplate = restTemplate;
        this.sourceAccessor = sourceAccessor;
        this.bookingTimeMinutes = bookingTimeMinutes;
    }

    public void handleRegistration(final BookingRegistrationRequest registrationRequest) {
        logger.info("Start handling registration request [{}]", registrationRequest);
        try {
            final User user = userService.getByOsUsernameName(registrationRequest.osUsername());
            if (user == null) {
                throw new IllegalStateException("User with osName [" + registrationRequest.osUsername() + "] not found");
            }
            final LabUnit labUnit = labService.getById(registrationRequest.labId());
            if (labUnit == null) {
                throw new IllegalStateException("Lab with id [" + registrationRequest.labId() + "] not found");
            }

            final LocalDateTime startBooking = LocalDateTime.of(registrationRequest.startDate(), registrationRequest.startTime());
            final LocalDateTime endBooking = startBooking.plusMinutes(bookingTimeMinutes);
            validateBookingTime(startBooking, endBooking, labUnit.getId());

            final Booking newBooking = Booking.builder()
                    .lab(labUnit)
                    .user(user)
                    .startDateTime(startBooking)
                    .endDateTime(endBooking)
                    .status(BookingStatus.CREATED)
                    .build();
            bookingService.save(newBooking);
            sendCallbackResponse(registrationRequest, null);
        } catch (final ValidationException e) {
            logger.info("Booking validation error, registration request [{}], error [{}]", registrationRequest, e.getMessage());
            sendCallbackResponse(registrationRequest, e.getMessage());
        } catch (final RuntimeException e) {
            logger.error("Error while process booking registration request [{}]", registrationRequest, e);
            sendCallbackResponse(registrationRequest, sourceAccessor.getMessage(DEFAULT_ERROR_MESSAGE));
        }
    }

    private void validateBookingTime(final LocalDateTime startBooking,
                                     final LocalDateTime endBooking,
                                     final Integer labId) throws ValidationException {
        final Period bookingPeriod = new Period(startBooking, endBooking);
        final List<Period> availableTime = bookingService.getAvailableTime(startBooking.toLocalDate(), labId);
        for (final Period period : availableTime) {
            if (bookingPeriod.equals(period)) {
                return;
            }
        }

        throw new ValidationException(sourceAccessor.getMessage(BOOKING_TIME_VALIDATION_ERROR_MESSAGE));
    }

    private void sendCallbackResponse(final BookingRegistrationRequest registrationRequest,
                                      final String errorMessage) {
        final BookingRegistrationResponse callback = new BookingRegistrationResponse(errorMessage,
                registrationRequest.callbackResponse());
        final ResponseEntity<Void> responseEntity = restTemplate.postForEntity(registrationRequest.callbackUrl(),
                callback,
                Void.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK && logger.isErrorEnabled()) {
            logger.error("Unable to deliver callback by url [{}]", registrationRequest.callbackUrl());
        }
    }
}
