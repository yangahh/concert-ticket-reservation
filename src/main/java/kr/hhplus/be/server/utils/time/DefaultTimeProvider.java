package kr.hhplus.be.server.utils.time;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DefaultTimeProvider implements TimeProvider {
    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
