package ru.github.dankharlushin.authorizationserver.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.github.dankharlushin.authorizationserver.principal.OsUserPrincipal;
import ru.github.dankharlushin.lbmlib.data.entity.User;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

public class OsUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public OsUserDetailsService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userService.findByOsUsernameName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new OsUserPrincipal(user);
    }
}
