package kr.hhplus.be.server.tests.reservation.integration;

import kr.hhplus.be.server.application.reservation.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.tests.support.JpaRepositorySupport;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class ReservationConcurrencyTest extends JpaRepositorySupport {
    @Autowired
    ReservationUseCase reservationUseCase;

    @Autowired
    ReservationService reservationService;

    @Autowired
    ConcertService concertService;

    @Autowired
    TimeProvider timeProvider;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    List<Long> userIds = new ArrayList<>();
    Long seatId;

    int threadCount = 30;

    @BeforeEach
    void setUp() {
        Concert concert = concertJpaRepository.save(Concert.create("test"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.now().plusDays(1), 50));
        Seat seat = seatJpaRepository.save(Seat.create(concertSchedule, "1", true, 1000, LocalDateTime.now().plusMinutes(5)));
        seatId = seat.getId();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            User user = User.create("test_user");
            users.add(user);
        }
        userJpaRepository.saveAllAndFlush(users);

        for (User user : users) {
            userIds.add(user.getId());
        }
    }

    @DisplayName("10명의 사용자가 동시에 같은 좌석에 예약을 요청하면, 예약은 단 한 건만 성공해야 하고 나머지는 실패해야 한다.")
    @Test
    void shouldSuccessOnlyOneReservationWhen100Requests() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();
        List<Exception> exceptions = new ArrayList<>();

        List<Long> threadExecutionTimes = new ArrayList<>();
        long startTime = System.nanoTime();

        // when
        for (Long userId : userIds) {
            executorService.submit(() -> {
                long threadStartTime = System.nanoTime();
                try {
                    reservationUseCase.makeTempReservation(userId, seatId);
                    successCnt.getAndIncrement();
                } catch (Exception e) {
                    exceptions.add(e);
                    failCnt.getAndIncrement();
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
        assertThat(successCnt.get()).isEqualTo(1);
        assertThat(failCnt.get()).isEqualTo(threadCount - 1);

        List<Reservation> reservations = reservationJpaRepository.findAll();
        assertThat(reservations.size()).isEqualTo(1);

        for (Long threadExecutionTime : threadExecutionTimes) {
            log.info("thread execution time : {} s", threadExecutionTime / 1_000_000_000.0);
        }
        log.info("total execution time : {} s", totalExecutionTime / 1_000_000_000.0);
    }
}
