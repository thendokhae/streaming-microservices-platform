package com.example.orderservice.web;

import com.example.orderservice.dto.OrderPlaced;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.kafka.OrderProducer;
import com.example.orderservice.store.OrderStore;
import com.example.orderservice.store.SagaStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderProducer producer;
    private final OrderStore store;
    private final SagaStore sagaStore;

    public OrderController(OrderProducer producer, OrderStore store, SagaStore sagaStore) {
        this.producer = producer;
        this.store = store;
        this.sagaStore = sagaStore;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest req) {
        OrderPlaced placed = new OrderPlaced(req.getOrderId(), req.getItem(), req.getQuantity());
        // ensure correlation id
        if (placed.getCorrelationId() == null) {
            placed.setCorrelationId(UUID.randomUUID().toString());
        }
        store.put(req.getOrderId(), OrderStore.Status.PLACED);
        sagaStore.createSaga(req.getOrderId(), placed.getCorrelationId());
        producer.publishOrder(placed);
        return ResponseEntity.ok("order-published");
    }
}
