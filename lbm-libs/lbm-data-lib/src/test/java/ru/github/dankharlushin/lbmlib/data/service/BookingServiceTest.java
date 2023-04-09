package ru.github.dankharlushin.lbmlib.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;
import ru.github.dankharlushin.lbmlib.data.entity.User;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Import(DataLibConfig.class)
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private TestEntityManager entityManager;

    private List<User> testUsers;
    private List<LabUnit> testLabs;

    @BeforeEach
    void setUp() {
        testUsers = createTestUsers();
        testLabs = createTestLabs();
        testUsers.forEach(entityManager::persist);
        testLabs.forEach(entityManager::persist);
    }

    @Test
    void testGetBookingsByStatusNotAndStartDateTimeBeforeAndEndDateTimeAfter() {
        final OffsetDateTime validStart = OffsetDateTime.now().minus(5, ChronoUnit.MINUTES);
        final OffsetDateTime validEnd = OffsetDateTime.now().plus(1, ChronoUnit.HOURS);
        final Booking booking1 = Booking.builder()
                .user(testUsers.get(0))
                .lab(testLabs.get(0))
                .status(BookingStatus.CREATED)
                .startDateTime(validStart)
                .endDateTime(validEnd)
                .build();
        final Booking booking2 = Booking.builder()
                .user(testUsers.get(1))
                .lab(testLabs.get(1))
                .status(BookingStatus.CREATED)
                .startDateTime(validStart)
                .endDateTime(validEnd)
                .build();
        final Booking booking3 = Booking.builder()
                .user(testUsers.get(1))
                .lab(testLabs.get(1))
                .status(BookingStatus.CREATED)
                .startDateTime(OffsetDateTime.now().plus(3, ChronoUnit.HOURS))
                .endDateTime(OffsetDateTime.now().plus(4, ChronoUnit.HOURS))
                .build();
        final Booking booking4 = Booking.builder()
                .user(testUsers.get(1))
                .lab(testLabs.get(1))
                .status(BookingStatus.CANCELED)
                .startDateTime(OffsetDateTime.now().plus(5, ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plus(1, ChronoUnit.HOURS))
                .build();
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        entityManager.persist(booking4);

        final Map<String, String> currentBookings = bookingService.getCurrentBookingLabAppNameToUsername();
        assertThat(currentBookings.size(), is(2));
        assertThat(currentBookings.get("appName0"), is("userName0"));
        assertThat(currentBookings.get("appName1"), is("userName1"));
    }

    private List<User> createTestUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            users.add(User.builder()
                    .osUsername("userName" + i)
                    .osPassword("userPassword" + i)
                    .firstname("firstName" + i)
                    .lastname("lastName" + i)
                    .patronymic("patronymic" + i)
                    .group("group" + i)
                    .build());
        }

        return users;
    }

    private List<LabUnit> createTestLabs() {
        List<LabUnit> labs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            labs.add(LabUnit.builder()
                    .name("labName" + i)
                    .appName("appName" + i)
                    .build());
        }

        return labs;
    }
}
