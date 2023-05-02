package ru.github.dankharlushin.accountsynchronizer.model;

import lombok.Builder;

@Builder
public record Shadow(
        String username,
        String password,
        long lastChanged,
        Integer minimum,
        Integer maximum,
        Integer warn,
        Integer inactive,
        Long expire
) {
}
