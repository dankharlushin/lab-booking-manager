package ru.github.dankharlushin.telegramui.handler.impl.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.CommandHandler;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class StartCommandHandler implements CommandHandler {

    private static final String COMMAND = "/start";
    private static final String START_MESSAGE_CODE = "startMessage";

    private final BotMessageService messageService;

    public StartCommandHandler(final BotMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String getTextMessage() {
        return COMMAND;
    }

    @Override
    public List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo) {
        return singletonList(messageService.createReplyMessage(update.getMessage().getChatId(), START_MESSAGE_CODE));
    }
}
