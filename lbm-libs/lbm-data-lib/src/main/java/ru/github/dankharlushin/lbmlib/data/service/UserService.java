package ru.github.dankharlushin.lbmlib.data.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.github.dankharlushin.lbmlib.data.entity.User;

public interface UserService {

    User findByOsUsernameName(final String osName);

    User findByChatId(final long chatId);

    void updateChatId(@NonNull final User user, @Nullable final Long newChatId);
}
