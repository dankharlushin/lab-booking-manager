package ru.github.dankharlushin.notificationservice.creator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.TelegramUiStartBookingNotification;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TelegramUiStartBookingNotificationCreator extends TelegramUiNotificationCreator<TelegramUiStartBookingNotification> {

    private final String startBookingUrlSuffix;
    private final Integer checkIntervalInMillis;
    private final Duration beforeBookingStart;

    public TelegramUiStartBookingNotificationCreator(@Value("${notification-service.telegram-ui.start-booking-suffix}")
                                                     final String startBookingUrlSuffix,
                                                     @Value("${notification-service.notification.check-interval}")
                                                     final Integer checkIntervalInMillis,
                                                     @Value("${notification-service.notification.time.before.booking-start}")
                                                     final String beforeBookingStart) {
        this.startBookingUrlSuffix = startBookingUrlSuffix;
        this.checkIntervalInMillis = checkIntervalInMillis;
        this.beforeBookingStart = Duration.parse(beforeBookingStart);
    }

    @Override
    protected List<TelegramUiStartBookingNotification> createNotification(final BookingService bookingService) {
        final LocalDateTime startDateTimeBegin = LocalDateTime.now().plusMinutes(beforeBookingStart.toMinutes());
        final LocalDateTime startDateTimeEnd = startDateTimeBegin.plus(checkIntervalInMillis, ChronoUnit.MILLIS);
        final List<Booking> createdBooking = bookingService
                .getCreatedBookingByStartBetween(startDateTimeBegin, startDateTimeEnd);
        return createdBooking
                .stream()
                .filter(b -> Objects.nonNull(b.getUser().getChatId()))
                .map(b -> new TelegramUiStartBookingNotification(
                        UUID.randomUUID().toString(),
                        b.getUser().getChatId(),
                        b.getLab().getName(),
                        (int) beforeBookingStart.toMinutes()
                ))
                .toList();
    }

    @Override
    protected String addressSuffix() {
        return startBookingUrlSuffix;
    }
}
