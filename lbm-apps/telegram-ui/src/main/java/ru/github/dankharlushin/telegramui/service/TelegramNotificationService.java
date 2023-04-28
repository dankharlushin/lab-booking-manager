package ru.github.dankharlushin.telegramui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.github.dankharlushin.lbmlib.data.cache.JedisClient;
import ru.github.dankharlushin.lbmlib.data.dto.notification.impl.TelegramUiStartBookingNotification;
import ru.github.dankharlushin.telegramui.model.BotActionWrapper;

@Service
public class TelegramNotificationService {

    private static final String START_BOOKING_NOTIFICATION_MESSAGE_CODE = "startBookingNotificationMessage";
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final BotMessageService messageService;
    private final JedisClient jedisClient;
    private final ApplicationEventPublisher eventPublisher;

    public TelegramNotificationService(final BotMessageService messageService,
                                       final JedisClient jedisClient,
                                       final ApplicationEventPublisher eventPublisher) {
        this.messageService = messageService;
        this.jedisClient = jedisClient;
        this.eventPublisher = eventPublisher;
    }

    public void notifyUser(final TelegramUiStartBookingNotification notification) {
        final long chatId = notification.getChatId();
        try {
            if (jedisClient.exists(String.valueOf(chatId))) {
                final SendMessage message = messageService.createReplyMessage(chatId,
                        START_BOOKING_NOTIFICATION_MESSAGE_CODE,
                        null,
                        notification.getLabName(),
                        notification.getStartInMinutes());

                eventPublisher.publishEvent(new BotActionWrapper(message, chatId));
                logger.info("Notification with id [{}], lab [{}], chatId [{}] was successfully sent",
                        notification.getId(), notification.getLabName(), chatId);
            }
        } catch (final Exception e) {
            logger.error("Unable to send notification with id [{}], lab [{}], chatId [{}]",
                    notification.getId(), notification.getLabName(), chatId, e);
        }
    }
}
