package ru.github.dankharlushin.accountsynchronizer.parser;

import org.apache.commons.io.IOUtils;
import ru.github.dankharlushin.accountsynchronizer.model.Group;
import ru.github.dankharlushin.accountsynchronizer.model.Shadow;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EtcParser {

    private static final String ETC_DIR = "/etc";

    private EtcParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<Group> parseGroup() {
        try (final FileInputStream inputStream = new FileInputStream(ETC_DIR + "/group")) {
            final String groupFile = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final List<Group> result = new ArrayList<>();
            for (final String groupInfo : groupFile.split("\n")) {
                result.add(buildGroup(groupInfo));
            }
            return result;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to parse /etc/group file", e);
        }
    }

    public static List<Shadow> parseShadow() {
        try (final FileInputStream inputStream = new FileInputStream(ETC_DIR + "/shadow")) {
            final String shadowFile = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final List<Shadow> result = new ArrayList<>();
            for (final String shadowInfo : shadowFile.split("\n")) {
                result.add(buildShadow(shadowInfo));
            }
            return result;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to parse /etc/shadow file", e);
        }
    }

    private static Shadow buildShadow(final String shadowInfo) {
        final String[] chunks = shadowInfo.split(":");
        if (chunks.length > 8 || chunks.length < 3) {
            throw new IllegalStateException("Bad shadow file string format");
        }

        return Shadow.builder()
                .username(chunks[0])
                .password(chunks[1])
                .lastChanged(Long.parseLong(chunks[2]))
                .minimum(chunks.length > 3 ? Integer.parseInt(chunks[3]) : null)
                .maximum(chunks.length > 4 ? Integer.parseInt(chunks[4]) : null)
                .warn(chunks.length > 5 ? Integer.parseInt(chunks[5]) : null)
                .inactive(chunks.length > 6 ? Integer.parseInt(chunks[6]) : null)
                .expire(chunks.length > 7 ? Long.parseLong(chunks[7]) : null)
                .build();
    }

    private static Group buildGroup(final String groupInfo) {
        final String[] chunks = groupInfo.split(":");
        if (chunks.length > 4 || chunks.length < 3) {
            throw new IllegalStateException("Bad group file string format");
        }

        List<String> groupList = new ArrayList<>();
        if (chunks.length == 4) {
            groupList.addAll(Arrays.asList(chunks[3].split(",")));
        }
        return Group.builder()
                .groupName(chunks[0])
                .password(chunks[1])
                .groupId(Integer.parseInt(chunks[2]))
                .groupList(groupList)
                .build();
    }
}
