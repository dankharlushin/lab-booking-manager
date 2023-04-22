package ru.github.dankharlushin.bookingservice.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;

import java.util.UUID;

@Component
public class BookingRegistrationRequestProducer {

    private final KafkaTemplate<String, BookingRegistrationRequest> kafkaTemplate;
    private final String topicName;

    public BookingRegistrationRequestProducer(final KafkaTemplate<String, BookingRegistrationRequest> kafkaTemplate,
                                              @Value("${booking-service.registration.kafka.topic.name}") final String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendRequest(final BookingRegistrationRequest registrationRequest) {
        kafkaTemplate.send(topicName, UUID.randomUUID().toString(), registrationRequest);
    }
}
