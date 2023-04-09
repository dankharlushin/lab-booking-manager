package ru.github.dankharlushin.labcontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.dto.OsSessionExpireNotification;
import ru.github.dankharlushin.lbmlib.shell.service.notification.ShellNotificationService;
import ru.github.dankharlushin.lbmlib.shell.service.notification.Urgency;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;

import java.util.List;
import java.util.Optional;

@Service
public class LabNotificationServiceImpl implements LabNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(LabNotificationServiceImpl.class);

    private final ShellNotificationService shellNotificationService;
    private final ShellProcessService shellProcessService;

    private final String sessionExpirationTitle;
    private final String sessionExpirationBody;

    public LabNotificationServiceImpl(final ShellNotificationService shellNotificationService,
                                      final ShellProcessService shellProcessService,
                                      @Value("${lab-controller.notification.session-expiration.title}") final String sessionExpirationTitle,
                                      @Value("${lab-controller.notification.session-expiration.body}") final String sessionExpirationBody) {
        this.shellNotificationService = shellNotificationService;
        this.shellProcessService = shellProcessService;
        this.sessionExpirationTitle = sessionExpirationTitle;
        this.sessionExpirationBody = sessionExpirationBody;
    }

    @Override
    public void sessionExpireNotification(final OsSessionExpireNotification notificationDto) {
        logger.info("Start processing notification with id [{}], lab [{}], user [{}]",
                notificationDto.getId(),
                notificationDto.getLabAppName(),
                notificationDto.getOsUsername());
        try {
            final List<Integer> labAppPids = shellProcessService.getPidsByCommand(notificationDto.getLabAppName());
            if (!labAppPids.isEmpty()) {
                for (final Integer labAppPid : labAppPids) {
                    final Optional<String> userByPid = shellProcessService.getUserByPid(labAppPid);
                    userByPid.filter(notificationDto.getOsUsername()::equals)
                            .ifPresent(user -> notifyUserAboutSessionExpiration(user, notificationDto));
                }
            }
        } catch (final Exception e) {
            logger.error("Unable to send notification with id [{}], lab [{}], user [{}]",
                    notificationDto.getId(),
                    notificationDto.getLabAppName(),
                    notificationDto.getOsUsername(),
                    e);
        }
    }

    private void notifyUserAboutSessionExpiration(final String user, final OsSessionExpireNotification notificationDto) {
        final Urgency osNotificationUrgency;
        switch (notificationDto.getUrgencyLevel()) {
            case LOW -> osNotificationUrgency = Urgency.LOW;
            case CRITICAL -> osNotificationUrgency = Urgency.CRITICAL;
            default -> osNotificationUrgency = Urgency.NORMAL;
        }

        shellNotificationService.notifyUser(user, sessionExpirationTitle, sessionExpirationBody, osNotificationUrgency);
        logger.info("Notification with id [{}], lab [{}], user [{}] was succesfully sended",
                notificationDto.getId(),
                notificationDto.getLabAppName(),
                notificationDto.getOsUsername());
    }

}
