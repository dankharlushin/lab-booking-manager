package ru.github.dankharlushin.lbmlib.data.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.github.dankharlushin.lbmlib.data.entity.User;

import java.util.stream.Stream;

public interface UserService {

    User findByOsUsernameName(final String osName);

    User findByChatId(final long chatId);

    boolean existsByOsUsername(final String osUsername);

    Stream<User> findAll();

    void save(final User user);

    void deleteByOsUsername(final String osUsername);

    void updateChatId(@NonNull final User user, @Nullable final Long newChatId);
}
