package kr.hhplus.be.server.infrastructure.messaging.dataplatform;

import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.infrastructure.client.dataplatform.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.DATA_PLATFORM_CONSUMER_GROUP;
import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_COMPLETED_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformKafkaConsumer {
    private final DataPlatformClient dataPlatformClient;

    @KafkaListener(
        topics = PAYMENT_COMPLETED_TOPIC,
        groupId = DATA_PLATFORM_CONSUMER_GROUP
    )
    @Async
    public void consume(PaymentCompletedEvent event) {
        log.info("[DATA-PLATFORM-SERVICE][DataPlatformKafkaConsumer] 결제 데이터 전송: 결제 ID - {}", event.paymentId());
        dataPlatformClient.sendData();
        log.info("[DATA-PLATFORM-SERVICE][DataPlatformKafkaConsumer] 결제 데이터 전송 완료");
    }
}
