package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderPlaced;
import com.example.orderservice.dto.PaymentRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ordersTopic;
    private final String paymentsTopic;

    public OrderProducer(KafkaTemplate<String, Object> kafkaTemplate,
                         @Value("${app.topic.orders}") String ordersTopic,
                         @Value("${app.topic.payments-requests}") String paymentsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.ordersTopic = ordersTopic;
        this.paymentsTopic = paymentsTopic;
    }

    public void publishOrder(OrderPlaced order) {
        if (order.getCorrelationId() == null) {
            order.setCorrelationId(UUID.randomUUID().toString());
        }
        kafkaTemplate.send(ordersTopic, order.getOrderId(), order);
    }

    public void requestPayment(PaymentRequestEvent paymentRequest) {
        if (paymentRequest.getCorrelationId() == null) {
            paymentRequest.setCorrelationId(UUID.randomUUID().toString());
        }
        kafkaTemplate.send(paymentsTopic, paymentRequest.getOrderId(), paymentRequest);
    }
}
