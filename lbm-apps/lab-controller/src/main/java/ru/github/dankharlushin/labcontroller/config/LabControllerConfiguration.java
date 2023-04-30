package ru.github.dankharlushin.labcontroller.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.shell.config.ShellLibConfig;

import java.util.Locale;

@Configuration
@EnableScheduling
@Import({DataLibConfig.class, ShellLibConfig.class})
public class LabControllerConfiguration {

    @Bean
    public MessageSourceAccessor messageSourceAccessor(final MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.getDefault());
    }
}
