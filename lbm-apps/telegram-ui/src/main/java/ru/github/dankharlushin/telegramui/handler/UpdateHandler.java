package ru.github.dankharlushin.telegramui.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import java.util.List;

public interface UpdateHandler {

    List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo);
}
