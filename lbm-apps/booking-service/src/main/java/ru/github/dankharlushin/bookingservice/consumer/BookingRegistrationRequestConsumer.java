package ru.github.dankharlushin.bookingservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.bookingservice.handler.BookingRequestHandler;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;

@Component
public class BookingRegistrationRequestConsumer {

    private final BookingRequestHandler requestHandler;

    public BookingRegistrationRequestConsumer(final BookingRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @KafkaListener(topics = "${booking-service.registration.kafka.topic.name}",
            containerFactory = "bookingRegistrationKafkaListenerContainerFactory")
    public void bookingRegistrationListener(final BookingRegistrationRequest registrationRequest) {
        requestHandler.handleRegistration(registrationRequest);
    }
}
