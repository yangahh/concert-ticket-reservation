package kr.hhplus.be.server.tests.payment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.concert.dto.ConcertScheduleResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatResult;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.payment.controller.PaymentController;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
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

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QueueTokenService queueTokenService;

    @MockitoBean
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        given(queueTokenService.isTokenValid(any(UUID.class))).willReturn(true);
    }

    @DisplayName("결제 요청: 예약 ID가 없어서 결제에 실패한다.")
    @Test
    void shouldFailWhenReservationIdIsNull() throws Exception {
        // given
        String uri = "/payments";
        PaymentRequest request = new PaymentRequest(null);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("결제 요청: 헤더에 대기열 토큰이 없는 경우 결제에 실패한다.")
    @Test
    void shouldFailWhenTokenIsNull() throws Exception {
        // given
        String uri = "/payments";
        PaymentRequest request = new PaymentRequest(1L);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("결제 요청: 유효하지 않은 토큰으로 결제에 실패한다.")
    @Test
    void shouldFailWhenInvalidToken() throws Exception {
        // given
        String uri = "/payments";
        UUID token = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(1L);
        given(queueTokenService.isTokenValid(token)).willReturn(false);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", token.toString()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("결제 요청: 올바른 파라미터와 유효한 토큰으로 결제에 성공한다.")
    @Test
    void shouldSuccessWhenValidParams() throws Exception {
        // given
        String uri = "/payments";
        PaymentRequest request = new PaymentRequest(1L);
        UUID token = UUID.randomUUID();
        given(queueTokenService.isTokenValid(token)).willReturn(true);
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
            .status(ReservationStatus.CONFIRMED)
            .reservedAt(LocalDateTime.now())
            .tempReservationExpiredAt(LocalDateTime.now().plusMinutes(5))
            .confirmedAt(LocalDateTime.now())
            .concertScheduleResult(mockSchedule)
            .concertSeatResult(mockSeat)
            .build();
        given(paymentUseCase.payForReservation(1L, token)).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Queue-Token", token.toString()))
                .andExpect(status().isOk());
    }
}
