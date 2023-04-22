package ru.github.dankharlushin.telegramui.handler.impl;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.UpdateHandler;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.List;

import static java.util.Collections.singletonList;

public class DefaultUpdateHandler implements UpdateHandler {

    private static final String DEFAULT_MESSAGE_CODE = "defaultMessage";

    private final BotMessageService messageService;
    private final String messageCode;

    public DefaultUpdateHandler(final BotMessageService messageService, final String messageCode) {
        this.messageService = messageService;
        this.messageCode = messageCode == null ? DEFAULT_MESSAGE_CODE : messageCode;
    }

    @Override
    public List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo) {
        final long chatId = update.hasMessage() ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId();

        return singletonList(messageService.createReplyMessage(chatId, messageCode));
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
}
