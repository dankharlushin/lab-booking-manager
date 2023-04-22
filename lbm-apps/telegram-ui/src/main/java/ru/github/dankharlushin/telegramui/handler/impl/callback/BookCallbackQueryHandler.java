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
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.request.CreateBookingRequest;
import ru.github.dankharlushin.telegramui.model.TelegramCallbackResponse;
import ru.github.dankharlushin.telegramui.service.BotMessageService;

import java.util.Collections;
import java.util.List;

@Component
public class BookCallbackQueryHandler extends BotRequestHandler<CreateBookingRequest> {

    private static final String QUERY_ID = "book";
    private static final String CHAT_ID_KEY = "cId";
    private static final String MESSAGE_ID_KEY = "mId";
    private static final String BOOKING_CREATE_PATH = "/booking/create";
    private static final String BOOKING_REQUEST_PROCESSING_MESSAGE_CODE = "bookingRequestProcessingMessage";
    private static final String BOOKING_REQUEST_SENDING_ERROR_MESSAGE_CODE = "bookingRequestSendingErrorMessage";

    private static final Logger logger = LoggerFactory.getLogger(BookCallbackQueryHandler.class);

    private final BotMessageService messageService;
    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;
    private final String selfBaseUrl;

    public BookCallbackQueryHandler(final BotMessageService messageService,
                                    final RestTemplate restTemplate,
                                    @Value("${telegram-ui.booking.service.base-url}") final String bookingServiceUrl,
                                    @Value("${telegram-ui.booking.callback-registration-url}") final String selfBaseUrl) {
        this.messageService = messageService;
        this.restTemplate = restTemplate;
        this.bookingServiceUrl = bookingServiceUrl;
        this.selfBaseUrl = selfBaseUrl;
    }

    @Override
    public String getCallbackQueryId() {
        return QUERY_ID;
    }

    @Override
    protected Class<CreateBookingRequest> getRequestType() {
        return CreateBookingRequest.class;
    }

    @Override
    protected void updateRequest(final CreateBookingRequest request, final CallbackQueryDto callback) {
        final long chatId = Long.parseLong(callback.data().get(CHAT_ID_KEY).toString());
        final int messageId = (int) callback.data().get(MESSAGE_ID_KEY);

        final BookingRegistrationRequest bookingRegistrationRequest = new BookingRegistrationRequest(request.getRequesterName(),
                request.getLabUnitId(),
                request.getStartDate(),
                request.getStartTime(),
                selfBaseUrl,
                new TelegramCallbackResponse(chatId, messageId));

        try {
            final ResponseEntity<String> responseEntity = restTemplate.postForEntity(bookingServiceUrl + BOOKING_CREATE_PATH,
                    bookingRegistrationRequest,
                    String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) { request.setDelivered(true); }
        } catch (final Exception e) {
            logger.error("Sending error with booking request [{}]", request, e);
        }
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final CreateBookingRequest request, final Update update, final TelegramAuthInfo authInfo) {
        final Message inputMessage = update.getCallbackQuery().getMessage();
        final String msgCode = request.isDelivered() ? BOOKING_REQUEST_PROCESSING_MESSAGE_CODE : BOOKING_REQUEST_SENDING_ERROR_MESSAGE_CODE;

        final EditMessageText editMessage = messageService.createEditMessage(inputMessage.getChatId(),
                inputMessage.getMessageId(),
                msgCode,
                null,
                request.getRequesterName(), request.getLabName(), request.getStartDate(), request.getStartTime());
        return Collections.singletonList(editMessage);
    }
}
