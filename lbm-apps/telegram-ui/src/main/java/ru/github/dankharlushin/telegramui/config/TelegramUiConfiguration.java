package ru.github.dankharlushin.telegramui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.client.RestTemplate;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.config.JedisConfig;

import java.util.Locale;

@Configuration
@Import({DataLibConfig.class, JedisConfig.class})
public class TelegramUiConfiguration {

    @Bean
    public MessageSourceAccessor messageSourceAccessor(final MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
