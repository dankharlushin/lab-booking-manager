package ru.github.dankharlushin.notificationservice.creator;

import ru.github.dankharlushin.lbmlib.data.dto.notification.Notification;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.util.List;

public abstract class NotificationCreator<T extends Notification> {

    public final List<T> createNotificationsCommon(final BookingService bookingService) {
        final List<T> notifications = createNotification(bookingService);
        notifications.forEach(notification -> notification.setAddress(notificationAddress()));
        return notifications;
    }

    protected abstract List<T> createNotification(final BookingService bookingService);

    protected final String notificationAddress() {
        return baseAddress() + addressSuffix();
    }

    protected abstract String baseAddress();

    protected abstract String addressSuffix();
}
