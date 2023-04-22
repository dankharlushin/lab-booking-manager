package ru.github.dankharlushin.lbmlib.data.dto;

import java.time.LocalDateTime;

public record BookingDto(
        long id,
        String labName,
        String osUsername,
        LocalDateTime startBooking,
        LocalDateTime endBooking
) {
}
