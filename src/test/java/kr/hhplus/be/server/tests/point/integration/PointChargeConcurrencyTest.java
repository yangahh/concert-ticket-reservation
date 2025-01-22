package kr.hhplus.be.server.tests.point.integration;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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

    Long userId;
    Long pointId;

    @BeforeEach
    void setUp() {
        User user = userJpaRepository.save(User.create("test_user"));
        userId = user.getId();
        Point point = pointJpaRepository.save(Point.create(user));
        pointId = point.getId();
    }

    @DisplayName("포인트가 0인 사용자가 10,000 포인트 충전을 동시에 10번 충전을 요청할 때, 10,000 포인트만 충전되어야 한다.")
    @Test
    void Test() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Boolean> results = new ArrayList<>(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(userId, 10_000);
                    results.add(true);

                } catch (Exception e) {
                    results.add(false);
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        long successCnt = results.stream().filter(Boolean::booleanValue).count();
        long failCnt = results.stream().filter(result -> !result).count();
        assertThat(successCnt).isEqualTo(1);
        assertThat(failCnt).isEqualTo(threadCount - 1);

        Point point = pointJpaRepository.findById(pointId).get();
        assertThat(point.getBalance()).isEqualTo(10_000);


    }
}
