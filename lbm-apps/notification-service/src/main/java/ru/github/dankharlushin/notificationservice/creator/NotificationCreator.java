package ru.github.dankharlushin.notificationservice.creator;

import ru.github.dankharlushin.lbmlib.data.dto.notification.Notification;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.util.List;

public abstract class NotificationCreator<T extends Notification> {

    public List<T> createNotificationsCommon(final BookingService bookingService) {
        return createNotification(bookingService);
    }

    protected abstract List<T> createNotification(final BookingService bookingService);
}
