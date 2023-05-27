package ru.github.dankharlushin.labcontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;
import ru.github.dankharlushin.lbmlib.data.service.LabUnitService;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;

import java.util.*;

@Service
public class LabWorkingSessionService {

    private static final Logger logger = LoggerFactory.getLogger(LabWorkingSessionService.class);

    private final BookingService bookingService;
    private final LabUnitService labUnitService;
    private final ShellProcessService shellProcessService;

    public LabWorkingSessionService(final BookingService bookingService,
                                    final LabUnitService labUnitService,
                                    final ShellProcessService shellProcessService) {
        this.bookingService = bookingService;
        this.labUnitService = labUnitService;
        this.shellProcessService = shellProcessService;
    }

    public void verifySessions() {
        try {
            final Map<String, String> expectedAppToUser = bookingService.getCurrentBookingLabAppNameToUsername();
            final List<String> labAppNames = labUnitService.findAllLabApps();
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
