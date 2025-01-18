package kr.hhplus.be.server.domain.reservation.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
public record ReservationCommand (
    Long userId,
    Long seatId,
    int price,
    LocalDateTime tempReservationExpiredAt

) {
    public static ReservationCommand of(Long userId, Long seatId, int price, LocalDateTime tempReservationExpiredAt) {
        return ReservationCommand.builder()
                .userId(userId)
                .seatId(seatId)
                .price(price)
                .tempReservationExpiredAt(tempReservationExpiredAt)
                .build();
    }
}
