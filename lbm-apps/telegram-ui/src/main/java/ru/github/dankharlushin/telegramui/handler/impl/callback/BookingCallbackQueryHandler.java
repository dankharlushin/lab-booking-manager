package ru.github.dankharlushin.telegramui.handler.impl.callback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.BookingDto;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.source.SourceButton;
import ru.github.dankharlushin.telegramui.model.source.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.model.request.DeleteBookingRequest;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class BookingCallbackQueryHandler extends BotRequestHandler<DeleteBookingRequest> {

    private static final String QUERY_ID = "bookings";
    private static final String BOOKING_PATH = "/booking";
    private static final String FUTURE_PATH = "/future";
    private static final String NO_ACTIVE_BOOKINGS_MESSAGE_CODE = "noActiveBookingsMessage";
    private static final String ACTIVE_BOOKING_LIST_MESSAGE_CODE = "activeBookingListMessage";

    private final RestTemplate restTemplate;
    private final BotMessageService messageService;
    private final String bookingServiceUrl;

    public BookingCallbackQueryHandler(final RestTemplate restTemplate,
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
        request.setBookingId(null);
        request.setLabName(null);
        request.setBookingOwnerUsername(null);
        request.setStartDate(null);
        request.setStartTime(null);
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final DeleteBookingRequest request, final Update update, final TelegramAuthInfo authInfo) {
        final Message message = update.getCallbackQuery().getMessage();
        final Long chatId = message.getChatId();
        final Integer messageId = message.getMessageId();

        final ResponseEntity<List<BookingDto>> response = restTemplate.exchange(
                bookingServiceUrl + BOOKING_PATH + "/" + authInfo.osUsername() + FUTURE_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        final List<BookingDto> bookings = response.getBody();
        if (bookings == null || bookings.isEmpty()) {
            final SendMessage replyMessage = messageService.createReplyMessage(chatId, NO_ACTIVE_BOOKINGS_MESSAGE_CODE);
            return Collections.singletonList(replyMessage);
        }

        final List<SourceButton> sourceButtons = new ArrayList<>();
        for (final BookingDto booking : bookings) {
            sourceButtons.add(new SourceCallbackButton(initButtonText(booking),
                    new CallbackQueryDto("preDel", true, Map.of("bId", booking.id()))));
        }

        final EditMessageText editMessage = messageService.createEditMessage(chatId,
                messageId,
                ACTIVE_BOOKING_LIST_MESSAGE_CODE,
                KeyboardUtil.inlineKeyboard(sourceButtons, 1));
        return Collections.singletonList(editMessage);
    }

    private String initButtonText(final BookingDto booking) {
        return String.format("%s %s - %s | %s",
                booking.startBooking().toLocalDate().toString(),
                booking.startBooking().toLocalTime().toString(),
                booking.endBooking().toLocalTime(),
                booking.labName());
    }
}
