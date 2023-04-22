package ru.github.dankharlushin.lbmlib.data.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;

@Configuration
public class JedisConfig {

    @Value("${libs.data.redis.host:localhost}")
    private String redisHost;
    @Value("${libs.data.redis.port:6379}")
    private Integer redisPort;
    @Value("${libs.data.redis.max-total:8}")
    private Integer maxTotal;

    @Bean
    public JedisClient jedisClient() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        final JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort);
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new JedisClient(jedisPool, objectMapper);
    }
}
