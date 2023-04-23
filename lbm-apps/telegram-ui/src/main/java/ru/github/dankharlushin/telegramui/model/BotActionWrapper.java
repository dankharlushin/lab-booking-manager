package ru.github.dankharlushin.telegramui.model;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public record BotActionWrapper(BotApiMethod<?> action, long chatId) {
}
