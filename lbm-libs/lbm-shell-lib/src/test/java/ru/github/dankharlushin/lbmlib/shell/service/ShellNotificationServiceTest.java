package ru.github.dankharlushin.lbmlib.shell.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.MessageSourceAccessor;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutorImpl;
import ru.github.dankharlushin.lbmlib.shell.service.notification.ShellNotificationService;
import ru.github.dankharlushin.lbmlib.shell.service.notification.ShellNotificationServiceImpl;
import ru.github.dankharlushin.lbmlib.shell.service.notification.Urgency;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.Mockito.mock;

class ShellNotificationServiceTest {

    private ShellNotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new ShellNotificationServiceImpl(new CommandLineExecutorImpl(), mock(MessageSourceAccessor.class));
    }

    @Test //TODO work with common user
    void testNotifyUserSimple() {
        assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> notificationService.sendSimpleNotification("aspushkin", "Message title", "Message body", Urgency.NORMAL));
    }
}
