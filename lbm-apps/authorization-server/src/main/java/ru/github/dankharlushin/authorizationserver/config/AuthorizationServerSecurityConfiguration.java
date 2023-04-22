package ru.github.dankharlushin.authorizationserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import ru.github.dankharlushin.authorizationserver.filter.TelegramAuthFilter;
import ru.github.dankharlushin.authorizationserver.handler.CustomRedirectAuthenticationFailureHandler;
import ru.github.dankharlushin.authorizationserver.handler.CustomRedirectAuthenticationSuccessHandler;
import ru.github.dankharlushin.authorizationserver.service.OsUserDetailsService;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.config.JedisConfig;
import ru.github.dankharlushin.lbmlib.data.repository.UserRepository;

import java.time.Duration;

@Configuration
@EnableWebSecurity
@Import({DataLibConfig.class, JedisConfig.class})
public class AuthorizationServerSecurityConfiguration {

    private final JedisClient jedisClient;
    private final UserRepository userRepository;

    private final Duration authSessionTimeout;
    private final String tgTokenParamName;
    private final String tgTokenSessionAttrName;
    private final String tgCallbackParamName;
    private final String tgCallbackSessionAttrName;
    private final String tgPrivateKeyPath;
    private final String tgAlgorithmType;
    private final String defaultCallbackUrl;

    public AuthorizationServerSecurityConfiguration(final JedisClient jedisClient,
                                                    final UserRepository userRepository,
                                                    @Value("${server.servlet.session.timeout}")
                                                    final Duration authSessionTimeout,
                                                    @Value("${authorization-server.telegram-ui.auth.token.session-attribute-name}")
                                                    final String tgTokenSessionAttrName,
                                                    @Value("${telegram-ui.auth.token.param-name}")
                                                    final String tgTokenParamName,
                                                    @Value("${telegram-ui.auth.callback-url.param-name}")
                                                    final String tgCallbackParamName,
                                                    @Value("${authorization-server.telegram-ui.auth.callback-url.session-attribute-name}")
                                                    final String tgCallbackSessionAttrName,
                                                    @Value("${telegram-ui.auth.private-key}")
                                                    final String tgPrivateKeyPath,
                                                    @Value("${telegram-ui.auth.algorithm-type}")
                                                    final String tgAlgorithmType,
                                                    @Value("${authorization-server.default.redirect-url}")
                                                    final String defaultCallbackUrl) {
        this.jedisClient = jedisClient;
        this.userRepository = userRepository;
        this.authSessionTimeout = authSessionTimeout;
        this.tgTokenParamName = tgTokenParamName;
        this.tgTokenSessionAttrName = tgTokenSessionAttrName;
        this.tgCallbackParamName = tgCallbackParamName;
        this.tgCallbackSessionAttrName = tgCallbackSessionAttrName;
        this.tgPrivateKeyPath = tgPrivateKeyPath;
        this.tgAlgorithmType = tgAlgorithmType;
        this.defaultCallbackUrl = defaultCallbackUrl;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(telegramAuthFilter(), BasicAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                )
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling();
        return http.build();
    }

    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(osUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomRedirectAuthenticationSuccessHandler(jedisClient,
                new DefaultRedirectStrategy(),
                authSessionTimeout,
                tgTokenSessionAttrName,
                tgTokenParamName, tgCallbackParamName, tgCallbackSessionAttrName);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomRedirectAuthenticationFailureHandler(new DefaultRedirectStrategy(),
                tgTokenSessionAttrName,
                tgTokenParamName,
                tgCallbackParamName,
                tgCallbackSessionAttrName);
    }

    @Bean
    public TelegramAuthFilter telegramAuthFilter() {
        return new TelegramAuthFilter(jedisClient,
                tgTokenParamName,
                tgTokenSessionAttrName,
                tgCallbackParamName,
                tgCallbackSessionAttrName,
                tgPrivateKeyPath,
                tgAlgorithmType,
                defaultCallbackUrl);
    }

    @Bean
    public HttpFirewall configureFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowUrlEncodedDoubleSlash(true);
        return strictHttpFirewall;
    }

    @Bean
    public UserDetailsService osUserDetailsService() {
        return new OsUserDetailsService(userRepository);
    }
}
