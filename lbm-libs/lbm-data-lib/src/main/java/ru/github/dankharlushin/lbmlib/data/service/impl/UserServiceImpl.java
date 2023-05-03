package ru.github.dankharlushin.lbmlib.data.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.repository.UserRepository;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Integer usersBatchSize;

    public UserServiceImpl(final UserRepository userRepository,
                           @Value("${libs.data.service.users.batch-size:20}") final Integer usersBatchSize) {
        this.userRepository = userRepository;
        this.usersBatchSize = usersBatchSize;
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
    public boolean existsByOsUsername(final String osUsername) {
        return userRepository.existsByOsUsername(osUsername);
    }

    @Override
    public Slice<User> findAll() {
        return userRepository.findAll(PageRequest.of(0, usersBatchSize));
    }

    @Override
    public void save(final User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteAllById(final Iterable<Integer> ids) {
        userRepository.deleteAllById(ids);
    }

    @Override
    public void deleteByOsUsername(final String osUsername) {
        userRepository.deleteByOsUsername(osUsername);
    }

    @Override
    public void updateChatId(final @NonNull User user, @Nullable final Long newChatId) {
        user.setChatId(newChatId);
        userRepository.save(user);
    }
}
