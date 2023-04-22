package ru.github.dankharlushin.telegramui.model.source;

import ru.github.dankharlushin.telegramui.model.source.SourceButton;

public record SourceLinkButton(String text, String url) implements SourceButton {
}
