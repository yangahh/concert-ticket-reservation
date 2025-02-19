package kr.hhplus.be.server.tests.concert.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.infrastructure.repository.queuetoken.ActiveTokenRedisTemplate;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationData;
import kr.hhplus.be.server.interfaces.api.concert.dto.ConcertResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.ConcertScheduleDateResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenEncoder;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/concert_data.sql")
public class ConcertIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueueTokenService queueTokenService;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    TimeProvider timeProvider;

    QueueToken token;
    long userId = 1L;
    long concertId = 1L;

    @BeforeEach
    void setUp() {
        token = QueueToken.createWaitingToken(userId, concertId, timeProvider);
        token.activate();
        token = queueTokenRepository.save(token);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(ActiveTokenRedisTemplate.getKey(concertId));
    }

    @DisplayName("파라미터로 받은 날짜 이후의 콘서트 목록을 조회 성공 통합 테스트")
    @Test
    void getConcertsTest() throws Exception {
        // given
        String uri = "/concerts";

        // when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .param("date", "2025-02-01")  // concert_data.sql 기준 2025-02-01 이후 날짜로 2개의 콘서트가 있음
                .param("offset", "0")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        BaseResponse<PaginationData<ConcertResponse>> response = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            new TypeReference<>() {});
        assertThat(response.getData().getCount()).isEqualTo(2);
    }


    @DisplayName("concertID로 해당 콘서트의 날짜 목록을 조회 성공 통합 테스트")
    @Test
    void getConcertSchedulesTest() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates";
        String encodedToken = QueueTokenEncoder.base64EncodeToken(token.getTokenUuid(), concertId);
        // concert_data.sql 기준 concertId = 1인 schedule이 3개 있음

        // when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Queue-Token", encodedToken))
            .andExpect(status().isOk())
            .andReturn();

        BaseResponse<PaginationData<ConcertScheduleDateResponse>> response = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            new TypeReference<>() {});
        assertThat(response.getData().getCount()).isEqualTo(3);

    }

    @DisplayName("concertId와 date로 해당 콘서트의 좌석 목록 조회 성공 통합 테스트")
    @Test
    void getConcertSeatsTest() throws Exception {
        // given
        String uri = "/concerts/{concertId}/dates/{date}/seats";
        String encodedToken = QueueTokenEncoder.base64EncodeToken(token.getTokenUuid(), concertId);
        String date = "2025-03-31"; // concert_data.sql 기준 해당 날짜에 50개 좌석

        // when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri, concertId, date)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Queue-Token", encodedToken))
            .andExpect(status().isOk()).andReturn();

        BaseResponse<PaginationData<SeatResponse>> response = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            new TypeReference<>() {});

        assertThat(response.getData().getCount()).isEqualTo(50);
    }

}
