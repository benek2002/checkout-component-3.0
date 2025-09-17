# Checkout Service 3.0

A simple **market checkout service** built with **Spring Boot 3, Java 17, and Gradle**.  
The service exposes a REST API to scan items, apply pricing rules (bulk and bundle discounts), and generate receipts.

## Requirements
- **Java 17**
- **Gradle 8+**
- (optional) **Docker Desktop**

## Run locally

1. Build the project:
   ./gradlew clean build

2. Run the service:
  ./gradlew bootRun

3. To execute all tests (unit, integration, and acceptance), run:
   ./gradlew test

## Run with Docker

1. Build Docker image:
docker build -t checkout-service .

2. Run container:
docker run -p 8080:8080 checkout-service

## API Usage

API will be available at:
http://localhost:8080/checkout

### Start a checkout session

POST /checkout/start
Response: UUID (session id)

### Scan an item

POST /checkout/{sessionId}/scan/{itemName}

### Get total & receipt

GET /checkout/{sessionId}/total
