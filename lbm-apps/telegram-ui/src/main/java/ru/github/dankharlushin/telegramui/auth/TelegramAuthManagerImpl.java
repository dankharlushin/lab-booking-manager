package ru.github.dankharlushin.telegramui.auth;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.model.SourceLinkButton;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.SourceButtonService;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static ru.github.dankharlushin.telegramui.util.KeyboardUtil.inlineKeyboard;

@Component
public class TelegramAuthManagerImpl implements TelegramAuthManager {

    private static final String AUTHORIZATION_MESSAGE_CODE = "authorizationMessage";
    private static final String AUTHORIZATION_BUTTON_TEXT_CODE = "authorizationButtonText";

    private final JedisClient jedisClient;
    private final BotMessageService messageService;
    private final SourceButtonService buttonService;
    private final String authServerBaseUrl;
    private final String tokenParamName;
    private final String publicKeyPath;
    private final String algorithmType;

    public TelegramAuthManagerImpl(final JedisClient jedisClient,
                                   final BotMessageService messageService,
                                   final SourceButtonService buttonService,
                                   @Value("${telegram-ui.auth.server.base-url}") final String authServerBaseUrl,
                                   @Value("${telegram-ui.auth.token.param-name}") final String tokenParamName,
                                   @Value("${telegram-ui.auth.public-key}") final String publicKeyPath,
                                   @Value("${telegram-ui.auth.algorithm-type}") final String algorithmType) {
        this.messageService = messageService;
        this.buttonService = buttonService;
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
    public SendMessage createAuthMessage(final long chatId) {
        final String authUrl = generateAuthUrl(chatId);

        final SourceLinkButton sourceLinkButton = buttonService.createLinkButton(AUTHORIZATION_BUTTON_TEXT_CODE, authUrl);
        final InlineKeyboardMarkup keyboardMarkup = inlineKeyboard(Collections.singletonList(sourceLinkButton), 1);

        return messageService.createReplyMessage(chatId, AUTHORIZATION_MESSAGE_CODE, keyboardMarkup);
    }

    private String generateAuthUrl(final long chatId) {
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
