package ru.github.dankharlushin.lbmlib.data.service;

import jakarta.persistence.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;
import ru.github.dankharlushin.lbmlib.data.repository.BookingRepository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(final BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Map<String, String> getCurrentBookingLabAppNameToUsername() {
        Map<String, String> result = new HashMap<>();
        final List<Tuple> rows = bookingRepository.getCurrentBookingsLabAppNameToUserOsUsername(
                BookingStatus.CANCELED,
                OffsetDateTime.now(),
                OffsetDateTime.now());
        for (final Tuple row : rows) {
            if (result.containsKey(row.get(0, String.class))) {
                logger.error("Found two bookings on one lab");
            }
            result.put(row.get(0, String.class), row.get(1, String.class));
        }
        return result;
    }
}
