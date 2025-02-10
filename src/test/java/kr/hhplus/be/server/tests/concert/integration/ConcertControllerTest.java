package kr.hhplus.be.server.tests.concert.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.concert.dto.*;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.interfaces.api.concert.controller.ConcertController;
import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertService concertService;

    @MockitoBean
    private QueueTokenService queueTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("콘서트 목록 조회: 파라미터에 날짜를 입력하지 않으면 조회에 실패한다.")
    @Test
    void shouldFailWhenDateIsNotProvided() throws Exception {
        // given
        String uri = "/concerts";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("콘서트 목록 조회: offset, limit을 입력하지 않아도 기본 값인 offset=0, limit=100으로 조회에 성공한다.")
    @Test
    void shouldSuccessWhenOffsetAndLimitIsNotProvided() throws Exception {
        // given
        String uri = "/concerts";
        ConcertsResult mockResult = ConcertsResult.builder()
            .concerts(List.of(
                ConcertResult.builder().concertId(1L).concertName("concert1").reservationOpenDateTime(LocalDateTime.now()).build(),
                ConcertResult.builder().concertId(2L).concertName("concert2").reservationOpenDateTime(LocalDateTime.now()).build()))
            .offset(0)
            .limit(100)
            .total(2)
            .build();

        given(concertService.getConcertsAfterDate(any(), anyInt(), anyInt())).willReturn(mockResult);
        LocalDate today = LocalDate.now();

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("date", today.toString()))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 가능한 날짜 조회: 잘못된 형식의 token으로 조회에 실패한다.")
    @Test
    void shouldFailWhenTokenIsInvalidFormat() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates";
        long concertId = 1L;
        String wrongToken = "wrong-token";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Queue-Token", wrongToken))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("예약 가능한 날짜 조회: 올바른 token으로 조회에 성공한다.")
    @Test
    void shouldSuccessWhenTokenIsValid() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates";
        long concertId = 1L;
        UUID tokenUuid = UUID.randomUUID();
        String encodedToken = QueueTokenEncoder.base64EncodeToken(tokenUuid, concertId);

        ConcertSchedulesResult mockResult = ConcertSchedulesResult.builder()
            .concertSchedules(List.of(
                ConcertScheduleResult.builder().concertId(1L).concertScheduleId(1L).concertName("concert1").eventDateTime(LocalDateTime.of(2025, 3, 30, 20, 0, 0)).build(),
                ConcertScheduleResult.builder().concertId(1L).concertScheduleId(1L).concertName("concert1").eventDateTime(LocalDateTime.of(2025, 3, 30, 20, 0, 0)).build()))
            .offset(0)
            .limit(10)
            .total(2)
            .build();
        given(queueTokenService.isTokenValid(concertId, tokenUuid)).willReturn(true);
        given(concertService.getConcertSchedules(any(Long.class), any(), any()))
                .willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Queue-Token", encodedToken))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 가능한 좌석 조회: 잘못된 형식의 날짜로 조회에 실패한다.")
    @Test
    void shouldFailWhenDateIsInvalidFormat() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        long concertId = 1L;
        String wrongDate = "20250101";
        String encodedToken = QueueTokenEncoder.base64EncodeToken(UUID.randomUUID(), concertId);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, wrongDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Queue-Token", encodedToken))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("예약 가능한 좌석 조회: 헤더에 잘못된 형식의 token으로 조회에 실패한다.")
    @Test
    void shouldFailWhenNotTokenInHeader_getSeats() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        long concertId = 1L;
        String date = "2025-01-01";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Queue-Token", ""))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("예약 가능한 좌석 조회: 올바른 날짜와 token으로 조회에 성공한다.")
    @Test
    void shouldSuccessWhenDateAndTokenIsValid() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        long concertId = 1L;
        UUID tokenUuid = UUID.randomUUID();
        String encodedToken = QueueTokenEncoder.base64EncodeToken(tokenUuid, concertId);

        String date = "2025-03-01";
        ConcertSeatsResult mockResult = ConcertSeatsResult.builder()
            .seats(List.of(
                ConcertSeatResult.builder().concertScheduleId(1L).seatId(1L).seatNo("A").isAvailable(true).price(1000).build(),
                ConcertSeatResult.builder().concertScheduleId(1L).seatId(2L).seatNo("B").isAvailable(true).price(1000).build()))
            .offset(0)
            .limit(50)
            .total(50)
            .build();

        given(queueTokenService.isTokenValid(concertId, tokenUuid)).willReturn(true);
        given(concertService.getSeatsByConcertIdAndEventDate(any(Long.class), any(), any(), any()))
                .willReturn(mockResult);


        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Queue-Token", encodedToken))
                .andExpect(status().isOk());
    }
}
