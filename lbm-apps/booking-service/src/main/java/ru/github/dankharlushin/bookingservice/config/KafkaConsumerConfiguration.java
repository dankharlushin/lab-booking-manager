package ru.github.dankharlushin.bookingservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationRequest;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    private final String bootstrapAddress;
    private final String consumerGroupId;

    public KafkaConsumerConfiguration(@Value("${spring.kafka.bootstrap-servers}") final String bootstrapAddress,
                                      @Value("${booking-service.registration.kafka.consumer.group-id}") final String consumerGroupId) {
        this.bootstrapAddress = bootstrapAddress;
        this.consumerGroupId = consumerGroupId;
    }

    @Bean
    public ConsumerFactory<String, BookingRegistrationRequest> bookingRegistrationConsumerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(BookingRegistrationRequest.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingRegistrationRequest> bookingRegistrationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingRegistrationRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingRegistrationConsumerFactory());
        return factory;
    }
}
