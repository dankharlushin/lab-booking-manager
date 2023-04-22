package ru.github.dankharlushin.telegramui.handler;

public interface CommandHandler extends UpdateHandler {

    String getTextMessage();
}
