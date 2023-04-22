package ru.github.dankharlushin.lbmlib.data.service.impl;

import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.repository.UserRepository;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getByOsUsernameName(final String osUsernameName) {
        return userRepository.findUserByOsUsername(osUsernameName);
    }
}
