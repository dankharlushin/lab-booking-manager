package ru.github.dankharlushin.labcontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.LabControllerBookingExpireNotification;
import ru.github.dankharlushin.lbmlib.shell.service.notification.ShellNotificationService;
import ru.github.dankharlushin.lbmlib.shell.service.notification.Urgency;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;

import java.util.List;
import java.util.Optional;

@Service
public class LabNotificationService {

    private static final String BOOKING_EXPIRATION_MESSAGE_TITLE_CODE = "bookingExpirationMessageTitle";
    private static final String BOOKING_EXPIRATION_MESSAGE_BODY_CODE = "bookingExpirationMessageBody";
    private static final Logger logger = LoggerFactory.getLogger(LabNotificationService.class);

    private final ShellNotificationService shellNotificationService;
    private final ShellProcessService shellProcessService;

    public LabNotificationService(final ShellNotificationService shellNotificationService,
                                  final ShellProcessService shellProcessService) {
        this.shellNotificationService = shellNotificationService;
        this.shellProcessService = shellProcessService;
    }

    public void notifyUser(final LabControllerBookingExpireNotification notificationDto) {
        logger.debug("Start processing notification with id [{}], lab [{}], user [{}]",
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

    private void notifyUserAboutSessionExpiration(final String user, final LabControllerBookingExpireNotification notificationDto) {
        final Urgency osNotificationUrgency;
        switch (notificationDto.getUrgencyLevel()) {
            case LOW -> osNotificationUrgency = Urgency.LOW;
            case CRITICAL -> osNotificationUrgency = Urgency.CRITICAL;
            default -> osNotificationUrgency = Urgency.NORMAL;
        }

        shellNotificationService.sendMessageSourceNotification(user,
                BOOKING_EXPIRATION_MESSAGE_TITLE_CODE,
                BOOKING_EXPIRATION_MESSAGE_BODY_CODE,
                osNotificationUrgency,
                null,
                null);
        logger.info("Notification with id [{}], lab [{}], user [{}] was successfully sent",
                notificationDto.getId(),
                notificationDto.getLabAppName(),
                notificationDto.getOsUsername());
    }

}
