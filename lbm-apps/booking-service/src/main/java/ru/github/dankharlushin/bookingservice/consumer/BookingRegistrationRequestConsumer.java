package ru.github.dankharlushin.bookingservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import ru.github.dankharlushin.bookingservice.handler.BookingRequestHandler;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;

@Component
public class BookingRegistrationRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BookingRegistrationRequestConsumer.class);

    private final BookingRequestHandler requestHandler;

    public BookingRegistrationRequestConsumer(final BookingRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @KafkaListener(topics = "${booking-service.registration.kafka.topic.name}",
            containerFactory = "bookingRegistrationKafkaListenerContainerFactory"
    )
    @RetryableTopic(
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void bookingRegistrationListener(final BookingRegistrationRequest registrationRequest) {
        logger.info("Consuming registration request [{}]", registrationRequest);
        requestHandler.handleRegistration(registrationRequest);
    }
}
