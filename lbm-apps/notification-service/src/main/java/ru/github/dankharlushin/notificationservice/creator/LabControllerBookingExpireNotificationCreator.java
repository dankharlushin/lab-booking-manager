package ru.github.dankharlushin.notificationservice.creator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.dto.NotificationUrgency;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.LabControllerBookingExpireNotification;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class LabControllerBookingExpireNotificationCreator
        extends LabControllerNotificationCreator<LabControllerBookingExpireNotification> {

    private final String endBookingUrlSuffix;
    private final Duration beforeBookingEnd;

    public LabControllerBookingExpireNotificationCreator(@Value("${notification-service.lab-controller.expire-booking-suffix}")
                                                         final String endBookingUrlSuffix,
                                                         @Value("${notification-service.notification.time.before.booking-end}")
                                                         final String beforeBookingEnd) {
        this.endBookingUrlSuffix = endBookingUrlSuffix;
        this.beforeBookingEnd = Duration.parse(beforeBookingEnd);
    }

    @Override
    protected List<LabControllerBookingExpireNotification> createNotification(final BookingService bookingService) {
        final List<Booking> createdBooking = bookingService
                .getCreatedBookingByEnd(LocalDateTime.now().minusMinutes(beforeBookingEnd.toMinutes()));
        return createdBooking
                .stream()
                .map(b -> new LabControllerBookingExpireNotification(
                        UUID.randomUUID().toString(),
                        b.getUser().getOsUsername(),
                        b.getLab().getAppName(),
                        (int) beforeBookingEnd.toMinutes(),
                        NotificationUrgency.NORMAL)
                )
                .toList();
    }

    @Override
    protected String addressSuffix() {
        return endBookingUrlSuffix;
    }
}
