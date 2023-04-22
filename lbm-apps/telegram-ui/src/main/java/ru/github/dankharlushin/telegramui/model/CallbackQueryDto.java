package ru.github.dankharlushin.telegramui.model;

import java.io.Serializable;
import java.util.Map;

public record CallbackQueryDto(String id, boolean init, Map<String, ?> data) implements Serializable {
}
