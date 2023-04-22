package ru.github.dankharlushin.telegramui.handler.impl.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.CommandHandler;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.Collections;
import java.util.List;

@Component
public class LogoutCommandHandler implements CommandHandler {

    private static final String COMMAND = "/logout";
    private static final String SUCCESS_LOGOUT_MESSAGE_CODE = "successLogoutMessage";

    private final JedisClient jedisClient;
    private final BotMessageService messageService;

    public LogoutCommandHandler(final JedisClient jedisClient,
                                final BotMessageService messageService) {
        this.jedisClient = jedisClient;
        this.messageService = messageService;
    }

    @Override
    public String getTextMessage() {
        return COMMAND;
    }

    @Override
    public List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo) {
        final long chatId = update.getMessage().getChatId();
        if (jedisClient.exists(String.valueOf(chatId))) {
            jedisClient.del(String.valueOf(chatId));
        }

        return Collections.singletonList(messageService.createReplyMessage(chatId, SUCCESS_LOGOUT_MESSAGE_CODE));
    }
}
