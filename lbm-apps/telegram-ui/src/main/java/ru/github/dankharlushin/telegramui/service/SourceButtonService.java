package ru.github.dankharlushin.telegramui.service;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.telegramui.model.CallbackQueryDto;
import ru.github.dankharlushin.telegramui.model.SourceCallbackButton;
import ru.github.dankharlushin.telegramui.model.SourceLinkButton;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;

@Service
public class SourceButtonService {

    private static final String BACK_BUTTON_TEXT_CODE = "backButtonText";

    private final MessageSourceAccessor sourceAccessor;

    public SourceButtonService(final MessageSourceAccessor sourceAccessor) {
        this.sourceAccessor = sourceAccessor;
    }

    public SourceLinkButton createLinkButton(final String messageCode, final String url) {
        return new SourceLinkButton(parseToUnicode(sourceAccessor.getMessage(messageCode)), url);
    }

    public SourceCallbackButton createCallbackButton(final String messageCode, final CallbackQueryDto callbackQueryDto) {
        return new SourceCallbackButton(parseToUnicode(sourceAccessor.getMessage(messageCode)), callbackQueryDto);
    }

    public SourceCallbackButton createBackButton(final CallbackQueryDto callbackQueryDto) {
        return createCallbackButton(BACK_BUTTON_TEXT_CODE, callbackQueryDto);
    }
}
