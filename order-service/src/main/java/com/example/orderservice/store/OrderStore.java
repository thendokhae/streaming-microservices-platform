package com.example.orderservice.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OrderStore {
    public enum Status { PLACED, INVENTORY_CONFIRMED, INVENTORY_DECLINED, PAYMENT_REQUESTED, PAID, DISPATCHED }

    private final Map<String, Status> map = new ConcurrentHashMap<>();

    public void put(String orderId, Status status) { map.put(orderId, status); }
    public Status get(String orderId) { return map.get(orderId); }
}
