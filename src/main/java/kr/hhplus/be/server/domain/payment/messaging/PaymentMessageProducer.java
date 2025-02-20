package kr.hhplus.be.server.domain.payment.messaging;

import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;

public interface PaymentMessageProducer {
    void publishPaymentCompetedMessage(PaymentCompletedEvent event);
}
