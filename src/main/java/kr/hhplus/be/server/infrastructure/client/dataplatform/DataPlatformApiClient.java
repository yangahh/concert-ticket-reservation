package kr.hhplus.be.server.infrastructure.client.dataplatform;

import kr.hhplus.be.server.domain.dataplatform.client.DataPlatformSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformApiClient implements DataPlatformSender {
    @Override
    public Boolean sendData() {
        log.info("=============sendDate 호출==============");
        return true;
    }
}
