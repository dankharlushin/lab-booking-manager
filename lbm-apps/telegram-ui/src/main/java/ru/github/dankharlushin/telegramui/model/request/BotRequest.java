package ru.github.dankharlushin.telegramui.model.request;

/**
 * Реализация должна иметь дефолтный конструктор
 */
public interface BotRequest {

    String getRequesterName();

    void setRequesterName(final String requesterName);
}
