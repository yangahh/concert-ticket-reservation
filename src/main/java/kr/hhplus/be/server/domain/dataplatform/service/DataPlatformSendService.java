package kr.hhplus.be.server.domain.dataplatform.service;

import kr.hhplus.be.server.domain.dataplatform.client.DataPlatformSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataPlatformSendService {
    private final DataPlatformSender dataPlatformSender;

    public Boolean sendReservationPaymentResult() {
        return dataPlatformSender.sendData();
    }
}
