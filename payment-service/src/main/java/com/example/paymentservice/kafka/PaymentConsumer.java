package com.example.paymentservice.kafka;

import com.example.paymentservice.dto.PaymentRequestEvent;
import com.example.paymentservice.dto.PaymentCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final String paymentsCompletedTopic;

    public PaymentConsumer(KafkaTemplate<String,Object> kafkaTemplate,
                           @Value("${app.topic.payments-completed}") String paymentsCompletedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentsCompletedTopic = paymentsCompletedTopic;
    }

    @KafkaListener(topics = "${app.topic.payments-requests}", groupId = "payment-service-group", containerFactory = "paymentRequestKafkaListenerContainerFactory")
    public void handlePaymentRequest(PaymentRequestEvent req) throws InterruptedException {
        // simulate payment processing
        Thread.sleep(500);
        PaymentCompletedEvent completed = new PaymentCompletedEvent(req.getOrderId(), true, "tx-" + System.currentTimeMillis());
        kafkaTemplate.send(paymentsCompletedTopic, req.getOrderId(), completed);
    }
}
