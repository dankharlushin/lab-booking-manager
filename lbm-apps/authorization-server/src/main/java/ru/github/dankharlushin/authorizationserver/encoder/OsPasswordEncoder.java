package ru.github.dankharlushin.authorizationserver.encoder;

import org.apache.commons.codec.digest.Crypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OsPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(final CharSequence rawPassword) {
        return Crypt.crypt(rawPassword.toString());
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        final String salt = encodedPassword.substring(0, encodedPassword.lastIndexOf("$"));
        final String encodedInput = Crypt.crypt(rawPassword.toString().getBytes(), salt);
        return encodedPassword.equals(encodedInput);
    }
}
