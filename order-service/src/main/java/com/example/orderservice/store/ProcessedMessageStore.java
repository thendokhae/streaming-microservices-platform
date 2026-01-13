package com.example.orderservice.store;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessedMessageStore {
    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    public boolean isProcessed(String correlationId) {
        if (correlationId == null) return false;
        return processed.contains(correlationId);
    }

    public void markProcessed(String correlationId) {
        if (correlationId != null) processed.add(correlationId);
    }
}

