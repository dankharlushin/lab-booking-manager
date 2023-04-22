package ru.github.dankharlushin.bookingservice.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;

import java.util.UUID;

@Component
public class BookingRegistrationRequestProducer {

    private static final Logger logger = LoggerFactory.getLogger(BookingRegistrationRequestProducer.class);

    private final KafkaTemplate<String, BookingRegistrationRequest> kafkaTemplate;
    private final String topicName;

    public BookingRegistrationRequestProducer(final KafkaTemplate<String, BookingRegistrationRequest> kafkaTemplate,
                                              @Value("${booking-service.registration.kafka.topic.name}") final String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendRequest(final BookingRegistrationRequest registrationRequest) {
        final String key = UUID.randomUUID().toString();
        kafkaTemplate.send(topicName, key, registrationRequest);
        logger.info("Registration request [{}] was produced wit key [{}]", registrationRequest, key);
    }
}
