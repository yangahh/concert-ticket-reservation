package kr.hhplus.be.server.utils.time;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
