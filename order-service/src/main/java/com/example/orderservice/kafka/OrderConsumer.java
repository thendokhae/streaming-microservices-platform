package com.example.orderservice.kafka;

import com.example.orderservice.dto.DispatchReadyEvent;
import com.example.orderservice.dto.InventoryResponseEvent;
import com.example.orderservice.dto.PaymentCompletedEvent;
import com.example.orderservice.dto.PaymentRequestEvent;
import com.example.orderservice.store.OrderStore;
import com.example.orderservice.store.ProcessedMessageStore;
import com.example.orderservice.store.SagaStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    private final OrderStore store;
    private final OrderProducer producer;
    private final ProcessedMessageStore processedStore;
    private final SagaStore sagaStore;

    public OrderConsumer(OrderStore store, OrderProducer producer, ProcessedMessageStore processedStore, SagaStore sagaStore) {
        this.store = store;
        this.producer = producer;
        this.processedStore = processedStore;
        this.sagaStore = sagaStore;
    }

    @KafkaListener(topics = "${app.topic.inventory-responses}", groupId = "order-service-group", containerFactory = "inventoryResponseKafkaListenerContainerFactory")
    public void handleInventoryResponse(InventoryResponseEvent resp) {
        String cid = resp.getCorrelationId();
        if (processedStore.isProcessed(cid)) return; // idempotent

        if (resp.isConfirmed()) {
            store.put(resp.getOrderId(), OrderStore.Status.INVENTORY_CONFIRMED);
            sagaStore.updateStatus(resp.getOrderId(), SagaStore.Status.INVENTORY_CONFIRMED);
            // for demo, charge a fixed amount per order
            PaymentRequestEvent req = new PaymentRequestEvent(resp.getOrderId(), 19.99);
            req.setCorrelationId(cid);
            store.put(resp.getOrderId(), OrderStore.Status.PAYMENT_REQUESTED);
            sagaStore.updateStatus(resp.getOrderId(), SagaStore.Status.PAYMENT_REQUESTED);
            producer.requestPayment(req);
        } else {
            store.put(resp.getOrderId(), OrderStore.Status.INVENTORY_DECLINED);
            sagaStore.updateStatus(resp.getOrderId(), SagaStore.Status.INVENTORY_DECLINED);
        }
        processedStore.markProcessed(cid);
    }

    @KafkaListener(topics = "${app.topic.dispatch-ready}", groupId = "order-service-group", containerFactory = "dispatchReadyKafkaListenerContainerFactory")
    public void handleDispatchReady(DispatchReadyEvent event) {
        String cid = event.getCorrelationId();
        if (processedStore.isProcessed(cid)) return;
        store.put(event.getOrderId(), OrderStore.Status.DISPATCHED);
        sagaStore.updateStatus(event.getOrderId(), SagaStore.Status.DISPATCHED);
        processedStore.markProcessed(cid);
    }

    @KafkaListener(topics = "${app.topic.payments-completed}", groupId = "order-service-group", containerFactory = "paymentCompletedKafkaListenerContainerFactory")
    public void handlePaymentCompleted(PaymentCompletedEvent evt) {
        String cid = evt.getCorrelationId();
        if (processedStore.isProcessed(cid)) return;
        if (evt.isSuccess()) {
            store.put(evt.getOrderId(), OrderStore.Status.PAID);
            sagaStore.updateStatus(evt.getOrderId(), SagaStore.Status.PAID);
        } else {
            // payment failed -> mark saga cancelled
            sagaStore.updateStatus(evt.getOrderId(), SagaStore.Status.CANCELLED);
        }
        processedStore.markProcessed(cid);
    }
}
