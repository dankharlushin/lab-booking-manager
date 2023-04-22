package ru.github.dankharlushin.telegramui.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class LabBookingBotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(LabBookingBotInitializer.class);

    private final LabBookingBot bot;

    public LabBookingBotInitializer(final LabBookingBot bot) {
        this.bot = bot;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("Can not initialize bot", e);
        }
    }
}
