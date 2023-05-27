package ru.github.dankharlushin.labcontroller.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.labcontroller.service.LabAccessService;

@Component
public class LabAccessManager {

    private static final Logger logger = LoggerFactory.getLogger(LabAccessManager.class);

    private final LabAccessService labAccessService;
    private final Integer checkInterval;

    public LabAccessManager(final LabAccessService labAccessService,
                            @Value("${lab-controller.access.fixed-rate-ms}") final Integer checkInterval) {
        this.labAccessService = labAccessService;
        this.checkInterval = checkInterval;
    }

    @Scheduled(fixedRateString = "${lab-controller.access.fixed-rate-ms}")
    public void check() {
        logger.info("Start lab access checking");
        labAccessService.setAccess(checkInterval);
        logger.info("Finish lab access checking");
    }
}
