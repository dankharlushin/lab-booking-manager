package ru.github.dankharlushin.lbmlib.data.dto.notification;

public interface TelegramUiNotification extends Notification {

    long getChatId();

    void setAddress(final String address);
}
