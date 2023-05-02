package ru.github.dankharlushin.accountsynchronizer.util;

import org.junit.jupiter.api.Test;
import ru.github.dankharlushin.accountsynchronizer.model.Group;
import ru.github.dankharlushin.accountsynchronizer.model.Shadow;
import ru.github.dankharlushin.accountsynchronizer.parser.EtcParser;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EtcParserTest {

    @Test
    void testParseGroup() {
        final List<Group> groups = EtcParser.parseGroup();
        final Optional<Group> root = groups
                .stream()
                .filter(g -> "root".equals(g.groupName()))
                .findFirst();

        assertThat(root.isPresent(), is(true));
        final Group group = root.get();
        assertThat(group.groupName(), is("root"));
        assertThat(group.password(), is("x"));
        assertThat(group.groupId(), is(0));
        assertThat(group.groupList().size(), is(0));
    }

    @Test
    void testParseShadow() {
        final List<Shadow> shadows = EtcParser.parseShadow();
        final Optional<Shadow> root = shadows
                .stream()
                .filter(s -> "root".equals(s.username()))
                .findFirst();

        assertThat(root.isPresent(), is(true));
        final Shadow shadow = root.get();
        assertThat(shadow.username(), is("root"));
        assertThat(shadow.password(), is("!"));
        assertThat(shadow.minimum(), is(0));
        assertThat(shadow.maximum(), is(99999));
    }
}
