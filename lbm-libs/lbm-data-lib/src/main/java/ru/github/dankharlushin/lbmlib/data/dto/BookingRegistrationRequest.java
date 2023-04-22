package ru.github.dankharlushin.lbmlib.data.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRegistrationRequest (
    String osUsername,
    Integer labId,
    LocalDate startDate,
    LocalTime startTime,
    String callbackUrl,
    Object callbackResponse
) {
}
