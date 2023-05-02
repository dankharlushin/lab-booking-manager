package ru.github.dankharlushin.accountsynchronizer.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.github.dankharlushin.accountsynchronizer.model.Group;
import ru.github.dankharlushin.accountsynchronizer.model.Shadow;
import ru.github.dankharlushin.accountsynchronizer.parser.EtcParser;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

import java.util.List;
import java.util.Optional;

public class SynchronizationWorker {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizationWorker.class);

    private final UserService userService;
    private final String groupName;

    public SynchronizationWorker(final UserService userService,
                                 final String groupName) {
        this.userService = userService;
        this.groupName = groupName;
    }

    public void work() {
        final List<Group> groups = EtcParser.parseGroup();
        final Optional<Group> optTargetGroup = groups
                .stream()
                .filter(g -> g.groupName().equals(groupName))
                .findFirst();
        if (optTargetGroup.isEmpty()) {
            logger.error("Can't find group for synchronization with groupname [{}]", groupName);
            return;
        }
        final Group targetGroup = optTargetGroup.get();
        deleteInvalid(targetGroup);

        if (targetGroup.groupList().isEmpty()) {
            logger.warn("Group with name [{}] has no users", groupName);
            return;
        }
        addValid(targetGroup);
    }

    private void addValid(final Group targetGroup) {
        final List<Shadow> shadows = EtcParser.parseShadow();
        for (final String groupUsername : targetGroup.groupList()) {
            if (!userService.existsByOsUsername(groupUsername)) {
                final Optional<String> password = shadows
                        .stream().filter(s -> groupUsername.equals(s.username()))
                        .map(Shadow::password)
                        .findFirst();
                final User user = new User();
                user.setOsUsername(groupUsername);
                user.setOsPassword(password
                        .orElseThrow(() -> new IllegalStateException("Can't find password for user " + groupUsername)));
                userService.save(user);
                logger.debug("User with username [{}] was added into database", groupUsername);
            }
        }
    }

    private void deleteInvalid(final Group targetGroup) {
        userService.findAll()
                .filter(u -> isNotExistsInGroup(u, targetGroup))
                .map(User::getOsUsername)
                .forEach(userService::deleteByOsUsername);
    }

    private boolean isNotExistsInGroup(final User u, final Group targetGroup) {
        final List<String> groupList = targetGroup.groupList();
        if (groupList.isEmpty()) { return true; }
        return groupList.contains(u.getOsUsername());
    }
}
