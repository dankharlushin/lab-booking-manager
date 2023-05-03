package ru.github.dankharlushin.lbmlib.data.service;

import org.springframework.data.domain.Slice;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.github.dankharlushin.lbmlib.data.entity.User;

public interface UserService {

    User findByOsUsernameName(final String osName);

    User findByChatId(final long chatId);

    boolean existsByOsUsername(final String osUsername);

    Slice<User> findAll();

    void save(final User user);

    void deleteAllById(Iterable<Integer> ids);

    void deleteByOsUsername(final String osUsername);

    void updateChatId(@NonNull final User user, @Nullable final Long newChatId);
}
