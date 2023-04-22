package ru.github.dankharlushin.telegramui.model.source;

import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.source.SourceButton;

public record SourceCallbackButton(String text, CallbackQueryDto callback) implements SourceButton {
}
