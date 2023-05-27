package ru.github.dankharlushin.labcontroller.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.lbmlib.data.service.LabUnitService;
import ru.github.dankharlushin.lbmlib.shell.exception.ShellException;
import ru.github.dankharlushin.lbmlib.shell.service.access.ShellAccessService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LabAccessService {

    private static final Logger logger = LoggerFactory.getLogger(LabAccessService.class);

    private final ShellAccessService shellAccessService;
    private final LabUnitService labUnitService;
    private final BookingService bookingService;

    private final String labsDir;
    private final String admin;

    public LabAccessService(final ShellAccessService shellAccessService,
                            final LabUnitService labUnitService,
                            final BookingService bookingService,
                            @Value("${lab-controller.labs.dir}") final String labsDir,
                            @Value("${lab-controller.access.admin}") final String admin) {
        this.shellAccessService = shellAccessService;
        this.labUnitService = labUnitService;
        this.bookingService = bookingService;
        this.labsDir = labsDir;
        this.admin = admin;
    }

    public void setAccess(final int checkIntervalMillis) {
        final LocalDateTime beginInterval = LocalDateTime.now();
        final LocalDateTime endInterval = beginInterval.plus(checkIntervalMillis, ChronoUnit.MILLIS);
        openAccess(beginInterval, endInterval);
        closeAccess(beginInterval, endInterval);
    }

    private void openAccess(final LocalDateTime beginInterval, final LocalDateTime endInterval) {
        final List<Booking> started = bookingService.getCreatedBookingByStartBetween(beginInterval, endInterval);
        for (final Booking booking : started) {
            final String appName = booking.getLab().getAppName();
            final String osUsername = booking.getUser().getOsUsername();
            try {
                shellAccessService.grantAccessToUser(absolutePath(appName), osUsername);
            } catch (final Exception e) {
                logger.error("Error with open access to lab [{}] for user [{}]", appName, osUsername, e);
            }
            logger.info("Successfully grant access to lab [{}] for user [{}]", appName, osUsername);
        }
    }

    private void closeAccess(final LocalDateTime beginInterval, final LocalDateTime endInterval) {
        final List<Booking> finished = bookingService.getCreatedBookingByEndBetween(beginInterval, endInterval);
        for (final Booking booking : finished) {
            final String appName = booking.getLab().getAppName();
            final String osUsername = booking.getUser().getOsUsername();
            try {
                shellAccessService.grantAccessToUser(absolutePath(appName), admin);
            } catch (final Exception e) {
                logger.error("Error with close access to lab [{}] for user [{}]", appName, osUsername, e);
            }
            logger.info("Successfully block access to lab [{}] for user [{}]", appName, osUsername);
        }
    }

    @PostConstruct
    private void setSafePermissions() {
        final List<String> labApps = labUnitService.findAllLabApps();
        for (final String labApp : labApps) {
            shellAccessService.setSafePermissions(absolutePath(labApp));
        }
        logger.info("Safe permission on lab apps successfully set");
    }

    private String absolutePath(final String file) {
        return labsDir + file;
    }
}
