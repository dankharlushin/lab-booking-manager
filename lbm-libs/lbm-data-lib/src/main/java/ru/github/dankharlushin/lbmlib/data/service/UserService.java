package ru.github.dankharlushin.lbmlib.data.service;

import ru.github.dankharlushin.lbmlib.data.entity.User;

public interface UserService {

    User getByOsUsernameName(final String osName);
}
