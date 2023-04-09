package ru.github.dankharlushin.lbmlib.shell.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutor;

import java.util.Map;

@Component
public class ShellNotificationServiceImpl implements ShellNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ShellNotificationServiceImpl.class);

    private final CommandLineExecutor executor;

    public ShellNotificationServiceImpl(final CommandLineExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void notifyUser(final String username,
                           final String messageTitle,
                           final String messageBody,
                           final Urgency urgency) {
        try {
            final int exitValue = executor.notifySend("sudo -u " + username, messageTitle, messageBody, Map.of());//fixme check!
            if (exitValue != 0) {
                logger.error("Unable to notify user [{}], exit value [{}]", username, exitValue);
            }
        } catch (final ExecutionException e) {
            logger.error("Unable to notify user [{}]", username, e);
        }
    }
}
