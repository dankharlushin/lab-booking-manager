package ru.github.dankharlushin.bookingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.dankharlushin.bookingservice.producer.BookingRegistrationRequestProducer;
import ru.github.dankharlushin.lbmlib.data.dto.BookingDto;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;
import ru.github.dankharlushin.lbmlib.data.dto.Period;
import ru.github.dankharlushin.lbmlib.data.entity.Booking;
import ru.github.dankharlushin.lbmlib.data.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRegistrationRequestProducer registrationRequestProducer;

    public BookingController(final BookingService bookingService,
                             final BookingRegistrationRequestProducer registrationRequestProducer) {
        this.bookingService = bookingService;
        this.registrationRequestProducer = registrationRequestProducer;
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable final Long id) {
        final Booking booking = bookingService.getById(id);
        return new BookingDto(booking.getId(),
                booking.getLab().getName(),
                booking.getUser().getOsUsername(),
                booking.getStartDateTime(),
                booking.getEndDateTime());
    }

    @GetMapping("/{osUsername}/future")
    public List<BookingDto> getFutureBookings(@PathVariable final String osUsername) {
        return bookingService.getFutureBookingByUsername(osUsername)
                .stream()
                .map(b -> new BookingDto(b.getId(),
                        b.getLab().getName(),
                        b.getUser().getOsUsername(),
                        b.getStartDateTime(),
                        b.getEndDateTime()))
                .toList();
    }

    @GetMapping("/{labId}/available-dates")
    public List<LocalDate> getAvailableDates(@PathVariable final Integer labId) {
        return bookingService.getAvailableDates(labId);
    }

    @GetMapping("/{labId}/available-time")
    public List<Period> getAvailableTime(@PathVariable final Integer labId, @RequestParam LocalDate date) {
        return bookingService.getAvailableTime(date, labId);
    }

    @PostMapping(value = "/create", consumes = {"application/json"})
    public ResponseEntity<String> createBooking(@RequestBody BookingRegistrationRequest registrationRequest) {
        registrationRequestProducer.sendRequest(registrationRequest);
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/{id}/delete")
    public ResponseEntity<String> deleteBooking(@PathVariable final Long id) {
        bookingService.deleteById(id);
        return ResponseEntity.ok("OK");
    }
}
