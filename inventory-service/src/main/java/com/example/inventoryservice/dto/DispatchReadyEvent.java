package com.example.inventoryservice.dto;

public class DispatchReadyEvent {
    private String orderId;
    private String correlationId;
    public DispatchReadyEvent() {}
    public DispatchReadyEvent(String orderId) { this.orderId = orderId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
