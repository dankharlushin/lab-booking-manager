package ru.github.dankharlushin.accountsynchronizer.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;
import ru.github.dankharlushin.accountsynchronizer.model.Group;
import ru.github.dankharlushin.accountsynchronizer.model.Shadow;
import ru.github.dankharlushin.accountsynchronizer.parser.EtcParser;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class SynchronizationWorker {

    private static final Set<String> VALID_HASH_ALGORITHMS = Set.of("$5", "$6");
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationWorker.class);

    private final UserService userService;
    private final String groupName;

    public SynchronizationWorker(final UserService userService,
                                 final String groupName) {
        this.userService = userService;
        this.groupName = groupName;
    }

    public void work() {
        logger.info("Synchronization started");
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
        logger.info("Synchronization finished");
    }

    private void addValid(final Group targetGroup) {
        final List<Shadow> shadows = EtcParser.parseShadow();
        for (final String groupUsername : targetGroup.groupList()) {
            if (!userService.existsByOsUsername(groupUsername)) {
                final Optional<String> password = shadows
                        .stream().filter(s -> groupUsername.equals(s.username()))
                        .map(Shadow::password)
                        .findFirst();
                password.ifPresentOrElse(p -> addUserToDb(groupUsername, p),
                        () -> logger.error("Can't add user [{}] to database without password", groupUsername));
            }
        }
    }

    private void addUserToDb(final String groupUsername, final String password) {
        final String hashType = password.substring(0, password.indexOf("$", password.indexOf("$") + 1));
        if (!VALID_HASH_ALGORITHMS.contains(hashType)) {
            logger.error("Can't add user [{}] to database with invalid password hash algorithm", groupUsername);
            return;
        }

        final User user = new User();
        user.setOsUsername(groupUsername);
        user.setOsPassword(password);
        userService.save(user);
        logger.debug("User with username [{}] was added into database", groupUsername);
    }

    private void deleteInvalid(final Group targetGroup) {
        final List<Integer> invalidIds = new ArrayList<>();
        final Slice<User> slice = userService.findAll();
        collectInvalidIds(slice.get(), targetGroup, invalidIds);
        while (slice.hasNext()) {
            collectInvalidIds(slice.get(), targetGroup, invalidIds);
        }

        userService.deleteAllById(invalidIds);
        logger.debug("Delete [{}] invalid users from database", invalidIds.size());
    }

    private void collectInvalidIds(final Stream<User> userStream, final Group targetGroup, final List<Integer> invalidIds) {
        userStream
                .filter(u -> isNotExistsInGroup(u, targetGroup))
                .map(User::getId)
                .forEach(invalidIds::add);
    }

    private boolean isNotExistsInGroup(final User u, final Group targetGroup) {
        final List<String> groupList = targetGroup.groupList();
        if (groupList.isEmpty()) { return true; }
        return !groupList.contains(u.getOsUsername());
    }
}
