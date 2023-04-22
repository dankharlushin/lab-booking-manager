package ru.github.dankharlushin.telegramui.handler;

public interface CallbackQueryHandler extends UpdateHandler {

    String getCallbackQueryId();

    default String getErrorMessageCode() {
        return "callbackQueryHandlerDefaultErrorMessage";
    }
}
