package com.example.inventoryservice.dto;

public class InventoryResponseEvent {
    private String orderId;
    private boolean confirmed;
    private String reason;
    private String correlationId;

    public InventoryResponseEvent() {}
    public InventoryResponseEvent(String orderId, boolean confirmed, String reason) {
        this.orderId = orderId; this.confirmed = confirmed; this.reason = reason;
    }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
