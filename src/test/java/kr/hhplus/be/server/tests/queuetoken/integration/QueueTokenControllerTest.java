package kr.hhplus.be.server.tests.queuetoken.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenPositionResult;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenResult;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.interfaces.api.queuetoken.controller.QueueTokenController;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QueueTokenController.class)
public class QueueTokenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QueueTokenService queueTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("토큰 발급: 잘못된 형식의 userId로 발급에 실패한다.")
    @Test
    void shouldFailWhenInvalidUserId() throws Exception {
        // given
        String uri = "/queue/token";
        QueueTokenRequest request = new QueueTokenRequest(0L, 100L);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("토큰 발급: 잘못된 형식의 concertId로 발급에 실패한다.")
    @Test
    void shouldFailWhenInvalidConcertId() throws Exception {
        // given
        String uri = "/queue/token";
        QueueTokenRequest request = new QueueTokenRequest(1L, -1L);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("토큰 발급: 올바른 userId로 발급에 성공한다.")
    @Test
    void shouldIssueTokenWhenUserIdIsValid() throws Exception {
        // given
        String uri = "/queue/token";
        QueueTokenRequest request = new QueueTokenRequest(1L, 100L);
        QueueTokenResult mockResult = QueueTokenResult.builder()
                .tokenUuid(UUID.randomUUID())
                .userId(1L)
                .concertId(100L)
                .isActive(false)
                .build();
        given(queueTokenService.issueWaitingToken(1L, 100L)).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @DisplayName("대기 순번 조회: 잘못된 형식의 token(uuid)으로 조회에 실패한다.")
    @Test
    void shouldFailWhenTokenIsInvalidUUID() throws Exception {
        // given
        String uri = "/queue/position";
        String wrongToken = "wrong-token";

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .param("token", wrongToken))
            .andExpect(status().isBadRequest());
    }

    @DisplayName("대기 순번 조회: 올바른 token(uuid)으로 조회에 성공한다.")
    @Test
    void shouldGetPositionWhenTokenIsValidUUID() throws Exception {
        // given
        String uri = "/queue/position";
        UUID validToken = UUID.randomUUID();
        Long concertId = 100L;

        QueueTokenPositionResult mockResult = QueueTokenPositionResult.builder()
                .tokenUuid(validToken)
                .userId(1L)
                .concertId(concertId)
                .position(1)
                .remainingSeconds(1)
                .isActive(false)
                .build();
        given(queueTokenService.getWaitingTokenPositionAndRemainingTime(concertId, validToken)).willReturn(mockResult);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .param("token", QueueTokenEncoder.base64EncodeToken(validToken, concertId)))
            .andExpect(status().isOk());
    }
}
