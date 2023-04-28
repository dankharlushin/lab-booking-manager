package ru.github.dankharlushin.telegramui.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.auth.TelegramAuthManager;
import ru.github.dankharlushin.telegramui.handler.UpdateHandlerManager;
import ru.github.dankharlushin.telegramui.model.BotActionWrapper;

@Component
public class LabBookingBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(LabBookingBot.class);

    private final TelegramAuthManager telegramAuthManager;
    private final UpdateHandlerManager updateHandlerManager;
    private final String botUsername;
    private final String botToken;

    public LabBookingBot(final TelegramAuthManager telegramAuthManager,
                         final UpdateHandlerManager updateHandlerManager,
                         @Value("${telegram-ui.bot.username}") final String botUsername,
                         @Value("${telegram-ui.bot.token}") final String botToken) {
        this.telegramAuthManager = telegramAuthManager;
        this.updateHandlerManager = updateHandlerManager;
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
        final Long chatId = getChatId(update);
        if (chatId == null) {
            logger.debug("Received update without message and callback");
            return;
        }

        telegramAuthManager.findAuthInfo(chatId).ifPresentOrElse(
                userInfo -> processUpdate(update, userInfo, chatId),
                () -> tryExecute(telegramAuthManager.createAuthMessage(chatId))
        );
    }

    @EventListener
    public void onEventReceived(BotActionWrapper actionWrapper) {
        logger.info("Executing event [{}], chatId [{}]", actionWrapper.action(), actionWrapper.chatId());
        tryExecute(actionWrapper.action());
    }

    private void processUpdate(final Update update, final TelegramAuthInfo userInfo, final Long chatId) {
        telegramAuthManager.prolongAuth(chatId);
        updateHandlerManager.findHandler(update).handle(update, userInfo).forEach(this::tryExecute);
    }

    private Long getChatId(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else {
            return null;
        }
    }

    private void tryExecute(BotApiMethod<?> action) {
        try {
            execute(action);
        } catch (TelegramApiException e) {
            logger.error("Execution error", e);
        }
    }
}
