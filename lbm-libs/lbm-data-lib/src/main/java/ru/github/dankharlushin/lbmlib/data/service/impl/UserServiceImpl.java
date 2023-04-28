package ru.github.dankharlushin.lbmlib.data.service.impl;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.repository.UserRepository;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByOsUsernameName(final String osUsernameName) {
        return userRepository.findUserByOsUsername(osUsernameName);
    }

    @Override
    public User findByChatId(final long chatId) {
        return userRepository.findUserByChatId(chatId);
    }

    @Override
    public void updateChatId(final @NonNull User user, @Nullable final Long newChatId) {
        user.setChatId(newChatId);
        userRepository.save(user);
    }
}
