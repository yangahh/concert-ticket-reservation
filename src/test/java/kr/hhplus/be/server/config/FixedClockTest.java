package kr.hhplus.be.server.config;

import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(FixedClockConfig.class)
public class FixedClockTest {
    @Autowired
    private TimeProvider timeProvider;  // DefaultTimeProvider 주입, FixedClockConfig에 의해 Clock은 test-clock으로 변경

    @Test
    void testWithFixedClock() {
        LocalDateTime expected = LocalDateTime.of(2025, 1, 1, 0, 0);
        assertEquals(expected, timeProvider.now());
    }
}
