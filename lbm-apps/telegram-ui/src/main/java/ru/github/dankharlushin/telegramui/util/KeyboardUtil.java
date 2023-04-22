package ru.github.dankharlushin.telegramui.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.github.dankharlushin.telegramui.model.source.SourceButton;
import ru.github.dankharlushin.telegramui.model.source.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.model.source.SourceLinkButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private KeyboardUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static InlineKeyboardMarkup inlineKeyboard(final List<SourceButton> sourceButtons, int colNum) {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        sourceButtons.stream()
                .map(KeyboardUtil::mapSourceButton)
                .forEach(button -> insertIntoKeyboard(colNum, keyboard, row, button));
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineKeyboard(final List<SourceButton> sourceButtons,
                                                      final SourceCallbackButton backButton,
                                                      int colNum) {
        final InlineKeyboardButton back = mapSourceButton(backButton);
        final InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboard(sourceButtons, colNum);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(back);
        inlineKeyboardMarkup.getKeyboard().add(row);
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardButton mapSourceButton(final SourceButton sourceButton) {
        return switch (sourceButton) {
            case SourceCallbackButton sourceCallbackButton -> createCallbackButton(sourceCallbackButton);
            case SourceLinkButton sourceLinkButton -> createLinkButton(sourceLinkButton);
            default -> throw new IllegalStateException("Unexpected value: " + sourceButton);
        };
    }

    private static InlineKeyboardButton createCallbackButton(final SourceCallbackButton sourceButton) {
        try {
            final String callbackData = objectMapper.writeValueAsString(sourceButton.callback());
            if (callbackData.length() > 64) {
                throw new IllegalArgumentException("Invalid button callback data length: [" + callbackData.length() + "] data: [" + callbackData + "]");
            }

            return InlineKeyboardButton.builder()
                    .text(sourceButton.text())
                    .callbackData(callbackData)
                    .build();
        } catch (final JsonProcessingException e) {
            throw new IllegalStateException("Unable to add callback to button [" + sourceButton.callback() + "]", e);
        }
    }

    private static InlineKeyboardButton createLinkButton(final SourceLinkButton sourceButton) {
        return InlineKeyboardButton.builder()
                .text(sourceButton.text())
                .url(sourceButton.url())
                .build();
    }

    private static void insertIntoKeyboard(final int colNum,
                                           final List<List<InlineKeyboardButton>> keyboard,
                                           final List<InlineKeyboardButton> row,
                                           final InlineKeyboardButton button) {
        if (row.size() < colNum) {
            row.add(button);
        } else {
            keyboard.add(new ArrayList<>(row));
            row.clear();
            row.add(button);
        }
    }
}
