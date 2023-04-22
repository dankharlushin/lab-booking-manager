package ru.github.dankharlushin.authorizationserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class TelegramAuthFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(TelegramAuthFilter.class);

    private final JedisClient jedisClient;
    private final String tgTokenParamName;
    private final String tgTokenSessionAttrName;
    private final String tgCallbackParamName;
    private final String tgCallbackSessionAttrName;
    private final String tgPrivateKeyPath;
    private final String tgAlgorithmType;
    private final String defaultCallbackUrl;

    public TelegramAuthFilter(final JedisClient jedisClient,
                              final String tgTokenParamName,
                              final String tgTokenSessionAttrName,
                              final String tgCallbackParamName,
                              final String tgCallbackSessionAttrName,
                              final String tgPrivateKeyPath,
                              final String tgAlgorithmType,
                              final String defaultCallbackUrl) {
        this.jedisClient = jedisClient;
        this.tgTokenParamName = tgTokenParamName;
        this.tgTokenSessionAttrName = tgTokenSessionAttrName;
        this.tgCallbackParamName = tgCallbackParamName;
        this.tgCallbackSessionAttrName = tgCallbackSessionAttrName;
        this.tgPrivateKeyPath = tgPrivateKeyPath;
        this.tgAlgorithmType = tgAlgorithmType;
        this.defaultCallbackUrl = defaultCallbackUrl;
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws ServletException, IOException {
        final String encodedB64ChatId = request.getParameter(tgTokenParamName);
        if (encodedB64ChatId != null) {
            final String chatId = decodeParameter(encodedB64ChatId);
            processChatId(request, response, chain, encodedB64ChatId, chatId);
        } else {
            logger.error("Telegram token param is null");
            constructNotFoundResponse((HttpServletResponse) response);
        }
    }

    private void processChatId(final ServletRequest request,
                               final ServletResponse response,
                               final FilterChain chain,
                               final String encodedB64ChatId,
                               final String chatId) throws IOException, ServletException {
        if (!hasActiveSession(chatId)) {
            final HttpServletRequest req = (HttpServletRequest) request;
            final HttpSession session = req.getSession(true);
            session.setAttribute(tgTokenSessionAttrName, chatId);
            session.setAttribute(tgTokenParamName, encodedB64ChatId);
            initCallback(request, session);

            chain.doFilter(request, response);
        } else {
            constructNotFoundResponse((HttpServletResponse) response);
        }
    }

    private void initCallback(final ServletRequest request, final HttpSession session) {
        final String encodedB64CallbackUrl = request.getParameter(tgCallbackParamName);
        if (encodedB64CallbackUrl != null) {
            final String callbackUrl = decodeParameter(encodedB64CallbackUrl);
            session.setAttribute(tgCallbackSessionAttrName, callbackUrl);
            session.setAttribute(tgCallbackParamName, encodedB64CallbackUrl);
        } else {
            logger.warn("Callback url is null");
            session.setAttribute(tgCallbackSessionAttrName, defaultCallbackUrl);
        }
    }

    private String decodeParameter(final String encodedB64Parameter) {
        try {
            File privateKeyFile = new File(tgPrivateKeyPath);
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            final KeyFactory keyFactory = KeyFactory.getInstance(tgAlgorithmType);
            final PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Cipher decryptCipher = Cipher.getInstance(tgAlgorithmType);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            final byte[] decodedBytesChatId = decryptCipher.doFinal(Base64.getDecoder().decode(encodedB64Parameter));
            return new String(decodedBytesChatId);
        } catch (final Exception e) {
            throw new IllegalStateException("Unable to decode tg auth parameter", e);
        }
    }

    private void constructNotFoundResponse(final HttpServletResponse response) {
        response.reset();
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private boolean hasActiveSession(final String chatId) {
        return jedisClient.exists(chatId);
    }
}
