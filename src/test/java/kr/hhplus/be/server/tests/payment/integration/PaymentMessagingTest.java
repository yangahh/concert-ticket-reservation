package kr.hhplus.be.server.tests.payment.integration;

import kr.hhplus.be.server.application.payment.scheduler.PublishingEventRetryScheduler;
import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.vo.PaymentStatus;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.repository.payment.PaymentCompletedEventOutboxJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.payment.PaymentJpaRepository;
import kr.hhplus.be.server.tests.support.InfraRepositorySupport;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_COMPLETED_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
public class PaymentMessagingTest extends InfraRepositorySupport {
    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private PublishingEventRetryScheduler publishingEventRetryScheduler;

    @Autowired
    private QueueTokenService queueTokenService;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private PaymentCompletedEventOutboxJpaRepository paymentCompletedEventOutboxJpaRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    private BlockingQueue<PaymentCompletedEvent> receivedMessages;
    User user;
    QueueToken token;
    Point userPoint;
    Concert concert;
    ConcertSchedule concertSchedule;
    Reservation reservation;

    @BeforeEach
    void setUp() {
        // given
        concert = concertJpaRepository.save(Concert.create("test", timeProvider.now()));

        user = userJpaRepository.save(User.create("test"));
        token = QueueToken.createWaitingToken(user.getId(), concert.getId(), timeProvider);
        token.activate();
        queueTokenRepository.save(token);

        userPoint = Point.create(user);
        userPoint.plus(1_000_000);
        userPoint = pointJpaRepository.save(userPoint);

        concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, timeProvider.now().plusDays(1), 50));

        Seat seat = Seat.create(concertSchedule, "A", true, 10000, timeProvider.now().plusMinutes(5));
        seat.reserve(timeProvider.now());
        seat = seatJpaRepository.save(seat);
        concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, timeProvider.now().plusDays(1), 50));

        reservation = reservationJpaRepository.save(Reservation.tempReserve(user, seat, timeProvider.now().plusMinutes(5)));

        receivedMessages = new LinkedBlockingQueue<>();

    }

    @AfterEach
    void tearDown() {
        paymentJpaRepository.deleteAll();
        paymentCompletedEventOutboxJpaRepository.deleteAll();
    }

    @DisplayName("PaymentCompletedEvent 관련 통합 테스트: " +
        "결제 완료에 대한 비즈니스 로직이 실행된 후 결제 완료 이벤트 리스너에서 outbox에 해당 이벤트를 저장하고 kafka로 메세지를 정상적으로 발행한다.")
    @Test
    void successSaveOutBoxAndPublishKafkaMessage() {
        // when
        paymentUseCase.payForReservation(reservation.getId(), token.getTokenUuid());

        // then
        // 1. 결제 데이터 생성 확인
        List<Payment> completedPayment = paymentJpaRepository.findByReservationIdAndStatus(reservation.getId(), PaymentStatus.COMPLETED);
        assertThat(completedPayment.size()).isEqualTo(1);

        // given2
        Long paymentId = completedPayment.get(0).getId();

        // 2. 이벤트 리스너를 통해서 outbox에 데이터가 저장되었는지 확인
        await()
            .pollInterval(Duration.ofMillis(500))
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Optional<PaymentCompletedEventOutbox> outbox = paymentCompletedEventOutboxJpaRepository.findById(paymentId);
                assertThat(outbox).isPresent();
            });

        // 3. 이벤트 리스너를 통해서 kafka로 메세지가 정상적으로 발행되었는지 확인
        await()
            .pollInterval(Duration.ofMillis(500))
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(receivedMessages).isNotEmpty();
                PaymentCompletedEvent receivedEvent = receivedMessages.poll();
                assertThat(receivedEvent.paymentId()).isEqualTo(paymentId);
            });
    }

    @KafkaListener(topics = PAYMENT_COMPLETED_TOPIC, groupId = "test-payment-group")
    public void listen(ConsumerRecord<String, PaymentCompletedEvent> record) {
        receivedMessages.offer(record.value());
    }
}
