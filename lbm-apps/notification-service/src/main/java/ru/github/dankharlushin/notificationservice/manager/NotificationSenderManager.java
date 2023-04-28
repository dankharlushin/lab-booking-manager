package ru.github.dankharlushin.notificationservice.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.github.dankharlushin.lbmlib.data.dto.notification.Notification;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.notificationservice.creator.NotificationCreator;

import java.util.List;

@Component
public class NotificationSenderManager {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSenderManager.class);

    private final List<NotificationCreator<? extends Notification>> notificationCreators;
    private final BookingService bookingService;
    private final RestTemplate restTemplate;

    public NotificationSenderManager(final List<NotificationCreator<? extends Notification>> notificationCreators,
                                     final BookingService bookingService,
                                     final RestTemplate restTemplate) {
        this.notificationCreators = notificationCreators;
        this.bookingService = bookingService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelayString = "${notification-service.notification.check-interval}")
    public void work() {
        notificationCreators.stream()
                .flatMap(creator -> creator.createNotificationsCommon(bookingService).stream())
                .forEach(this::send);
    }

    private void send(final Notification notification) {
        final ResponseEntity<String> responseEntity = restTemplate
                .postForEntity(notification.getAddress(), notification, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            logger.info("Successfully sent message with id [{}] to address [{}]",
                    notification.getId(),
                    notification.getAddress());
        } else {
            logger.error("Can't send message with id [{}] to address [{}], status [{}], error [{}]",
                    notification.getId(),
                    notification.getAddress(),
                    responseEntity.getStatusCode().value(),
                    responseEntity.getBody());
        }
    }
}
