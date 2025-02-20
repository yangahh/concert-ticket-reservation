package kr.hhplus.be.server.infrastructure.messaging.config;

public final class KafkaConstance {
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String PAYMENT_CONSUMER_GROUP = "payment-consumer-group";
    public static final String DATA_PLATFORM_CONSUMER_GROUP = "data-platform-consumer-group";


    private KafkaConstance() {}  // Prevent instantiation

}
