package kr.hhplus.be.server.tests.point.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.point.dto.PointResult;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.interfaces.api.point.controller.PointController;
import kr.hhplus.be.server.interfaces.api.point.dto.PointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
public class PointControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointService pointService;

    @DisplayName("포인트 충전: user id가 null일 경우 포인트 충전에 실패한다.")
    @Test
    void shouldFailWhenUserIdIsNull_chargePoint() throws Exception {
        // given
        String uri = "/points";
        String request = "{\"userId\": null, \"amount\": 10000}";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("포인트 충전: 포인트 충전에 성공한다.")
    @Test
    void shouldSuccessWhenChargePoints() throws Exception {
        // given
        String uri = "/points";
        PointRequest request = new PointRequest(1L, 10000);
        PointResult mockResult = PointResult.builder()
            .userId(1L)
            .balance(10000)
            .build();
        given(pointService.chargePoint(request.getUserId(), request.getAmount())).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("포인트 조회: 잘못된 형식의 user id인 경우 포인트 조회에 실패한다.")
    @Test
    void shouldFailWhenUserIdIsNull_getPoint() throws Exception {
        // given
        String uri = "/points";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("user_id", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("포인트 조회: 올바른 user_id로 포인트 조회에 성공한다.")
    @Test
    void shouldSuccessWhenGetBalance() throws Exception {
        // given
        String uri = "/points";
        PointResult mockResult = PointResult.builder()
            .userId(1L)
            .balance(10000)
            .build();
        given(pointService.getUserPoint(1L)).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
