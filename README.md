Distributed Streaming Microservices Platform (Order / Inventory / Payments)

This repository contains three minimal Spring Boot microservices that communicate via Apache Kafka:

- order-service: publishes OrderPlaced events, consumes order confirmations, requests payments, and listens for dispatch readiness.
- inventory-service: consumes OrderPlaced, checks stock, publishes confirmation or decline; consumes PaymentCompleted to decrease stock and publish OrderReadyToDispatch.
- payment-service: consumes PaymentRequest events, simulates a payment, and publishes PaymentCompleted.

Included: a Docker Compose to run Zookeeper and Kafka locally.

Quick start (Windows - cmd.exe):
1. Start Kafka and Zookeeper:
   docker-compose up -d

2. Build each service (from repository root):
   mvn -f order-service/pom.xml -DskipTests package
   mvn -f inventory-service/pom.xml -DskipTests package
   mvn -f payment-service/pom.xml -DskipTests package

3. Run services (each in its own terminal):
   java -jar order-service\target\order-service-0.0.1-SNAPSHOT.jar
   java -jar inventory-service\target\inventory-service-0.0.1-SNAPSHOT.jar
   java -jar payment-service\target\payment-service-0.0.1-SNAPSHOT.jar

4. Place a new order (HTTP POST):
   curl -X POST -H "Content-Type: application/json" -d "{\"orderId\":\"o1\",\"item\":\"widget\",\"quantity\":1}" http://localhost:8080/orders

You should see logs showing the choreography across services.

Notes:
- These services are intentionally minimal to demonstrate event-driven orchestration.
- The services use in-memory stores; for production use persistent storage and robust error handling.

