package ru.github.dankharlushin.telegramui.handler.impl.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.request.DeleteBookingRequest;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.Collections;
import java.util.List;

@Component
public class DelCallbackQueryHandler extends BotRequestHandler<DeleteBookingRequest> {

    private static final String QUERY_ID = "del";
    private static final String BOOKING_ID_KEY = "bId";
    private static final String BOOKING_PATH = "/booking";
    private static final String DELETE_PATH = "/delete";
    private static final String DELETION_ERROR_MESSAGE_CODE = "deletionErrorMessage";
    private static final String DELETION_SUCCESS_MESSAGE_CODE = "deletionSuccessMessage";
    private static final String BAD_PERMISSION_ERROR_MESSAGE_CODE = "badPermissionErrorMessage";

    private static final Logger logger = LoggerFactory.getLogger(DelCallbackQueryHandler.class);

    private final RestTemplate restTemplate;
    private final BotMessageService messageService;
    private final String bookingServiceUrl;

    public DelCallbackQueryHandler(final RestTemplate restTemplate,
                                   final BotMessageService messageService,
                                   @Value("${telegram-ui.booking.service.base-url}") final String bookingServiceUrl) {
        this.restTemplate = restTemplate;
        this.messageService = messageService;
        this.bookingServiceUrl = bookingServiceUrl;
    }

    @Override
    public String getCallbackQueryId() {
        return QUERY_ID;
    }

    @Override
    protected Class<DeleteBookingRequest> getRequestType() {
        return DeleteBookingRequest.class;
    }

    @Override
    protected void updateRequest(final DeleteBookingRequest request, final CallbackQueryDto callback) {
        if (!request.getBookingOwnerUsername().equals(request.getRequesterName())) {
            logger.debug("Bad booking deletion permission, booking owner [{}], current user [{}]",
                    request.getBookingOwnerUsername(),
                    request.getRequesterName());
            return;
        }

        final long bookingId = Long.parseLong(callback.data().get(BOOKING_ID_KEY).toString());
        try {
            final ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    bookingServiceUrl + BOOKING_PATH + "/" + bookingId + DELETE_PATH,
                    null,
                    String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) { request.setDeleted(true); }
        } catch (final Exception e) {
            logger.error("Deletion error with booking id [{}]", bookingId, e);
        }
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final DeleteBookingRequest request,
                                                      final Update update,
                                                      final TelegramAuthInfo authInfo) {
        final Message inputMessage = update.getCallbackQuery().getMessage();
        if (!request.getBookingOwnerUsername().equals(request.getRequesterName())) {
            return Collections.singletonList(messageService.createEditMessage(inputMessage.getChatId(),
                    inputMessage.getMessageId(),
                    BAD_PERMISSION_ERROR_MESSAGE_CODE));
        }

        final String msgCode = request.isDeleted() ? DELETION_SUCCESS_MESSAGE_CODE : DELETION_ERROR_MESSAGE_CODE;
        final EditMessageText editMessage = messageService.createEditMessage(inputMessage.getChatId(),
                inputMessage.getMessageId(),
                msgCode,
                null,
                request.getBookingOwnerUsername(), request.getLabName(), request.getStartDate(), request.getStartTime());
        return Collections.singletonList(editMessage);
    }
}
