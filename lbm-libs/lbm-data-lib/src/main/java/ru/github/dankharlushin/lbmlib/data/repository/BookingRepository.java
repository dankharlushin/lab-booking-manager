package ru.github.dankharlushin.lbmlib.data.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.entity.BookingStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b.lab.appName, b.user.osUsername " +
            "from Booking b " +
            "where b.status <> :status and b.startDateTime < :startDateTime and b.endDateTime > :endDateTime")
    List<Tuple> getCurrentBookingsLabAppNameToUserOsUsername(@Param("status") final BookingStatus status,
                                                             @Param("startDateTime") final OffsetDateTime startDateTime,
                                                             @Param("endDateTime") final OffsetDateTime endDateTime);
}
