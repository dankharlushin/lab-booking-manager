package ru.github.dankharlushin.telegramui.auth;

import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import java.util.Optional;

public interface TelegramAuthManager {

    Optional<TelegramAuthInfo> authenticate(final long chatId);

    String generateAuthUrl(final long chatId);
}
