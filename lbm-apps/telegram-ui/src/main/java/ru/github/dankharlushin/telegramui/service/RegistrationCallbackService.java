package ru.github.dankharlushin.telegramui.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.github.dankharlushin.telegramui.model.request.CreateBookingRequest;
import ru.github.dankharlushin.telegramui.model.TelegramCallbackResponse;

@Service
public class RegistrationCallbackService {

    private static final String BOOKING_REQUEST_SUCCESS_MESSAGE_CODE = "bookingRequestSuccessMessage";
    private static final String BOOKING_REQUEST_REJECT_MESSAGE_CODE = "bookingRequestRejectMessage";
    private static final String DEFAULT_ERROR_MESSAGE = "defaultErrorMessage";

    private final ApplicationEventPublisher eventPublisher;
    private final BotRequestCacheService cacheService;
    private final BotMessageService messageService;

    public RegistrationCallbackService(final ApplicationEventPublisher eventPublisher,
                                       final BotRequestCacheService cacheService,
                                       final BotMessageService messageService) {
        this.eventPublisher = eventPublisher;
        this.cacheService = cacheService;
        this.messageService = messageService;
    }

    public void processCallback(final TelegramCallbackResponse callbackResponse, final String errorMessage) {
        final int messageId = callbackResponse.messageId();
        final long chatId = callbackResponse.chatId();
        final CreateBookingRequest request = cacheService.getRequest(messageId, chatId, CreateBookingRequest.class);

        final EditMessageText editMessage;
        if (request == null) {
            editMessage = messageService.createEditMessage(chatId,
                    messageId,
                    DEFAULT_ERROR_MESSAGE,
                    null);
        } else if (errorMessage == null) {
            cacheService.deleteRequest(messageId, chatId, request.getClass());
            editMessage = messageService.createEditMessage(chatId,
                    messageId,
                    BOOKING_REQUEST_SUCCESS_MESSAGE_CODE,
                    null,
                    request.getRequesterName(), request.getLabName(), request.getStartDate(), request.getStartTime());
        } else {
            cacheService.deleteRequest(messageId, chatId, request.getClass());
            editMessage = messageService.createEditMessage(chatId,
                    messageId,
                    BOOKING_REQUEST_REJECT_MESSAGE_CODE,
                    null,
                    request.getRequesterName(), request.getLabName(), request.getStartDate(), request.getStartTime(), errorMessage);
        }
        eventPublisher.publishEvent(editMessage);
    }
}
