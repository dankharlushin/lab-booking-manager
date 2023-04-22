package ru.github.dankharlushin.lbmlib.data.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;
import ru.github.dankharlushin.lbmlib.data.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest(properties = {"libs.data.service.booking.default.start-booking-time=08:30:00,10:10:00,11:50:00,14:00:00,15:40:00",
        "libs.data.service.booking.default.available-days=14",
        "libs.data.service.booking.default.booking-time=90"})
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

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    void testGetBookingsByStatusNotAndStartDateTimeBeforeAndEndDateTimeAfter() {
        final LocalDateTime validStart = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        final LocalDateTime validEnd = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
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
                .startDateTime(LocalDateTime.now().plus(3, ChronoUnit.HOURS))
                .endDateTime(LocalDateTime.now().plus(4, ChronoUnit.HOURS))
                .build();
        final Booking booking4 = Booking.builder()
                .user(testUsers.get(1))
                .lab(testLabs.get(1))
                .status(BookingStatus.CANCELED)
                .startDateTime(LocalDateTime.now().plus(5, ChronoUnit.MINUTES))
                .endDateTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
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

    @Test
    void testGetAvailableDates() {
        final Booking booking = Booking.builder()
                .user(testUsers.get(0))
                .lab(testLabs.get(1))
                .status(BookingStatus.CREATED)
                .startDateTime(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(8, 30)))
                .endDateTime(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(10, 0)))
                .build();

        entityManager.persist(booking);
        final List<LocalDate> availableDates = bookingService.getAvailableDates(testLabs.get(1).getId());

        assertThat(availableDates.size(), is(12));
    }

    @Test
    void testGetAvailableTime() {
        final LocalDate date = LocalDate.now().plusDays(2);
        final Booking booking = Booking.builder()
                .user(testUsers.get(0))
                .lab(testLabs.get(1))
                .status(BookingStatus.CREATED)
                .startDateTime(LocalDateTime.of(date, LocalTime.of(8, 30)))
                .endDateTime(LocalDateTime.of(date, LocalTime.of(10, 0)))
                .build();

        entityManager.persist(booking);

        final List<Period> availableTime = bookingService.getAvailableTime(date, testLabs.get(1).getId());

        assertThat(availableTime.size(), is(4));
        assertThat(availableTime.get(0).start().isEqual(LocalDateTime.of(date, LocalTime.of(10, 10))), is(true));
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
