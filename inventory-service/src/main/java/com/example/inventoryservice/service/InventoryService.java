package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponseEvent;
import com.example.inventoryservice.dto.OrderPlaced;
import com.example.inventoryservice.dto.PaymentCompletedEvent;
import com.example.inventoryservice.dto.DispatchReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryService {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final String inventoryRespTopic;
    private final String dispatchTopic;

    private final Map<String,Integer> stock = new ConcurrentHashMap<>();
    // keep pending order details so we can decrease stock after payment
    private final Map<String, OrderPlaced> pendingOrders = new ConcurrentHashMap<>();

    public InventoryService(KafkaTemplate<String,Object> kafkaTemplate,
                            @Value("${app.topic.inventory-responses}") String inventoryRespTopic,
                            @Value("${app.topic.dispatch-ready}") String dispatchTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryRespTopic = inventoryRespTopic;
        this.dispatchTopic = dispatchTopic;
    }

    @PostConstruct
    public void initStock() {
        stock.put("widget", 10);
        stock.put("gadget", 5);
    }

    @KafkaListener(topics = "${app.topic.orders}", groupId = "inventory-service-group", containerFactory = "orderKafkaListenerContainerFactory")
    public void handleOrderPlaced(OrderPlaced order) {
        pendingOrders.put(order.getOrderId(), order);
        int available = stock.getOrDefault(order.getItem(), 0);
        if (available >= order.getQuantity()) {
            InventoryResponseEvent resp = new InventoryResponseEvent(order.getOrderId(), true, "available");
            kafkaTemplate.send(inventoryRespTopic, order.getOrderId(), resp);
        } else {
            InventoryResponseEvent resp = new InventoryResponseEvent(order.getOrderId(), false, "insufficient stock");
            kafkaTemplate.send(inventoryRespTopic, order.getOrderId(), resp);
            // remove pending since cannot fulfill
            pendingOrders.remove(order.getOrderId());
        }
    }

    @KafkaListener(topics = "${app.topic.payments-completed}", groupId = "inventory-service-group", containerFactory = "paymentKafkaListenerContainerFactory")
    public void handlePaymentCompleted(PaymentCompletedEvent evt) {
        if (!evt.isSuccess()) return;
        OrderPlaced order = pendingOrders.remove(evt.getOrderId());
        if (order == null) return;
        stock.computeIfPresent(order.getItem(), (k,v) -> v - order.getQuantity());
        DispatchReadyEvent ready = new DispatchReadyEvent(order.getOrderId());
        kafkaTemplate.send(dispatchTopic, order.getOrderId(), ready);
    }
}
