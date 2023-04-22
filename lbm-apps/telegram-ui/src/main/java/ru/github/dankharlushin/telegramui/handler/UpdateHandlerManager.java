package ru.github.dankharlushin.telegramui.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.telegramui.handler.impl.DefaultUpdateHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.List;
import java.util.Optional;

@Component
public class UpdateHandlerManager {

    private static final Logger logger = LoggerFactory.getLogger(UpdateHandlerManager.class);

    private final List<CallbackQueryHandler> callbackQueryHandlers;
    private final List<CommandHandler> commandHandlers;
    private final BotMessageService messageService;
    private final ObjectMapper objectMapper;

    public UpdateHandlerManager(final List<CallbackQueryHandler> callbackQueryHandlers,
                                final List<CommandHandler> commandHandlers,
                                final BotMessageService messageService,
                                final ObjectMapper objectMapper) {
        this.callbackQueryHandlers = callbackQueryHandlers;
        this.commandHandlers = commandHandlers;
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    public UpdateHandler findHandler(final Update update) {
        if (update.hasCallbackQuery()) {
            return findQueryHandler(update);
        } else if (update.hasMessage()) {
            return findByMessage(update);
        } else {
            logger.debug("Return default update handler");
            return new DefaultUpdateHandler(messageService, null);
        }
    }

    private UpdateHandler findQueryHandler(final Update update) {
        try {
            final CallbackQueryDto callback = objectMapper.readValue(update.getCallbackQuery().getData(), CallbackQueryDto.class);
            final String callbackQueryId = callback.id();
            return callbackQueryHandlers
                    .stream()
                    .filter(handler -> handler.getCallbackQueryId().equals(callbackQueryId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Wrong call back query id [" + callbackQueryId + "]"));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Bad callback query format [" + update.getCallbackQuery().getData() + "]");
        }
    }

    private UpdateHandler findByMessage(final Update update) {
        final Message message = update.getMessage();
        if (message.hasText()) {
            final Optional<CommandHandler> optHandler = commandHandlers
                    .stream()
                    .filter(handler -> message.getText().contains(message.getText()))
                    .findFirst();
            if (optHandler.isPresent()) {
                logger.debug("Find text message handler for update with text [{}]", update.getMessage().getText());
                return optHandler.get();
            }
        }

        logger.debug("Return default update handler");
        return new DefaultUpdateHandler(messageService, null);
    }
}
