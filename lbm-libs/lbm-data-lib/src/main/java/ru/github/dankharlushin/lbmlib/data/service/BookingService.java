package ru.github.dankharlushin.lbmlib.data.service;

import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {

    Map<String, String> getCurrentBookingLabAppNameToUsername();

    List<LocalDate> getAvailableDates(final Integer labUnitId);

    List<Period> getAvailableTime(final LocalDate date, final Integer labUnitId);

    List<Booking> getFutureBookingByUsername(final String osUsername);

    Booking getById(final Long id);

    void save(final Booking booking);

    void deleteById(final Long id);
}
