package kr.hhplus.be.server.tests.payment.integration;

import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.tests.support.JpaRepositorySupport;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PaymentConcurrencyTest extends JpaRepositorySupport {
    @Autowired
    protected PaymentUseCase paymentUseCase;

    @Autowired
    protected ConcertService concertSeatService;

    @Autowired
    protected ReservationService reservationService;

    @Autowired
    protected PointService pointService;

    @Autowired
    protected QueueTokenService queueTokenService;

    @Autowired
    protected TimeProvider timeProvider;

    User user;
    Seat seat;
    Reservation reservation;
    UUID tokenUuid;
    Long pointId;
    int price = 10000;
    int balance = price * 10;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(User.create("test"));
        Concert concert = concertJpaRepository.save(Concert.create("test"));
        ConcertSchedule schedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, timeProvider.now(), 50));
        seat = Seat.create(schedule, "1", true, price, timeProvider.now().plusMinutes(5));
        seat.reserve(timeProvider.now());
        seat = seatJpaRepository.saveAndFlush(seat);
        reservation = Reservation.tempReserve(user, seat, LocalDateTime.now().plusMinutes(5));
        reservation = reservationJpaRepository.saveAndFlush(reservation);
        Point point = Point.create(user);
        point.plus(balance);
        point = pointJpaRepository.saveAndFlush(point);
        pointId = point.getId();

        QueueToken token = QueueToken.createWaitingToken(user.getId(), concert.getId(), timeProvider);
        token.activate();
        token = queueTokenJpaRepository.save(token);
        tokenUuid = token.getTokenUuid();
    }

    @DisplayName("사용자가 예약한 좌석에 대해 결제 요청을 동시에 30번 시도하면 한 번만 성공해야 한다.")
    @Test
    void concurrencyTest() throws InterruptedException {
        // given
        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();
        List<Exception> exceptions = new ArrayList<>();

        List<Long> threadExecutionTimes = new ArrayList<>();
        long startTime = System.nanoTime();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                long threadStartTime = System.nanoTime();

                try {
                    paymentUseCase.payForReservation(reservation.getId(), tokenUuid);
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
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
        assertThat(successCnt.get()).isEqualTo(1);
        assertThat(failCnt.get()).isEqualTo(threadCount - 1);

        Point point = pointJpaRepository.findById(pointId).get();
        assertThat(point.getBalance()).isEqualTo(balance - price);

        for (int i = 0; i < threadExecutionTimes.size(); i++) {
            log.info("thread [{}] execution time : {} s", i, threadExecutionTimes.get(i) / 1_000_000_000.0);
        }
        log.info("total execution time : {} s", totalExecutionTime / 1_000_000_000.0);
    }
}
