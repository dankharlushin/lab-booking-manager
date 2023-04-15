package ru.github.dankharlushin.authorizationserver.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomRedirectAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomRedirectAuthenticationFailureHandler.class);

    private final RedirectStrategy redirectStrategy;
    private final String tgTokenSessionAttrName;
    private final String tgTokenParamName;

    public CustomRedirectAuthenticationFailureHandler(final RedirectStrategy redirectStrategy,
                                                      final String tgTokenSessionAttrName,
                                                      final String tgTokenParamName) {
        this.redirectStrategy = redirectStrategy;
        this.tgTokenSessionAttrName = tgTokenSessionAttrName;
        this.tgTokenParamName = tgTokenParamName;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException {
        final HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(tgTokenParamName) != null) {
            logger.debug("Authentication failure for session [{}]", session.getId());
            session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
            final String chatId = (String) session.getAttribute(tgTokenParamName);
            redirectStrategy.sendRedirect(request, response,
                    String.format("/login?error&%s=%s", tgTokenParamName, URLEncoder.encode(chatId, StandardCharsets.UTF_8)));

            session.removeAttribute(tgTokenSessionAttrName);
            session.removeAttribute(tgTokenParamName);
        } else {
            logger.warn("Can't get telegram token param from session");
            response.reset();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
