package kr.hhplus.be.server.concert.unit;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ConcertSeatEntityTest {

    @DisplayName("isAvailableNow: 이미 누군가 예약한 좌석이지만 임시 예약 만료시간이 지났다면 예약 가능하다.")
    @Test
    void isAvailableNow_shouldReturnTrueWhenExpiredTempReservation() {
        // given
        LocalDateTime mockPastTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        Concert concert = Concert.create("test");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.now(), 1);
        Seat seatForExpiredReservation = Seat.create(concertSchedule, "A1", false, 10000, mockPastTime);

        // when
        boolean result = seatForExpiredReservation.isAvailableNow(LocalDateTime.now());

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("isAvailableNow: 임시 예약 만료시간이 지나지 않았다면 예약 불가능하다.")
    @Test
    void isAvailableNow_shouldReturnFalseWhenNotExpiredTempReservation() {
        // given
        Concert concert = Concert.create("test");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.now(), 1);
        Seat seatForNotExpiredReservation = Seat.create(concertSchedule, "A1", false, 10000, LocalDateTime.now().plusMinutes(10));

        // when
        boolean result = seatForNotExpiredReservation.isAvailableNow(LocalDateTime.now());

        // then
        assertThat(result).isFalse();
    }
}
