package ru.github.dankharlushin.telegramui.auth;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import java.util.Optional;

public interface TelegramAuthManager {

    Optional<TelegramAuthInfo> findAuthInfo(final long chatId);

    SendMessage createAuthMessage(final long chatId);

    void prolongAuth(final long chatId);
}
