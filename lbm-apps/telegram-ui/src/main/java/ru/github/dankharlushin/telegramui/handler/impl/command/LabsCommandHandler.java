package ru.github.dankharlushin.telegramui.handler.impl.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.github.dankharlushin.lbmlib.data.dto.TelegramAuthInfo;
import ru.github.dankharlushin.lbmlib.data.dto.LabUnitDto;
import ru.github.dankharlushin.telegramui.handler.CommandHandler;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.source.SourceButton;
import ru.github.dankharlushin.telegramui.model.source.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.service.BotMessageService;
import ru.github.dankharlushin.telegramui.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class LabsCommandHandler implements CommandHandler {

    private static final String COMMAND = "/labs";
    private static final String LAB_NAMES_PATH = "/lab/names";
    private static final String LAB_LIST_MESSAGE_CODE = "labListMessage";

    private final BotMessageService messageService;
    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;

    public LabsCommandHandler(final BotMessageService messageService,
                              final RestTemplate restTemplate,
                              @Value("${telegram-ui.booking.service.base-url}") final String bookingServiceUrl) {
        this.messageService = messageService;
        this.restTemplate = restTemplate;
        this.bookingServiceUrl = bookingServiceUrl;
    }

    @Override
    public String getTextMessage() {
        return COMMAND;
    }

    @Override
    public List<BotApiMethod<?>> handle(final Update update, final TelegramAuthInfo authInfo) {
        final ResponseEntity<List<LabUnitDto>> response = restTemplate.exchange(bookingServiceUrl + LAB_NAMES_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        final List<LabUnitDto> labUnitDtos = response.getBody();
        if (labUnitDtos == null) {
            throw new IllegalStateException("Can't retrieve lab names info");
        }

        List<SourceButton> keyboardData = new ArrayList<>();
        for (final LabUnitDto labUnitDto : labUnitDtos) {
            final CallbackQueryDto callback = new CallbackQueryDto("lab", true, Map.of("labId", labUnitDto.id()));
            keyboardData.add(new SourceCallbackButton(labUnitDto.name(), callback));
        }

        final InlineKeyboardMarkup keyboard = KeyboardUtil.inlineKeyboard(keyboardData, 1);
        final SendMessage message = messageService.createReplyMessage(update.getMessage().getChatId(), LAB_LIST_MESSAGE_CODE, keyboard);
        return Collections.singletonList(message);
    }
}
