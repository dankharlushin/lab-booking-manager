package ru.github.dankharlushin.telegramui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;

@Configuration
@Import({DataLibConfig.class})
public class TelegramUiConfiguration {
}
