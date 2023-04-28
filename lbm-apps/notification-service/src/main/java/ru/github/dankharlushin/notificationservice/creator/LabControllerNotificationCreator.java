package ru.github.dankharlushin.notificationservice.creator;

import org.springframework.beans.factory.annotation.Value;
import ru.github.dankharlushin.lbmlib.data.dto.notification.LabControllerNotification;

public abstract class LabControllerNotificationCreator<T extends LabControllerNotification> extends NotificationCreator<T> {

    @Value("${notification-service.lab-controller.base-url}")
    private String labControllerBaseUrl;

    @Override
    protected String baseAddress() {
        return labControllerBaseUrl;
    }
}
