package ru.github.dankharlushin.labcontroller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.dankharlushin.labcontroller.service.LabNotificationService;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.LabControllerBookingExpireNotification;

@RestController
@RequestMapping(("/notification"))
public class LabNotificationRestController {

    private static final Logger logger = LoggerFactory.getLogger(LabNotificationRestController.class);

    private final LabNotificationService notificationService;

    public LabNotificationRestController(final LabNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/expire-booking")
    public ResponseEntity<String> sessionExpiration(@RequestBody final LabControllerBookingExpireNotification notification) {
        logger.info("Receive session expire notification with id [{}], lab [{}], user [{}]",
                notification.getId(),
                notification.getLabAppName(),
                notification.getOsUsername());
        notificationService.notifyUser(notification);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> sendErrorMessage(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
