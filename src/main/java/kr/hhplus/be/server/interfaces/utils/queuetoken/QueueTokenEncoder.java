package kr.hhplus.be.server.interfaces.utils.queuetoken;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenCoreInfo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class QueueTokenEncoder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String base64EncodeToken(UUID tokenUuid, Long concertId) {
        try {
            QueueTokenCoreInfo coreInfo = new QueueTokenCoreInfo(concertId, tokenUuid);
            String jsonString = objectMapper.writeValueAsString(coreInfo);

            return Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
