package kr.hhplus.be.server.tests.point.integration;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class PointChargeConcurrencyTest {
    @Autowired
    PointService pointService;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PointJpaRepository pointJpaRepository;

    @Autowired
    PointHistoryJpaRepository pointHistoryJpaRepository;

    Long userId;
    Long pointId;

    @BeforeEach
    void setUp() {
        User user = userJpaRepository.save(User.create("test_user"));
        userId = user.getId();
        Point point = pointJpaRepository.save(Point.create(user));
        pointId = point.getId();
    }

    @AfterEach
    void tearDown() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @DisplayName("포인트가 0인 사용자가 10,000 포인트 충전을 동시에 30번 충전을 요청할 때, 10번의 요청이 모두 처리되어야 한다.")
    @Test
    void concurrencyTest() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();
        List<Exception> exceptions = new ArrayList<>();
        int chargeAmount = 10_000;

        List<Long> threadExecutionTimes = new ArrayList<>();
        long startTime = System.nanoTime();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                long threadStartTime = System.nanoTime();
                try {
                    pointService.chargePoint(userId, chargeAmount);
                    successCnt.getAndIncrement();

                } catch (Exception e) {
                    failCnt.getAndIncrement();
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                    long threadEndTime = System.nanoTime();
                    threadExecutionTimes.add(threadEndTime - threadStartTime);
                }
            });
        }
        latch.await();
        executorService.shutdown();

        long endTime = System.nanoTime();
        long totalExecutionTime = endTime - startTime;

        // then
        assertThat(successCnt.get()).isEqualTo(threadCount);
        assertThat(failCnt.get()).isEqualTo(0);

        Point point = pointJpaRepository.findById(pointId).get();
        assertThat(point.getBalance()).isEqualTo(chargeAmount * threadCount);

        for (int i = 0; i < threadExecutionTimes.size(); i++) {
            log.info("thread [{}] execution time : {} s", i, threadExecutionTimes.get(i) / 1_000_000_000.0);
        }
        log.info("total execution time : {} s", totalExecutionTime / 1_000_000_000.0);
    }
}
