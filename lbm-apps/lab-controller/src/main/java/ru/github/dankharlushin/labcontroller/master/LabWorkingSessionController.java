package ru.github.dankharlushin.labcontroller.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;

import java.util.*;

@Component
public class LabWorkingSessionController {

    private static final Logger logger = LoggerFactory.getLogger(LabWorkingSessionController.class);

    private final BookingService bookingService;
    private final ShellProcessService shellProcessService;

    private final List<String> labAppNames;

    public LabWorkingSessionController(final BookingService bookingService,
                                       final ShellProcessService shellProcessService,
                                       @Value("#{'${lab-controller.session.lab-app-names}'.split(',')}") final List<String> labAppNames) {
        this.bookingService = bookingService;
        this.shellProcessService = shellProcessService;
        this.labAppNames = labAppNames;
    }

    @Scheduled(fixedDelayString = "${lab-controller.session.fixed-delay-ms}")
    public void check() {
        logger.info("Start bookings verification");
        try {
            final Map<String, String> expectedAppToUser = bookingService.getCurrentBookingLabAppNameToUsername();
            for (final String labAppName : labAppNames) {
                final String expectedLabUser = expectedAppToUser.get(labAppName);
                final Map<String, List<Integer>> actualAppUsers = getCurrentUsersToPidsByLabAppName(labAppName);
                validateSession(labAppName, expectedLabUser, actualAppUsers);
            }
        } catch (final Exception e) {
            logger.error("Unable to verify working sessions", e);
        }
    }

    private void validateSession(final String labAppName,
                                 final String expectedLabUser,
                                 final Map<String, List<Integer>> actualAppUsers) {
        boolean valid = true;
        for (final Map.Entry<String, List<Integer>> entry : actualAppUsers.entrySet()) {
            if (!entry.getKey().equals(expectedLabUser)) {
                entry.getValue().forEach(pid -> killByPid(pid, entry.getKey(), labAppName));
                valid = false;
            }
        }

        if (valid) {
            logger.info("Found 0 invalid sessions for lab [{}]", labAppName);
        }
    }

    private Map<String, List<Integer>> getCurrentUsersToPidsByLabAppName(final String labAppName) {
        final Map<String, List<Integer>> result = new HashMap<>();
        final List<Integer> labAppPids = shellProcessService.getPidsByCommand(labAppName);
        for (final Integer labAppPid : labAppPids) {
            final Optional<String> userByPid = shellProcessService.getUserByPid(labAppPid);
            userByPid.ifPresentOrElse(s -> addPidToUsername(result, labAppPid, s),
                    () -> logger.error("Unable to get lab [{}] user by PID [{}]", labAppName, labAppPid));
        }
        return result;
    }

    private void killByPid(final Integer pid, final String actualUser, final String labAppName) {
        shellProcessService.killByPid(pid);
        logger.info("Invalid session for lab [{}] with user [{}] was successfully deleted", labAppName, actualUser);
    }

    private void addPidToUsername(final Map<String, List<Integer>> result,
                                  final Integer labAppPid,
                                  final String s) {
        result.putIfAbsent(s, new ArrayList<>());
        result.get(s).add(labAppPid);
    }
}
