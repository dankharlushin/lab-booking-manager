package ru.github.dankharlushin.lbmlib.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.github.dankharlushin.lbmlib.data.config.JedisConfig;

@SpringBootApplication(exclude = JedisConfig.class)
public class TestApplication {
}
