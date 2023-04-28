package ru.github.dankharlushin.telegramui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.TelegramUiStartBookingNotification;
import ru.github.dankharlushin.telegramui.service.TelegramNotificationService;

@RestController
@RequestMapping("/notification")
public class TelegramNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationController.class);

    private final TelegramNotificationService notificationService;

    public TelegramNotificationController(final TelegramNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/start-booking")
    public ResponseEntity<String> startBooking(@RequestBody final TelegramUiStartBookingNotification notification) {
        logger.info("Receive booking start notification with id [{}], lab [{}], chatId [{}]",
                notification.getId(),
                notification.getLabName(),
                notification.getChatId());
        notificationService.notifyUser(notification);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> sendErrorMessage(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
