package kr.hhplus.be.server.infrastructure.client.dataplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformClient {
    public Boolean sendData() {
        log.info("=============sendDate 호출==============");
        return true;
    }
}
