package kr.hhplus.be.server.interfaces.eventlistener;

import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.infrastructure.client.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final DataPlatformClient dataPlatformClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCompletedEvent(PaymentCompletedEvent event) {
        try {
            log.info("결제 정보 데이터 플랫폼 전송: 사용자 ID = {}, 결제 ID = {}, 예약 ID = {}", event.userId(), event.paymentId(), event.reservationId());
            dataPlatformClient.sendData();
            log.info("결제 정보 데이터 플랫폼 전송 완료");
        } catch (Exception e) {
            log.error("결제 정보 데이터 플랫폼 전송 오류: {}", e.getMessage());  // 데이터 전송 실패 시 로깅 처리(결제에 영향 X)
        }
    }
}
