package ru.github.dankharlushin.telegramui.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.telegramui.model.request.BotRequest;

import java.time.Duration;
import java.util.Objects;

@Service
public class BotRequestCacheService {

    private final JedisClient jedisClient;
    private final Duration ttl;

    public BotRequestCacheService(final JedisClient jedisClient,
                                  @Value("${telegram-ui.booking.request.cache.ttl}") final String ttl) {
        this.jedisClient = jedisClient;
        this.ttl = Duration.parse(ttl);
    }

    public <T extends BotRequest> boolean requestExists(final int messageId, final long chatId, Class<T> requestType) {
        return jedisClient.exists(toKey(messageId, chatId, requestType));
    }

    public <T extends BotRequest> T getRequest(final int messageId, final long chatId, Class<T> requestType) {
        return jedisClient.get(toKey(messageId, chatId, requestType), requestType);
    }

    public <T extends BotRequest> void setRequest(final int messageId, final long chatId, T request) {
        jedisClient.setWithTtl(toKey(messageId, chatId, request.getClass()),
                Objects.requireNonNull(request, "Request must not be null"),
                ttl.getSeconds());
    }

    public <T extends BotRequest> void deleteRequest(final int messageId, final long chatId, Class<T> requestType) {
        jedisClient.del(toKey(messageId, chatId, requestType));
    }

    private <T extends BotRequest> String toKey(final int messageId, final long chatId, Class<T> requestType) {
        return chatId + "_" + messageId + "_" + requestType.getSimpleName();
    }
}
