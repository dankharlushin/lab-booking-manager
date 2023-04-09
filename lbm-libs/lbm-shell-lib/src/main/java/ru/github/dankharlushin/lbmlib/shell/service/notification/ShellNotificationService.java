package ru.github.dankharlushin.lbmlib.shell.service.notification;

public interface ShellNotificationService {

    void notifyUser(final String username, final String messageTitle, final String messageBody, final Urgency urgency);
}
