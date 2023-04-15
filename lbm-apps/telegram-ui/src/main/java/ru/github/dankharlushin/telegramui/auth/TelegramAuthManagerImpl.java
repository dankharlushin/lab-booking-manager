package ru.github.dankharlushin.telegramui.auth;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Component
public class TelegramAuthManagerImpl implements TelegramAuthManager {

    private final JedisClient jedisClient;
    private final String authServerBaseUrl;
    private final String tokenParamName;
    private final String publicKeyPath;
    private final String algorithmType;

    public TelegramAuthManagerImpl(final JedisClient jedisClient,
                                   @Value("${telegram-ui.auth.server.base-url}") final String authServerBaseUrl,
                                   @Value("${telegram-ui.auth.token.param-name}") final String tokenParamName,
                                   @Value("${telegram-ui.auth.public-key}") final String publicKeyPath,
                                   @Value("${telegram-ui.auth.algorithm-type}") final String algorithmType) {
        this.authServerBaseUrl = authServerBaseUrl;
        this.jedisClient = jedisClient;
        this.tokenParamName = tokenParamName;
        this.publicKeyPath = publicKeyPath;
        this.algorithmType = algorithmType;
    }

    @Override
    public Optional<TelegramAuthInfo> authenticate(final long chatId) {
        return Optional.ofNullable(jedisClient.get(String.valueOf(chatId), TelegramAuthInfo.class));
    }

    @Override
    public String generateAuthUrl(final long chatId) {
        try {
            File publicKeyFile = new File(publicKeyPath);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithmType);
            final PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Cipher encryptCipher = Cipher.getInstance(algorithmType);
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            final byte[] encodedBytesChatId = encryptCipher.doFinal(String.valueOf(chatId).getBytes());
            final String encodedChatId = Base64.getEncoder().encodeToString(encodedBytesChatId);

            final URIBuilder uriBuilder = new URIBuilder(authServerBaseUrl);
            uriBuilder.addParameter(tokenParamName, encodedChatId);
            return uriBuilder.build().toString();
        } catch (final Exception e) {
            throw new IllegalStateException("Can't generate auth url", e);
        }
    }
}
