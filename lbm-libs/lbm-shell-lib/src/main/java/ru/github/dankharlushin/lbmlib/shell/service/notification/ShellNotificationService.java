package ru.github.dankharlushin.lbmlib.shell.service.notification;

import org.springframework.lang.Nullable;

public interface ShellNotificationService {

    void sendSimpleNotification(final String username,
                                final String messageTitle,
                                final String messageBody,
                                final Urgency urgency);

    void sendMessageSourceNotification(final String username,
                                       final String messageTitleCode,
                                       final String messageBodyCode,
                                       final Urgency urgency,
                                       @Nullable final Object[] titleArgs,
                                       @Nullable final Object[] bodyArgs);
}
