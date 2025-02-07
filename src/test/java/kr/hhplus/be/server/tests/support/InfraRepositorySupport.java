package kr.hhplus.be.server.tests.support;

import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.ActiveTokenRedisTemplate;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.WaitingTokenRedisTemplate;
import kr.hhplus.be.server.infrastructure.reservation.repository.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class InfraRepositorySupport {
    @Autowired
    protected UserJpaRepository userJpaRepository;

    @Autowired
    protected ConcertJpaRepository concertJpaRepository;

    @Autowired
    protected ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    protected SeatJpaRepository seatJpaRepository;

    @Autowired
    protected ReservationJpaRepository reservationJpaRepository;

    @Autowired
    protected PointJpaRepository pointJpaRepository;

    @Autowired
    protected PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected ActiveTokenRedisTemplate activeTokenRedisTemplate;

    @Autowired
    protected WaitingTokenRedisTemplate waitingTokenRedisTemplate;

    @Autowired
    protected QueueTokenRepository queueTokenRepository;

    @AfterEach
    void tearDown() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
        seatJpaRepository.deleteAll();
        concertScheduleJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
