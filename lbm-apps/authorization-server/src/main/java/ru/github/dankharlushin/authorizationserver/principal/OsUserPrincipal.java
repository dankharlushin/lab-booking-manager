package ru.github.dankharlushin.authorizationserver.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.github.dankharlushin.lbmlib.data.entity.User;

import java.util.Collection;
import java.util.Collections;

public class OsUserPrincipal implements UserDetails {

    private final User labUser;

    public OsUserPrincipal(final User labUser) {
        this.labUser = labUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    @Override
    public String getPassword() {
        return labUser.getOsPassword();
    }

    @Override
    public String getUsername() {
        return labUser.getOsUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
