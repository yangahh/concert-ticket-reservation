package kr.hhplus.be.server.domain.dataplatform.service;

import kr.hhplus.be.server.domain.dataplatform.client.DataPlatformSender;
import kr.hhplus.be.server.domain.dataplatform.entity.DataPlatformSendLog;
import kr.hhplus.be.server.domain.dataplatform.repository.DataPlatformSendLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPlatformSendService {
    private final DataPlatformSender dataPlatformSender;
    private final DataPlatformSendLogRepository dataPlatformSendLogRepository;

    @Transactional
    public Boolean sendReservationPaymentResult(Long paymentId) {
        if (dataPlatformSendLogRepository.existsByPaymentId(paymentId)) {
            log.info("[DATA-PLATFORM-SERVICE] 이미 전송된 결제 건 → 중복 전송 스킵: paymentId={}", paymentId);
            return true;
        }

        Boolean sent = dataPlatformSender.sendData();

        try {
            dataPlatformSendLogRepository.save(DataPlatformSendLog.create(paymentId));
        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 paymentId를 처리하는 스레드가 먼저 저장에 성공한 경우 (유니크 제약 위반)
            // -> 이미 다른 스레드가 처리를 완료한 것이므로 무시하고 넘어간다.
            log.warn("[DATA-PLATFORM-SERVICE] 동시 처리로 인한 중복 저장 감지, 무시함: paymentId={}", paymentId);
        }

        return sent;
    }
}
