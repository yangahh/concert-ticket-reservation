package kr.hhplus.be.server.tests.infra;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


@SpringBootTest
@Testcontainers
@Slf4j
public class KafkaConnectionTest {

    @Autowired
    private KafkaTemplate<String, TestEvent> kafkaTemplate;
    private CountDownLatch latch;
    private TestEvent consumedMessage;
    private String consumedTopic;

    @BeforeEach
    void setup() {
        latch = new CountDownLatch(1);  // 메시지 수신을 기다리기 위한 카운트다운 래치
    }

    @DisplayName("카프카 producer, consumer 정상 동작 테스트")
    @Test
    void testKafkaMessageFlow() throws InterruptedException {
        // given
        String topic = "test-topic";
        TestEvent message = new TestEvent(1, "test message");

        // when
        kafkaTemplate.send(topic, message);

        // then
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS); // 최대 5초 대기

        await()
            .pollInterval(Duration.ofMillis(300))
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(messageReceived).isTrue();
                assertThat(consumedMessage).isEqualTo(message);
                assertThat(consumedTopic).isEqualTo(topic);
        });

    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, TestEvent> record) {
        log.info("====Consumed Message====");
        this.consumedMessage = record.value();
        this.consumedTopic = record.topic();
        latch.countDown();  // 메시지 도착을 알림
        log.info("[Consumer] Consumed Message: {}", record.value());
        log.info("====Consumed End====");
    }
}
