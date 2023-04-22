package ru.github.dankharlushin.telegramui.handler.impl.callback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.BookingDto;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.model.request.DeleteBookingRequest;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.SourceButtonService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class PreDelCallbackQueryHandler extends BotRequestHandler<DeleteBookingRequest> {

    private static final String QUERY_ID = "preDel";
    private static final String BOOKING_ID_KEY = "bId";
    private static final String BOOKING_PATH = "/booking";
    private static final String DELETION_CONFIRMATION_MESSAGE_CODE = "deletionConfirmationMessage";
    private static final String BAD_PERMISSION_ERROR_MESSAGE_CODE = "badPermissionErrorMessage";
    private static final String SEND_BUTTON_TEXT = "sendButtonText";
    private static final String CANCEL_BUTTON_TEXT = "cancelButtonText";

    private final BotMessageService messageService;
    private final SourceButtonService buttonService;
    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;

    public PreDelCallbackQueryHandler(final BotMessageService messageService,
                                      final SourceButtonService buttonService, final RestTemplate restTemplate,
                                      @Value("${telegram-ui.booking.service.base-url}") final String bookingServiceUrl) {
        this.messageService = messageService;
        this.buttonService = buttonService;
        this.restTemplate = restTemplate;
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
        final long bookingId = Long.parseLong(callback.data().get(BOOKING_ID_KEY).toString());
        final BookingDto booking = restTemplate.getForObject(bookingServiceUrl + BOOKING_PATH + "/" + bookingId, BookingDto.class);
        Objects.requireNonNull(booking);

        request.setBookingId(booking.id());
        request.setLabName(booking.labName());
        request.setBookingOwnerUsername(booking.osUsername());
        request.setStartDate(booking.startBooking().toLocalDate());
        request.setStartTime(booking.startBooking().toLocalTime());
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final DeleteBookingRequest request, final Update update, final TelegramAuthInfo authInfo) {
        final Message message = update.getCallbackQuery().getMessage();
        if (!request.getBookingOwnerUsername().equals(request.getRequesterName())) {
            return Collections.singletonList(messageService.createEditMessage(message.getChatId(),
                    message.getMessageId(),
                    BAD_PERMISSION_ERROR_MESSAGE_CODE));
        }

        final SourceCallbackButton confirmDeletion = buttonService.createCallbackButton(SEND_BUTTON_TEXT,
                new CallbackQueryDto("del", false, Map.of("bId", request.getBookingId())));
        final SourceCallbackButton cancel = buttonService.createCallbackButton(CANCEL_BUTTON_TEXT,
                new CallbackQueryDto("bookings", false, Map.of()));

        final EditMessageText editMessage = messageService.createEditMessage(message.getChatId(),
                message.getMessageId(),
                DELETION_CONFIRMATION_MESSAGE_CODE,
                KeyboardUtil.inlineKeyboard(Collections.singletonList(confirmDeletion), cancel, 1),
                request.getBookingOwnerUsername(), request.getLabName(), request.getStartDate(), request.getStartTime());
        return Collections.singletonList(editMessage);
    }
}
