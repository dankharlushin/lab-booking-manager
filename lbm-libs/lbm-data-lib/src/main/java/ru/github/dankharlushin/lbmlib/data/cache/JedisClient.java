package ru.github.dankharlushin.lbmlib.data.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class JedisClient {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    public JedisClient(final JedisPool jedisPool, final ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    public <T> T get(final String key, final Class<T> valueType) {
        try (Jedis jedis = jedisPool.getResource()) {
            final String json = jedis.get(key);
            if (json != null)
                return objectMapper.readValue(json, valueType);
            return null;
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void set(final String key, final Object value) {
        try (final Jedis jedis = jedisPool.getResource()) {
            final String json = objectMapper.writeValueAsString(value);
            jedis.set(key, json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setWithTtl(final String key, final Object value, final long secondsTtl) {
        try (final Jedis jedis = jedisPool.getResource()) {
            final String json = objectMapper.writeValueAsString(value);
            final SetParams params = SetParams.setParams().ex(secondsTtl);
            jedis.set(key, json, params);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean exists(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    public long del(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }
}
