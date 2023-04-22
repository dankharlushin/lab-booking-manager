package ru.github.dankharlushin.bookingservice.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.client.RestTemplate;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;

import java.util.Locale;

@Configuration
@Import({DataLibConfig.class})
public class BookingServiceConfiguration {

    @Bean
    public MessageSourceAccessor messageSourceAccessor(final MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
