package com.example.orderservice.store;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SagaStore {
    public enum Status { PLACED, INVENTORY_CONFIRMED, INVENTORY_DECLINED, PAYMENT_REQUESTED, PAID, DISPATCHED, CANCELLED }

    private final Map<String, Status> state = new ConcurrentHashMap<>();
    private final Map<String, String> correlation = new ConcurrentHashMap<>();

    public void createSaga(String orderId, String correlationId) {
        state.put(orderId, Status.PLACED);
        correlation.put(orderId, correlationId);
    }

    public void updateStatus(String orderId, Status s) { state.put(orderId, s); }
    public Status getStatus(String orderId) { return state.get(orderId); }
    public String getCorrelationId(String orderId) { return correlation.get(orderId); }
}

