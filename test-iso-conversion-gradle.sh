#!/bin/bash

# Test script for ISO 8583 to ISO 20022 conversion using Gradle
# Make sure the Spring Boot application is running on port 8080

echo "=== ISO 8583 to ISO 20022 Conversion Test (Gradle) ==="
echo

# Test 1: Build the project
echo "1. Building the project with Gradle..."
./gradlew build
echo

# Test 2: Health check
echo "2. Testing health endpoint..."
curl -s http://localhost:8080/api/iso/health | jq .
echo

# Test 3: Get sample ISO 8583 message
echo "3. Getting sample ISO 8583 message..."
curl -s http://localhost:8080/api/iso/sample-iso8583 | jq .
echo

# Test 4: Direct conversion
echo "4. Testing direct conversion..."
SAMPLE_ISO8583='{
  "MTI": "0200",
  "PAN": "4111111111111111",
  "PROC_CODE": "000000",
  "AMOUNT": "100.00",
  "STAN": "123456",
  "TIME": "143022",
  "DATE": "0710",
  "MERCHANT_TYPE": "5411",
  "POS_ENTRY_MODE": "021",
  "CARD_SEQUENCE": "001",
  "ACQUIRER_ID": "123456",
  "FORWARDER_ID": "654321",
  "TRACK2": "4111111111111111=123456789012345",
  "RETRIEVAL_REF": "123456789012",
  "AUTH_CODE": "123456",
  "RESPONSE_CODE": "00",
  "CARD_ACCEPTOR": "12345678",
  "CARD_ACCEPTOR_ID": "123456789012345",
  "CARD_ACCEPTOR_NAME": "DEMO MERCHANT",
  "CURRENCY_CODE": "840"
}'

curl -s -X POST http://localhost:8080/api/iso/convert \
  -H "Content-Type: application/json" \
  -d "$SAMPLE_ISO8583" | jq .
echo

# Test 5: NATS-based conversion
echo "5. Testing NATS-based conversion..."
curl -s -X POST http://localhost:8080/api/iso/test-conversion | jq .
echo

echo "=== Test completed ==="
echo "Check the application logs for conversion details."
echo
echo "Gradle commands:"
echo "  ./gradlew bootRun          - Run the application"
echo "  ./gradlew testIsoConversion - Run ISO conversion tests"
echo "  ./gradlew runDemo          - Run full demo with NATS" 