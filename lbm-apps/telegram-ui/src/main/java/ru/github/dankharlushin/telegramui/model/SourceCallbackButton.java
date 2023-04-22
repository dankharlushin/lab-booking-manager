package ru.github.dankharlushin.telegramui.model;

public record SourceCallbackButton(String text, CallbackQueryDto callback) implements SourceButton {
}
