package ru.github.dankharlushin.lbmlib.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

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
    private OffsetDateTime startDateTime;
    @Column(name = "end_date_time")
    private OffsetDateTime endDateTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
    @ManyToOne
    private User user;
    @ManyToOne
    private LabUnit lab;
}
