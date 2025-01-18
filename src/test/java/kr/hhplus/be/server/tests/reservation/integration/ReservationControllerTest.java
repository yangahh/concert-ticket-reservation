package kr.hhplus.be.server.tests.reservation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.reservation.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.concert.dto.ConcertScheduleResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatResult;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.controller.ReservationController;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QueueTokenService queueTokenService;

    @MockitoBean
    private ReservationUseCase reservationUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        given(queueTokenService.isTokenValid(any(UUID.class))).willReturn(true);
    }

    @DisplayName("예약 요청: userId가 없어서 예약에 실패한다.")
    @Test
    void shouldFailWhenUserIdIsNull() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(null, 1L);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 요청: seatId가 없어서 예약에 실패한다.")
    @Test
    void shouldFailWhenSeatIdIsNull() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(1L, null);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 요청: 올바른 userId와 seatId가 있지만 잘못된 token으로 예약 요청에 실패한다.")
    @Test
    void shouldFailWhenWrongToken() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(1L, 1L);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", "wrong-token"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 요청: 올바른 userId와 seatId가 있지만 token이 유효하지 않아서 예약에 실패한다.")
    @Test
    void shouldFailWhenTokenIsInvalid() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(1L, 1L);
        given(queueTokenService.isTokenValid(any(UUID.class))).willReturn(false);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("예약 요청: 올바른 userId와 seatId와 유효한 token이 모두 있어서 예약에 성공한다.")
    @Test
    void shouldSuccessWhenAllParamsAreValid() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(1L, 1L);
        ConcertScheduleResult mockSchedule = ConcertScheduleResult.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .build();
        ConcertSeatResult mockSeat = ConcertSeatResult.builder()
                .concertScheduleId(1L)
                .isAvailable(false)
                .build();
        ReservationResult mockResult = ReservationResult.builder()
                .reservationId(1L)
                .userId(1L)
                .price(1000)
                .status(ReservationStatus.PENDING_PAYMENT)
                .reservedAt(LocalDateTime.now())
                .tempReservationExpiredAt(LocalDateTime.now().plusMinutes(5))
                .concertScheduleResult(mockSchedule)
                .concertSeatResult(mockSeat)
                .build();
        given(reservationUseCase.makeTempReservation(1L, 1L)).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }
}
