package ru.github.dankharlushin.lbmlib.data.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
class JedisClientTest {

    private static final int REDIS_PORT = 6379;
    private static final int MAX_TOTAL = 2;

    private static JedisClient jedisClient;

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6.2.6-alpine"))
            .withExposedPorts(REDIS_PORT);

    @BeforeAll
    static void beforeAll() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(MAX_TOTAL);
        final JedisPool jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getFirstMappedPort());
        jedisClient = new JedisClient(jedisPool, new ObjectMapper());
    }

    @Test
    void testGet() {
        final String key = UUID.randomUUID().toString();
        final TelegramAuthInfo user = new TelegramAuthInfo("user");
        jedisClient.set(key, user);


        TelegramAuthInfo retrieved = jedisClient.get(key, TelegramAuthInfo.class);
        assertThat(retrieved, notNullValue());
        assertThat(retrieved.osUsername(), is("user"));

        retrieved = jedisClient.get("non", TelegramAuthInfo.class);
        assertThat(retrieved, nullValue());
    }

    @Test
    void testExists() {
        final String key = UUID.randomUUID().toString();
        final TelegramAuthInfo user = new TelegramAuthInfo("user");
        jedisClient.set(key, user);

        boolean exists = jedisClient.exists(key);
        assertThat(exists, is(true));

        exists = jedisClient.exists("non");
        assertThat(exists, is(false));
    }

    @Test
    void testDel() {
        final String key = UUID.randomUUID().toString();
        final TelegramAuthInfo user = new TelegramAuthInfo("user");
        jedisClient.set(key, user);
        boolean exists = jedisClient.exists(key);
        assertThat(exists, is(true));

        long retVal = jedisClient.del(key);
        assertThat(retVal, is(1L));
        exists = jedisClient.exists(key);
        assertThat(exists, is(false));

        retVal = jedisClient.del("non");
        assertThat(retVal, is(0L));
    }
}
