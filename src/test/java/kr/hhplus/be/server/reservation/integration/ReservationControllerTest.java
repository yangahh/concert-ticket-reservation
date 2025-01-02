package kr.hhplus.be.server.reservation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.interfaces.api.concert.controller.ConcertController;
import kr.hhplus.be.server.interfaces.api.reservation.controller.ReservationController;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                        .header("token", UUID.randomUUID().toString()))
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
                        .header("token", UUID.randomUUID().toString()))
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
                        .header("token", "wrong-token"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 요청: 올바른 userId와 seatId와 올바른 token이 모두 있어서 예약에 성공한다.")
    @Test
    void shouldSuccessWhenAllParamsAreValid() throws Exception {
        // given
        String uri = "/reservations";
        ReservationRequest request = new ReservationRequest(1L, 1L);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("token", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }
}
