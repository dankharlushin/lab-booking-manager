package ru.github.dankharlushin.lbmlib.data.dto.notification.impl;

import lombok.Getter;
import ru.github.dankharlushin.lbmlib.data.dto.NotificationUrgency;
import ru.github.dankharlushin.lbmlib.data.dto.notification.LabControllerNotification;

@Getter
public class LabControllerBookingExpireNotification implements LabControllerNotification {

    private final String id;
    private final String osUsername;
    private final String labAppName;
    private final Integer expireInMinutes;
    private final NotificationUrgency urgencyLevel;
    private final String address;

    public LabControllerBookingExpireNotification(final String id,
                                                  final String osUsername,
                                                  final String labAppName,
                                                  final Integer expireInMinutes,
                                                  final NotificationUrgency urgencyLevel,
                                                  final String address) {
        this.id = id;
        this.osUsername = osUsername;
        this.labAppName = labAppName;
        this.expireInMinutes = expireInMinutes;
        this.urgencyLevel = urgencyLevel;
        this.address = address;
    }

    @Override
    public void setAddress(final String address) {
        throw new UnsupportedOperationException("Address can't be reset");
    }
}
