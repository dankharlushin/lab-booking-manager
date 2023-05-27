package ru.github.dankharlushin.notificationservice.creator;

import ru.github.dankharlushin.lbmlib.data.dto.notification.Notification;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.util.List;

public abstract class CommonAddressNotificationCreator<T extends Notification>
        extends NotificationCreator<T>  {

    @Override
    public List<T> createNotificationsCommon(final BookingService bookingService) {
        final List<T> notificationList = super.createNotificationsCommon(bookingService);
        notificationList.forEach(notification -> notification.setAddress(notificationAddress()));
        return notificationList;
    }

    protected final String notificationAddress() {
        return baseAddress() + addressSuffix();
    }

    protected abstract String baseAddress();

    protected abstract String addressSuffix();
}
