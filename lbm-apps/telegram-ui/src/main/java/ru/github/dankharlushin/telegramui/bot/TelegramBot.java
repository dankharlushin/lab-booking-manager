package ru.github.dankharlushin.telegramui.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.github.dankharlushin.telegramui.auth.TelegramAuthManager;

import java.util.Collections;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramAuthManager telegramAuthManager;
    private final String botUsername;
    private final String botToken;

    public TelegramBot(final TelegramAuthManager telegramAuthManager,
                       @Value("${telegram-ui.bot.username}") final String botUsername,
                       @Value("${telegram-ui.bot.token}") final String botToken) {
        this.telegramAuthManager = telegramAuthManager;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (!update.hasMessage()) {
            logger.debug("Update has no message. Skip processing.");
            return;
        }

        final Long chatId = update.getMessage().getChatId();
        telegramAuthManager.authenticate(chatId).ifPresentOrElse(
                userInfo -> {},
                () -> sendAuthMessage(chatId)
        );
    }

    private void sendAuthMessage(final Long chatId) {
        final String authUrl = telegramAuthManager.generateAuthUrl(chatId);
        final SendMessage sendMessage = new SendMessage(chatId.toString(), "Please log in");
        final InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setUrl(authUrl);
        inlineKeyboardButton.setText("log in");
        final InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        replyMarkup.setKeyboard(Collections.singletonList(Collections.singletonList(inlineKeyboardButton)));
        sendMessage.setReplyMarkup(replyMarkup);
        tryExecute(sendMessage);
    }

    private void tryExecute(BotApiMethod<?> action) {
        try {
            execute(action);
        } catch (TelegramApiException e) {
            logger.error("Execution error", e);//fixme
        }
    }
}
