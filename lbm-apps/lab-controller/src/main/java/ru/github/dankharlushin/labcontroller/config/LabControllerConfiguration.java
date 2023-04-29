package ru.github.dankharlushin.labcontroller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.shell.config.ShellLibConfig;

@Configuration
@EnableScheduling
@Import({DataLibConfig.class, ShellLibConfig.class})
public class LabControllerConfiguration {
}
