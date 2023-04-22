package ru.github.dankharlushin.telegramui.service;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Service
public class BotMessageService {

    private final MessageSourceAccessor sourceAccessor;

    public BotMessageService(final MessageSourceAccessor sourceAccessor) {
        this.sourceAccessor = sourceAccessor;
    }

    public SendMessage createReplyMessage(final long chatId, final String messageCode) {
        return createReplyMessage(chatId, messageCode, null, (Object) null);
    }

    public SendMessage createReplyMessage(final long chatId, final String messageCode, final ReplyKeyboard replyKeyboard) {
        return createReplyMessage(chatId, messageCode, replyKeyboard, (Object) null);
    }

    public SendMessage createReplyMessage(final long chatId,
                                          final String messageCode,
                                          final ReplyKeyboard replyKeyboard,
                                          final Object... args) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(EmojiParser.parseToUnicode(sourceAccessor.getMessage(messageCode, args)))
                .replyMarkup(replyKeyboard)
                .build();
    }

    public EditMessageText createEditMessage(final long chatId,
                                             final int messageId,
                                             final String messageCode) {
        return createEditMessage(chatId, messageId, messageCode, null, (Object) null);
    }

    public EditMessageText createEditMessage(final long chatId,
                                             final int messageId,
                                             final String messageCode,
                                             final InlineKeyboardMarkup inlineKeyboardMarkup) {
        return createEditMessage(chatId, messageId, messageCode, inlineKeyboardMarkup, (Object) null);
    }

    public EditMessageText createEditMessage(final long chatId,
                                             final int messageId,
                                             final String messageCode,
                                             final InlineKeyboardMarkup inlineKeyboardMarkup,
                                             final Object... args) {
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(EmojiParser.parseToUnicode(sourceAccessor.getMessage(messageCode, args)))
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
}
