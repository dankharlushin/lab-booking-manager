package ru.github.dankharlushin.telegramui.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.impl.DefaultUpdateHandler;
import ru.github.dankharlushin.telegramui.model.request.BotRequest;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.BotRequestCacheService;

import java.util.List;

@Component
public abstract class BotRequestHandler<T extends BotRequest> implements CallbackQueryHandler {

    private ObjectMapper objectMapper;
    private BotRequestCacheService requestCacheService;
    private BotMessageService messageService;

    @Autowired
    public final void setObjectMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @Autowired
    public final void setRequestCacheService(BotRequestCacheService requestCacheService) { this.requestCacheService = requestCacheService; }

    @Autowired
    public void setMessageService(final BotMessageService messageService) { this.messageService = messageService; }

    @Override
    public final List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo) {
        final CallbackQueryDto callback = extractCallback(update);
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        if (callback.init()) {
            initBotRequest(authInfo, messageId, chatId);
        }

        if (requestCacheService.requestExists(messageId, chatId, getRequestType())) {
            final T request = requestCacheService.getRequest(messageId, chatId, getRequestType());
            request.setRequesterName(authInfo.osUsername());
            updateRequest(request, callback);
            requestCacheService.setRequest(messageId, chatId, request);
            return constructResponse(request, update, authInfo);
        } else {
            return new DefaultUpdateHandler(messageService, getErrorMessageCode()).handle(update, authInfo);
        }
    }

    protected abstract Class<T> getRequestType();

    protected abstract void updateRequest(final T request, final CallbackQueryDto callback);

    protected abstract List<BotApiMethod<?>> constructResponse(final T request,
                                                               final Update update,
                                                               final TelegramAuthInfo authInfo);

    private void initBotRequest(final TelegramAuthInfo authInfo, final Integer messageId, final Long chatId) {
        try {
            final T botRequest = getRequestType().getDeclaredConstructor().newInstance();
            botRequest.setRequesterName(authInfo.osUsername());
            requestCacheService.setRequest(messageId, chatId, botRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create new instance of bot request", e);
        }
    }

    private CallbackQueryDto extractCallback(final Update update) {
        try {
            return objectMapper.readValue(update.getCallbackQuery().getData(), CallbackQueryDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Bad callback query format [" + update.getCallbackQuery().getData() + "]");
        }
    }
}
