package ru.github.dankharlushin.lbmlib.shell.service.notification;

public enum Urgency {
    LOW("low"),
    NORMAL("normal"),
    CRITICAL("critical");

    private final String commandValue;

    Urgency(final String commandValue) {
        this.commandValue = commandValue;
    }

    public String getStrOsValue() {
        return commandValue;
    }
}
