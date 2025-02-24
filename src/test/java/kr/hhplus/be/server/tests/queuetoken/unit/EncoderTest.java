package kr.hhplus.be.server.tests.queuetoken.unit;

import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class EncoderTest {
    @DisplayName("Encoder Test")
    @Test
    void Test() {
        String encodeToken = QueueTokenEncoder.base64EncodeToken(UUID.fromString("f60805bd-dce6-42a4-981c-2bfc049bbb22"), 1L);
        System.out.println(encodeToken);

    }
}
