package com.example.orderservice.dto;

public class PaymentRequestEvent {
    private String orderId;
    private double amount;
    private String correlationId;

    public PaymentRequestEvent() {}
    public PaymentRequestEvent(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
