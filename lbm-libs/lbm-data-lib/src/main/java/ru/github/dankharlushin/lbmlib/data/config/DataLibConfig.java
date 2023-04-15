package ru.github.dankharlushin.lbmlib.data.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;

@Configuration
@EntityScan(basePackages = "ru.github.dankharlushin.lbmlib.data.entity")
@EnableJpaRepositories(basePackages = "ru.github.dankharlushin.lbmlib.data.repository")
@ComponentScan("ru.github.dankharlushin.lbmlib.data")
public class DataLibConfig {

    @Value("${libs.data.redis.host}")
    private String redisHost;
    @Value("${libs.data.redis.port}")
    private Integer redisPort;
    @Value("${libs.data.redis.max-total}")
    private Integer maxTotal;

    @Bean
    public JedisClient jedisClient() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        final JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort);
        return new JedisClient(jedisPool, new ObjectMapper());
    }
}
