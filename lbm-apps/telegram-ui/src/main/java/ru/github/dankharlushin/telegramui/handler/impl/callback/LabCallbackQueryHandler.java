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
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.lbmlib.data.dto.LabUnitDto;
import ru.github.dankharlushin.telegramui.handler.BotRequestHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.request.CreateBookingRequest;
import ru.github.dankharlushin.telegramui.model.SourceButton;
import ru.github.dankharlushin.telegramui.model.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.service.SourceButtonService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class LabCallbackQueryHandler extends BotRequestHandler<CreateBookingRequest> {

    private static final String QUERY_ID = "lab";
    private static final String LAB_ID_KEY = "labId";
    private static final String LAB_PATH = "/lab";
    private static final String NAME_PATH = "/name";
    private static final String BOOKING_PATH = "/booking";
    private static final String AVAILABLE_DATES_PATH = "/available-dates";
    private static final String CHOOSE_BOOKING_DATE_MESSAGE_CODE = "chooseBookingDateMessage";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM");

    private final SourceButtonService buttonService;
    private final BotMessageService messageService;
    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;

    public LabCallbackQueryHandler(final SourceButtonService buttonService,
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
        final Integer labId = (Integer) callback.data().get(LAB_ID_KEY);
        final LabUnitDto labUnitInfo = restTemplate.getForObject(
                bookingServiceUrl + LAB_PATH + "/" + labId + NAME_PATH,
                LabUnitDto.class);
        Objects.requireNonNull(labUnitInfo);

        request.setLabUnitId(Objects.requireNonNull(labUnitInfo.id()));
        request.setLabName(Objects.requireNonNull(labUnitInfo.name()));
        request.setStartDate(null);
    }

    @Override
    protected List<BotApiMethod<?>> constructResponse(final CreateBookingRequest request,
                                                      final Update update,
                                                      final TelegramAuthInfo authInfo) {
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final Integer labId = request.getLabUnitId();

        final ResponseEntity<List<LocalDate>> response = restTemplate.exchange(
                bookingServiceUrl + BOOKING_PATH + "/" + labId + AVAILABLE_DATES_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        final List<LocalDate> availableDates = Objects.requireNonNullElse(response.getBody(), new ArrayList<>());

        List<SourceButton> keyboardData = new ArrayList<>();
        for (final LocalDate availableDate : availableDates) {
            final String formattedDate = availableDate.format(dateTimeFormatter);
            final CallbackQueryDto callbackQueryDto = new CallbackQueryDto("date", false,
                    Map.of("date", availableDate.toString()));
            keyboardData.add(new SourceCallbackButton(formattedDate, callbackQueryDto));
        }
        final CallbackQueryDto backButtonCallback = new CallbackQueryDto("labs", false, Map.of());
        final SourceCallbackButton backButton = buttonService.createBackButton(backButtonCallback);

        final EditMessageText editMessage = messageService.createEditMessage(update.getCallbackQuery().getMessage().getChatId(),
                messageId,
                CHOOSE_BOOKING_DATE_MESSAGE_CODE,
                KeyboardUtil.inlineKeyboard(keyboardData, backButton, 6));
        return Collections.singletonList(editMessage);
    }
}
