package ru.github.dankharlushin.lbmlib.data.service.impl;

import jakarta.persistence.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;
import ru.github.dankharlushin.lbmlib.data.repository.BookingRepository;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.lbmlib.data.util.BookingUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final List<LocalTime> availableBookingStart;
    private final Integer bookingTimeMinutes;
    private final Integer availableDaysForBooking;

    public BookingServiceImpl(final BookingRepository bookingRepository,
                              @Value("${libs.data.service.booking.default.booking-time}")
                              final Integer bookingTimeMinutes,
                              @Value("${libs.data.service.booking.default.available-days}")
                              final Integer availableDaysForBooking,
                              @Value("#{'${libs.data.service.booking.default.start-booking-time}'.split(',')}")
                              final List<LocalTime> availableBookingStart) {
        this.bookingRepository = bookingRepository;
        this.availableBookingStart = availableBookingStart;
        this.availableDaysForBooking = availableDaysForBooking;
        this.bookingTimeMinutes = bookingTimeMinutes;
    }

    @Override
    public Map<String, String> getCurrentBookingLabAppNameToUsername() {
        Map<String, String> result = new HashMap<>();
        final List<Tuple> rows = bookingRepository.getLabAppNameToUserOsUsername(
                BookingStatus.CANCELED,
                LocalDateTime.now(),
                LocalDateTime.now());
        for (final Tuple row : rows) {
            if (result.containsKey(row.get(0, String.class))) {
                logger.error("Found two bookings on one lab");
            }
            result.put(row.get(0, String.class), row.get(1, String.class));
        }
        return result;
    }

    @Override
    public List<LocalDate> getAvailableDates(final Integer labUnitId) {
        final List<LocalDate> availableDates = new ArrayList<>();
        final LocalDate start = LocalDate.now().plusDays(1);
        final LocalDate end = start.plusDays(availableDaysForBooking);

        final List<Booking> bookings = bookingRepository.getBookingsByLabIdAndDatesAndStatusNot(labUnitId,
                BookingStatus.CANCELED,
                start.atStartOfDay(),
                end.atStartOfDay());
        for (LocalDate i = start; i.isBefore(end); i = i.plusDays(1)) {
            final LocalDate finalI = i;
            final List<Period> dateBookingPeriods = bookings
                    .stream()
                    .map(booking -> new Period(booking.getStartDateTime(), booking.getEndDateTime()))
                    .filter(period -> finalI.isEqual(period.start().toLocalDate()))
                    .toList();
            if (i.getDayOfWeek() != DayOfWeek.SUNDAY && hasAvailableTime(i, dateBookingPeriods)) {
                availableDates.add(i);
            }
        }

        return availableDates;
    }

    @Override
    public List<Period> getAvailableTime(final LocalDate date, final Integer labUnitId) {
        final List<Booking> bookings = bookingRepository.getBookingsByLabIdAndDateAndStatusNot(labUnitId,
                BookingStatus.CANCELED,
                date);
        if (bookings.isEmpty()) {
            return availableBookingStart
                    .stream()
                    .map(start -> createBookingPeriod(date, start))
                    .toList();
        }

        final List<Period> availableTime = new ArrayList<>();
        final List<Period> datePeriods = bookings
                .stream()
                .map(booking -> new Period(booking.getStartDateTime(), booking.getEndDateTime()))
                .filter(period -> date.isEqual(period.start().toLocalDate()))
                .toList();
        for (final LocalTime potentialStartTime : availableBookingStart) {
            if (isTimeAvailable(date, potentialStartTime, datePeriods)) {
                availableTime.add(createBookingPeriod(date, potentialStartTime));
            }
        }

        return availableTime;
    }

    @Override
    public List<Booking> getFutureBookingByUsername(final String osUsername) {
        return bookingRepository.getCreatedBookingsByUsernameAndStartDateAfter(osUsername, LocalDateTime.now());
    }

    @Override
    public List<Booking> getCreatedBookingByStart(final LocalDateTime startDateTime) {
        return bookingRepository.getBookingsByStartDateTimeAndStatusNot(startDateTime, BookingStatus.CANCELED);
    }

    @Override
    public List<Booking> getCreatedBookingByEnd(final LocalDateTime endDateTime) {
        return bookingRepository.getBookingsByEndDateTimeAndStatusNot(endDateTime, BookingStatus.CANCELED);
    }

    @Override
    public Booking getById(final Long id) {
        return bookingRepository.getReferenceById(id);
    }

    @Override
    public void save(final Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public void deleteById(final Long id) {
        bookingRepository.deleteById(id);
    }

    private boolean hasAvailableTime(final LocalDate date, final List<Period> datePeriods) {
        if (datePeriods.isEmpty()) {
            return true;
        }
        for (final LocalTime potentialStartTime : availableBookingStart) {
            if (isTimeAvailable(date, potentialStartTime, datePeriods)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeAvailable(final LocalDate date, final LocalTime time, final List<Period> datePeriods) {
        boolean available = true;
        final LocalDateTime potentialStartDateTime = LocalDateTime.of(date, time);
        final LocalDateTime potentialEndDateTime = potentialStartDateTime.plusMinutes(bookingTimeMinutes);
        final Period potentialPeriod = new Period(potentialStartDateTime, potentialEndDateTime);

        for (final Period bookedPeriod : datePeriods) {
            if (BookingUtil.isOverlapping(potentialPeriod, bookedPeriod)) {
                available = false;
                break;
            }
        }

        return available;
    }

    private Period createBookingPeriod(final LocalDate date, final LocalTime startTime) {
        return new Period(LocalDateTime.of(date, startTime), LocalDateTime.of(date, startTime.plusMinutes(bookingTimeMinutes)));
    }
}
