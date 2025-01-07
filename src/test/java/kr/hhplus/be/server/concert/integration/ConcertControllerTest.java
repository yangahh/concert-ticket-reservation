package kr.hhplus.be.server.concert.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.interfaces.api.concert.controller.ConcertController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertService concertService;

    @Autowired
    private ObjectMapper objectMapper;

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
                        .header("token", wrongToken))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("예약 가능한 날짜 조회: 올바른 token으로 조회에 성공한다.")
    @Test
    void shouldSuccessWhenTokenIsValid() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates";
        long concertId = 1L;

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("token", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 가능한 좌석 조회: 잘못된 형식의 날짜로 조회에 실패한다.")
    @Test
    void shouldFailWhenDateIsInvalidFormat() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        long concertId = 1L;
        String wrongDate = "20250101";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, wrongDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("token", UUID.randomUUID().toString()))
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
                        .header("token", ""))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("예약 가능한 좌석 조회: 올바른 날짜와 token으로 조회에 성공한다.")
    @Test
    void shouldSuccessWhenDateAndTokenIsValid() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        long concertId = 1L;
        String date = "2025-01-01";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("token", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }
}
