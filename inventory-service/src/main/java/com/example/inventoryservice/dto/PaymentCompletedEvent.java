package com.example.inventoryservice.dto;

public class PaymentCompletedEvent {
    private String orderId;
    private boolean success;
    private String transactionId;
    private String correlationId;

    public PaymentCompletedEvent() {}
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
