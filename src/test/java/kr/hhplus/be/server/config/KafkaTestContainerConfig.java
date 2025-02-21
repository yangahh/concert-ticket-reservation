package kr.hhplus.be.server.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@Configuration
public class KafkaTestContainerConfig {
    public static final ConfluentKafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.8.0");

        KAFKA_CONTAINER.start();
        System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());

    }

    @PreDestroy
    public void preDestroy() {
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }

    @Bean
    public ConfluentKafkaContainer kafkaContainer() {
        return KAFKA_CONTAINER;
    }
}
