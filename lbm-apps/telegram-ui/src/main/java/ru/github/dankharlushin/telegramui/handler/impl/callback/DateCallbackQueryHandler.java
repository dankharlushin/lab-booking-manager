package ru.github.dankharlushin.telegramui.handler.impl.callback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.request.CreateBookingRequest;
import ru.github.dankharlushin.telegramui.model.source.SourceButton;
import ru.github.dankharlushin.telegramui.model.source.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.SourceButtonService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DateCallbackQueryHandler extends BotRequestHandler<CreateBookingRequest> {

    private static final String QUERY_ID = "date";
    private static final String DATE_KEY = "date";
    private static final String BOOKING_PATH = "/booking";
    private static final String AVAILABLE_TIME_PATH = "/available-time";
    private static final String CHOOSE_BOOKING_TIME_MESSAGE_CODE = "chooseBookingTimeMessage";
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final SourceButtonService buttonService;
    private final BotMessageService messageService;
    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;

    public DateCallbackQueryHandler(final SourceButtonService buttonService,
                                    final BotMessageService messageService,
                                    final RestTemplate restTemplate,
                                    @Value("${telegram-ui.booking.service.base-url}") final String bookingServiceUrl) {
        this.buttonService = buttonService;
        this.messageService = messageService;
        this.restTemplate = restTemplate;
        this.bookingServiceUrl = bookingServiceUrl;
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
        final LocalDate date = LocalDate.parse((String) callback.data().get(DATE_KEY));
        request.setStartDate(date);
        request.setStartTime(null);
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final CreateBookingRequest request, final Update update, final TelegramAuthInfo authInfo) {
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final LocalDate date = request.getStartDate();
        final Integer labId = request.getLabUnitId();
        final ResponseEntity<List<Period>> response = restTemplate.exchange(
                bookingServiceUrl + BOOKING_PATH + "/" + labId + AVAILABLE_TIME_PATH + "?date=" + date,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        final List<Period> availablePeriods = Objects.requireNonNullElse(response.getBody(), new ArrayList<>());

        List<SourceButton> keyboardData = new ArrayList<>();
        for (final Period availablePeriod : availablePeriods) {
            final String formattedStart = availablePeriod.start().format(timeFormatter);
            final String formattedEnd = availablePeriod.end().format(timeFormatter);
            final CallbackQueryDto callbackQueryDto = new CallbackQueryDto("time", false,
                    Map.of("time", availablePeriod.start().format(timeFormatter)));
            keyboardData.add(new SourceCallbackButton(formattedStart + " - " + formattedEnd, callbackQueryDto));
        }
        final CallbackQueryDto backButtonCallback = new CallbackQueryDto("lab", false, Map.of("labId", labId));
        final SourceCallbackButton backButton = buttonService.createBackButton(backButtonCallback);

        final EditMessageText editMessage = messageService.createEditMessage(update.getCallbackQuery().getMessage().getChatId(),
                messageId,
                CHOOSE_BOOKING_TIME_MESSAGE_CODE,
                KeyboardUtil.inlineKeyboard(keyboardData, backButton, 1));
        return Collections.singletonList(editMessage);
    }
}
