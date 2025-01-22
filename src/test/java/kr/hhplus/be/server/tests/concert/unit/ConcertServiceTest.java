package kr.hhplus.be.server.tests.concert.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.dto.ConcertSchedulesResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatsResult;
import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {
    @InjectMocks
    private ConcertService sut;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private TimeProvider timeProvider;

    @DisplayName("날짜 조회: 존재하지 않는 콘서트 id로 조회하면 예외가 발생한다.")
    @Test
    void shouldThrowEntityNotFoundExceptionWhenNotExistingConcertId() {
        // given
        given(concertRepository.findConcertById(anyLong())).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> sut.getConcertSchedules(1L, Optional.empty(), Optional.empty()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Concert not found");
    }

    @Test
    @DisplayName("콘서트 날짜 목록 조회 시, 0건이면 빈 목록을 반환한다.")
    void shouldReturnEmptyListWhenNoConcertSchedules() {
        // given
        Long concertId = 1L;
        int offset = 0;
        int limit = 10;
        Page<ConcertSchedule> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), 0);
        Concert concert = Concert.create("test");
        given(concertRepository.findConcertById(anyLong())).willReturn(Optional.of(concert));
        given(concertRepository.findConcertSchedulesByConcertId(concertId, offset, limit)).willReturn(mockPage);

        // when
        ConcertSchedulesResult result = sut.getConcertSchedules(concertId, Optional.of(offset), Optional.of(limit));

        // then
        assertThat(result.concertSchedules()).isEmpty();
    }

    @DisplayName("콘서트 좌석 조회 시, 콘서트 ID가 없을 때 예외가 발생한다")
    @Test
    void shouldThrowEntityNotFoundExceptionWhenNotExistingConcertIdForSeats() {
        // given
        Long concertId = 1L;
        given(concertRepository.findConcertById(concertId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.getSeatsByConcertIdAndEventDate(1L, LocalDate.now().plusDays(1), Optional.empty(), Optional.empty()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Concert not found");
    }

    @DisplayName("콘서트 좌석 조회 시, 조회하려는 날짜가 현재 시간보다 이전이면 예외가 발생한다")
    @Test
    void shouldThrowUnprocessableEntityExceptionWhenEventDateIsPast() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        LocalDate searchDate = LocalDate.now().minusDays(1);
        Concert concert = Concert.create("test");
        given(concertRepository.findConcertById(anyLong())).willReturn(Optional.of(concert));

        // when & then
        assertThatThrownBy(() -> sut.getSeatsByConcertIdAndEventDate(1L, searchDate, Optional.empty(), Optional.empty()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("is past");
    }

    @Test
    @DisplayName("콘서트 좌석 조회 시, 결과가 없으면 Page 객체가 비어있어야 한다.")
    void testGetSeatsByConcertIdAndEventDateWithNoResults() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        Long concertId = 1L;
        LocalDate searchDate = LocalDate.now().plusDays(1);
        int offset = 0;
        int limit = 10;
        Page<Seat> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), 0); // 결과가 0건인 Page 객체 생성

        Concert concert = Concert.create("test");
        given(concertRepository.findConcertById(concertId)).willReturn(Optional.of(concert));
        given(concertRepository.findSeatsByConcertSchedule(concertId, searchDate, offset, limit)).willReturn(mockPage);

        // when
        ConcertSeatsResult result = sut.getSeatsByConcertIdAndEventDate(concertId, searchDate, Optional.of(offset), Optional.of(limit));

        // then
        assertThat(result.seats()).isEmpty();
    }

    @Test
    @DisplayName("예약을 위해 좌석 조회 시, 존재하지 않는 좌석 ID로 조회하면 예외가 발생한다.")
    void shouldThrowEntityNotFoundExceptionWhenNotExistingSeatId() {
        // given
        given(concertRepository.findSeatByIdForUpdate(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.reserveSeat(1L, LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Seat not found");
    }


    @Test
    @DisplayName("예약을 위해 좌석 조회 시, 좌석이 이미 예약되어 있으면 예외가 발생한다.")
    void shouldThrowEntityNotFoundExceptionWhenSeatIsAlreadyReserved() {
        // given
        Concert concert = Concert.create("test");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.now(), 1);
        Seat seat = Seat.create(concertSchedule, "A1", false, 10000, LocalDateTime.now().plusMinutes(5));
        given(concertRepository.findSeatByIdForUpdate(1L)).willReturn(Optional.of(seat));

        // when & then
        assertThatThrownBy(() -> sut.reserveSeat(1L, LocalDateTime.now()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("Seat is already reserved");
    }

    @Test
    @DisplayName("예약을 위해 좌석 조회 시, 좌석이 예약 가능하면 좌석의 isAvailable이 false로 변경된다.")
    void shouldReturnTrueWhenSeatIsAvailable() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Concert concert = Concert.create("concert1");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2025, 3, 10, 18, 0, 0), 50);

        Seat seat = Seat.create(concertSchedule, "A1", true, 10000, now.plusMinutes(5));
        Seat spySeat = Mockito.spy(seat);
        ConcertSchedule spyConcertSchedule = spy(concertSchedule);

        doReturn(100L).when(spySeat).getId();
        doReturn(10L).when(spyConcertSchedule).getId();
        doReturn(spyConcertSchedule).when(spySeat).getConcertSchedule();

        given(concertRepository.findSeatByIdForUpdate(1L)).willReturn(Optional.of(spySeat));
        given(concertRepository.saveSeat(spySeat)).willReturn(spySeat);

        // when
        ReservationSeatInfo reservationSeatInfo = sut.reserveSeat(1L, now);

        // then
        assertThat(reservationSeatInfo.isAvailable()).isFalse();
    }
}
