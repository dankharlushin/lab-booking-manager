package ru.github.dankharlushin.labcontroller.service;

import ru.github.dankharlushin.lbmlib.data.dto.OsSessionExpireNotification;

public interface LabNotificationService {

    void sessionExpireNotification(final OsSessionExpireNotification notificationDto);
}
