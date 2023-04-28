package ru.github.dankharlushin.notificationservice.creator;

import org.springframework.beans.factory.annotation.Value;
import ru.github.dankharlushin.lbmlib.data.dto.notification.TelegramUiNotification;

public abstract class TelegramUiNotificationCreator<T extends TelegramUiNotification> extends NotificationCreator<T> {

    @Value("${notification-service.telegram-ui.base-url}")
    private String telegramUiBaseUrl;

    @Override
    protected String baseAddress() {
        return telegramUiBaseUrl;
    }
}
