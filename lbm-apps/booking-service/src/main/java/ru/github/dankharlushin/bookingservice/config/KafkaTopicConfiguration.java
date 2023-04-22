package ru.github.dankharlushin.bookingservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfiguration {

    private final String bootstrapAddress;
    private final String bookingRegistrationTopicName;
    private final Integer bookingRegistrationTopicPartitionNum;
    private final Short bookingRegistrationTopicReplicFactor;

    public KafkaTopicConfiguration(@Value("${spring.kafka.bootstrap-servers}")
                                   final String bootstrapAddress,
                                   @Value("${booking-service.registration.kafka.topic.name}")
                                   final String bookingRegistrationTopicName,
                                   @Value("${booking-service.registration.kafka.topic.partions}")
                                   final Integer bookingRegistrationTopicPartitionNum,
                                   @Value("${booking-service.registration.kafka.topic.replication-factor}")
                                   final Short bookingRegistrationTopicReplicFactor) {
        this.bootstrapAddress = bootstrapAddress;
        this.bookingRegistrationTopicName = bookingRegistrationTopicName;
        this.bookingRegistrationTopicPartitionNum = bookingRegistrationTopicPartitionNum;
        this.bookingRegistrationTopicReplicFactor = bookingRegistrationTopicReplicFactor;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic registrationTopic() {
        return new NewTopic(bookingRegistrationTopicName, bookingRegistrationTopicPartitionNum, bookingRegistrationTopicReplicFactor);
    }
}
