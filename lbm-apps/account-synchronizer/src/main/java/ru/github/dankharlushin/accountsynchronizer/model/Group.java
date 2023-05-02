package ru.github.dankharlushin.accountsynchronizer.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Group(
        String groupName,
        String password,
        int groupId,
        List<String> groupList
) {
}
