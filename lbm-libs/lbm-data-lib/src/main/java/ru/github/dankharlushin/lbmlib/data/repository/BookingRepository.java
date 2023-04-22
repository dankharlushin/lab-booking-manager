package ru.github.dankharlushin.lbmlib.data.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b.lab.appName, b.user.osUsername " +
            "from Booking b " +
            "where b.status <> :status and b.startDateTime < :startDateTime and b.endDateTime > :endDateTime")
    List<Tuple> getLabAppNameToUserOsUsername(@Param("status") final BookingStatus status,
                                              @Param("startDateTime") final LocalDateTime startDateTime,
                                              @Param("endDateTime") final LocalDateTime endDateTime);
    @Query("select b from Booking  b " +
            "join b.lab l " +
            "where l.id = :labId and b.status <> :status and b.startDateTime > :startDateTimeBegin and b.startDateTime < :startDateTimeEnd")
    List<Booking> getBookingsByLabIdAndDatesAndStatusNot(@Param("labId") final Integer labId,
                                                         @Param("status") final BookingStatus status,
                                                         @Param("startDateTimeBegin") final LocalDateTime startDateTimeBegin,
                                                         @Param("startDateTimeEnd") final LocalDateTime startDateTimeEnd);

    @Query("select b from Booking b " +
            "join b.user u " +
            "where u.osUsername = :osUsername and b.status = 'CREATED' and b.startDateTime > :startDateTime " +
            "order by b.startDateTime")
    List<Booking> getCreatedBookingsByUsernameAndStartDateAfter(@Param("osUsername") final String osUsername,
                                                                @Param("startDateTime") final LocalDateTime startDateTime);

    default List<Booking> getBookingsByLabIdAndDateAndStatusNot(final Integer labId,
                                                                final BookingStatus status,
                                                                final LocalDate date) {
        return getBookingsByLabIdAndDatesAndStatusNot(labId, status, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }
}
