package kr.hhplus.be.server.application.dataplatform.messaging;

import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformSendService;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.DATA_PLATFORM_CONSUMER_GROUP;
import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_COMPLETED_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformKafkaConsumer {
    private final DataPlatformSendService dataPlatformSendService;

    @KafkaListener(
        topics = PAYMENT_COMPLETED_TOPIC,
        groupId = DATA_PLATFORM_CONSUMER_GROUP
    )
    public void consume(PaymentCompletedEvent event) {
        // event -> DTO 변환 (생략)
        log.info("[DATA-PLATFORM-SERVICE][DataPlatformKafkaConsumer] 결제 데이터 전송: 결제 ID - {}", event.paymentId());
        dataPlatformSendService.sendReservationPaymentResult();
        log.info("[DATA-PLATFORM-SERVICE][DataPlatformKafkaConsumer] 결제 데이터 전송 완료");
    }
}
