package ru.github.dankharlushin.lbmlib.data.util;

import ru.github.dankharlushin.lbmlib.data.dto.Period;

public class BookingUtil {

    private BookingUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isOverlapping(Period period1, Period period2) {
        return period1.start().isBefore(period2.end()) && period2.start().isBefore(period1.end());
    }
}
