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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CustomRedirectAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomRedirectAuthenticationFailureHandler.class);

    private final RedirectStrategy redirectStrategy;
    private final String tgTokenSessionAttrName;
    private final String tgTokenParamName;
    private final String tgCallbackParamName;
    private final String tgCallbackSessionAttrName;

    public CustomRedirectAuthenticationFailureHandler(final RedirectStrategy redirectStrategy,
                                                      final String tgTokenSessionAttrName,
                                                      final String tgTokenParamName,
                                                      final String tgCallbackParamName,
                                                      final String tgCallbackSessionAttrName) {
        this.redirectStrategy = redirectStrategy;
        this.tgTokenSessionAttrName = tgTokenSessionAttrName;
        this.tgTokenParamName = tgTokenParamName;
        this.tgCallbackParamName = tgCallbackParamName;
        this.tgCallbackSessionAttrName = tgCallbackSessionAttrName;
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
            final String callbackUrl = (String) session.getAttribute(tgCallbackParamName);

            final UriComponentsBuilder redirectBuilder = UriComponentsBuilder.fromPath("/login");
            redirectBuilder.queryParam("error", Collections.emptySet());
            redirectBuilder.queryParam(tgTokenParamName, URLEncoder.encode(chatId, UTF_8));
            if (callbackUrl != null) { redirectBuilder.queryParam(tgCallbackParamName, URLEncoder.encode(callbackUrl, UTF_8)); }
            redirectStrategy.sendRedirect(request, response, redirectBuilder.build(true).toString());

            session.removeAttribute(tgTokenSessionAttrName);
            session.removeAttribute(tgTokenParamName);
            session.removeAttribute(tgCallbackSessionAttrName);
            session.removeAttribute(tgCallbackParamName);
        } else {
            logger.warn("Can't get telegram token param from session");
            response.reset();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
