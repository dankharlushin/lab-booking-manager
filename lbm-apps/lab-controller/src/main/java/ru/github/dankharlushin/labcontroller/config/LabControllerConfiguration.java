package ru.github.dankharlushin.labcontroller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.shell.config.ShellLibConfig;

@Configuration
@Import({DataLibConfig.class, ShellLibConfig.class})
public class LabControllerConfiguration {
}
