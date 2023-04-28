package ru.github.dankharlushin.lbmlib.data.dto.notification.impl;

import lombok.Getter;
import ru.github.dankharlushin.lbmlib.data.dto.notification.TelegramUiNotification;

@Getter
public class TelegramUiStartBookingNotification implements TelegramUiNotification {

    private final String id;
    private final long chatId;
    private final String labName;
    private final Integer startInMinutes;
    private String address;

    public TelegramUiStartBookingNotification(final String id,
                                              final long chatId,
                                              final String labName,
                                              final Integer startInMinutes) {
        this.id = id;
        this.chatId = chatId;
        this.labName = labName;
        this.startInMinutes = startInMinutes;
    }


    @Override
    public void setAddress(final String address) {
        if (this.address != null) {
            throw new UnsupportedOperationException("Address can't be reset");
        }
        this.address = address;
    }
}
