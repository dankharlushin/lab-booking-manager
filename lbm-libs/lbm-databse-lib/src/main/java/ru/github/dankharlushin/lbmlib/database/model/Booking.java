package ru.github.dankharlushin.lbmlib.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date_time")
    private ZonedDateTime startDateTime;
    @Column(name = "end_date_time")
    private ZonedDateTime endDateTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
    @ManyToOne
    private User user;
    @ManyToOne
    private LabUnit lab;
}
