package ru.github.dankharlushin.labcontroller.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.labcontroller.service.LabWorkingSessionService;

@Component
public class LabWorkingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(LabWorkingSessionManager.class);

    private final LabWorkingSessionService labWorkingSessionService;

    public LabWorkingSessionManager(final LabWorkingSessionService labWorkingSessionService) {
        this.labWorkingSessionService = labWorkingSessionService;
    }

    @Scheduled(fixedDelayString = "${lab-controller.session.fixed-delay-ms}")
    public void check() {
        logger.info("Start bookings verification");
        labWorkingSessionService.verifySessions();
        logger.info("Finish bookings verification");
    }
}
