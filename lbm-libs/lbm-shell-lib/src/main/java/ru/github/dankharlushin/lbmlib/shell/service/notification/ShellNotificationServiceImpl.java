package ru.github.dankharlushin.lbmlib.shell.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShellNotificationServiceImpl implements ShellNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ShellNotificationServiceImpl.class);

    private final CommandLineExecutor executor;
    private final MessageSourceAccessor sourceAccessor;

    public ShellNotificationServiceImpl(final CommandLineExecutor executor,
                                        final MessageSourceAccessor sourceAccessor) {
        this.executor = executor;
        this.sourceAccessor = sourceAccessor;
    }

    @Override
    public void sendSimpleNotification(final String username,
                                       final String messageTitle,
                                       final String messageBody,
                                       final Urgency urgency) {
        try (final InputStream idStream = executor.id(username, Map.of("-u", ""))) {
            final String userId = new String(idStream.readAllBytes()).replace("\n", "");
            final List<Map.Entry<String, String>> preExecutionOptions = new ArrayList<>();
            preExecutionOptions.add(new AbstractMap.SimpleEntry<>("sudo", ""));
            preExecutionOptions.add(new AbstractMap.SimpleEntry<>("-u", username));
            preExecutionOptions.add(new AbstractMap.SimpleEntry<>("DISPLAY=:0", ""));
            preExecutionOptions.add(
                    new AbstractMap.SimpleEntry<>("DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/" + userId + "/bus", ""));

            final int exitValue = executor.notifySend(messageTitle, messageBody, Map.of(), preExecutionOptions);
            if (exitValue != 0) {
                logger.error("Unable to notify user [{}], exit value [{}]", username, exitValue);
            }
        } catch (final ExecutionException | IOException e) {
            logger.error("Unable to notify user [{}]", username, e);
        }
    }

    @Override
    public void sendMessageSourceNotification(final String username,
                                              final String messageTitleCode,
                                              final String messageBodyCode,
                                              final Urgency urgency,
                                              @Nullable final Object[] titleArgs,
                                              @Nullable final Object[] bodyArgs) {
        final String messageTitle = sourceAccessor.getMessage(messageTitleCode, titleArgs);
        final String messageBody = sourceAccessor.getMessage(messageBodyCode, bodyArgs);

        sendSimpleNotification(username, messageTitle, messageBody, urgency);
    }
}
