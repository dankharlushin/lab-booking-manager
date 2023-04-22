package ru.github.dankharlushin.authorizationserver.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.github.dankharlushin.authorizationserver.principal.OsUserPrincipal;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import java.io.IOException;
import java.time.Duration;

public class CustomRedirectAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomRedirectAuthenticationSuccessHandler.class);

    private final JedisClient jedisClient;
    private final RedirectStrategy redirectStrategy;
    private final Duration authSessionTimeout;
    private final String tgTokenSessionAttrName;
    private final String tgTokenParamName;
    private final String tgCallbackParamName;
    private final String tgCallbackSessionAttrName;

    public CustomRedirectAuthenticationSuccessHandler(final JedisClient jedisClient,
                                                      final RedirectStrategy redirectStrategy,
                                                      final Duration authSessionTimeout,
                                                      final String tgTokenSessionAttrName,
                                                      final String tgTokenParamName,
                                                      final String tgCallbackParamName,
                                                      final String tgCallbackSessionAttrName) {
        this.jedisClient = jedisClient;
        this.redirectStrategy = redirectStrategy;
        this.authSessionTimeout = authSessionTimeout;
        this.tgTokenSessionAttrName = tgTokenSessionAttrName;
        this.tgTokenParamName = tgTokenParamName;
        this.tgCallbackParamName = tgCallbackParamName;
        this.tgCallbackSessionAttrName = tgCallbackSessionAttrName;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(tgTokenSessionAttrName) != null) {
            final String chatId = (String) session.getAttribute(tgTokenSessionAttrName);
            final String callbackUrl = (String) session.getAttribute(tgCallbackSessionAttrName);
            final OsUserPrincipal user = (OsUserPrincipal) authentication.getPrincipal();
            jedisClient.setWithTtl(chatId, new TelegramAuthInfo(user.getUsername()), authSessionTimeout.getSeconds());

            redirectStrategy.sendRedirect(request, response, callbackUrl);
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            session.removeAttribute(tgTokenSessionAttrName);
            session.removeAttribute(tgTokenParamName);
            session.removeAttribute(tgCallbackSessionAttrName);
            session.removeAttribute(tgCallbackParamName);
            logger.debug("Authentication success for session [{}]", session.getId());
        } else {
            logger.warn("Can't get telegram token param from session");
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
