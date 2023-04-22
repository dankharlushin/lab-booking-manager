package ru.github.dankharlushin.lbmlib.data.dto;

public record BookingRegistrationResponse(
        String errorMessage,
        Object callbackData
) {
}
