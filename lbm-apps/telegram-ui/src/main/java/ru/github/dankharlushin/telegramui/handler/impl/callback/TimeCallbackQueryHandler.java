package ru.github.dankharlushin.telegramui.handler.impl.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.request.CreateBookingRequest;
import ru.github.dankharlushin.telegramui.model.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.SourceButtonService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TimeCallbackQueryHandler extends BotRequestHandler<CreateBookingRequest> {

    private static final String QUERY_ID = "time";
    private static final String TIME_KEY = "time";
    private static final String BOOKING_REQUEST_CREATED_MESSAGE = "bookingRequestCreatedMessage";
    private static final String SEND_BUTTON_TEXT = "sendButtonText";

    private final BotMessageService messageService;
    private final SourceButtonService buttonService;

    public TimeCallbackQueryHandler(final BotMessageService messageService,
                                    final SourceButtonService buttonService) {
        this.messageService = messageService;
        this.buttonService = buttonService;
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
        final LocalTime time = LocalTime.parse(((String) callback.data().get(TIME_KEY)));
        request.setStartTime(time);
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final CreateBookingRequest request,
                                                      final Update update,
                                                      final TelegramAuthInfo authInfo) {
        final Message inputMessage = update.getCallbackQuery().getMessage();

        final SourceCallbackButton confirm = buttonService.createCallbackButton(SEND_BUTTON_TEXT,
                new CallbackQueryDto("book",
                        false,
                        Map.of("cId", inputMessage.getChatId(), "mId", inputMessage.getMessageId())));
        final SourceCallbackButton back = buttonService.createBackButton(new CallbackQueryDto("date", false,
                Map.of("date", request.getStartDate().toString())));

        final EditMessageText editMessage = messageService.createEditMessage(inputMessage.getChatId(),
                inputMessage.getMessageId(),
                BOOKING_REQUEST_CREATED_MESSAGE,
                KeyboardUtil.inlineKeyboard(Collections.singletonList(confirm), back, 1),
                request.getRequesterName(), request.getLabName(), request.getStartDate(), request.getStartTime());
        return Collections.singletonList(editMessage);
    }
}
