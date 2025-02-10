package kr.hhplus.be.server.interfaces.utils.queuetoken;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queuetoken.exception.InvalidToken;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenCoreInfo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class QueueTokenDecoder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static QueueTokenCoreInfo base64DecodeToken(String encodedToken) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
            String jsonString = new String(decodedBytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonString, QueueTokenCoreInfo.class);
        } catch (IllegalArgumentException e) {
            throw new InvalidToken("Invalid Base64 encoded token: " + encodedToken);
        } catch (JsonProcessingException e) {
            throw new InvalidToken("Invalid token format: " + encodedToken);
        }
    }
}
