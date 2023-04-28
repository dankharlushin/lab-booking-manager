package ru.github.dankharlushin.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;

@Configuration
@EnableScheduling
@Import({DataLibConfig.class})
public class NotificationServiceConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
