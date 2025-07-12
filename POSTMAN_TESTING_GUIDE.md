# Postman Testing Guide for NATS Demo Application

This guide will help you test all the endpoints of the NATS demo application using Postman.

## Prerequisites

1. **Application Running**: Make sure the Spring Boot application is running on `http://localhost:8080`
2. **NATS Server**: Ensure NATS server is running on port 4222
3. **Postman**: Download and install Postman from [postman.com](https://www.postman.com/)

## Import Postman Collection

1. Open Postman
2. Click "Import" button
3. Select the `postman-collection.json` file from this project
4. The collection will be imported with all pre-configured requests

## Testing Steps

### 1. Health Checks (Start Here)

First, verify that all services are running properly:

#### 1.1 Application Health
- **Method**: GET
- **URL**: `http://localhost:8080/health`
- **Expected Response**: 
```json
{
  "status": "UP",
  "components": {
    "nats": {
      "status": "UP"
    }
  }
}
```

#### 1.2 ISO Converter Health
- **Method**: GET
- **URL**: `http://localhost:8080/api/iso/health`
- **Expected Response**:
```json
{
  "status": "healthy",
  "service": "ISO 8583 to ISO 20022 Converter",
  "timestamp": "2025-07-11T20:30:00"
}
```

#### 1.3 NATS Connection Status
- **Method**: GET
- **URL**: `http://localhost:8080/api/nats/status`
- **Expected Response**:
```json
{
  "connected": true,
  "timestamp": 1720728600000
}
```

### 2. Basic NATS Messaging

#### 2.1 Publish Simple Message
- **Method**: POST
- **URL**: `http://localhost:8080/api/nats/publish`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "subject": "demo.messages",
  "message": "Hello from Postman!"
}
```
- **Expected Response**:
```json
{
  "status": "success",
  "message": "Message published successfully",
  "subject": "demo.messages"
}
```

#### 2.2 Publish JSON Message
- **Method**: POST
- **URL**: `http://localhost:8080/api/nats/publish/json`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "subject": "demo.json",
  "payload": {
    "type": "user_event",
    "userId": "12345",
    "action": "login",
    "timestamp": "2025-07-11T20:30:00Z",
    "data": {
      "ip": "192.168.1.100",
      "userAgent": "PostmanRuntime/7.32.3"
    }
  }
}
```
- **Expected Response**:
```json
{
  "status": "success",
  "message": "JSON message published successfully",
  "subject": "demo.json"
}
```

### 3. ISO 8583 to ISO 20022 Conversion

#### 3.1 Get Sample ISO 8583 Message
- **Method**: GET
- **URL**: `http://localhost:8080/api/iso/sample-iso8583`
- **Expected Response**: A complete ISO 8583 message object with all fields populated

#### 3.2 Convert ISO 8583 to ISO 20022
- **Method**: POST
- **URL**: `http://localhost:8080/api/iso/convert`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "mti": "0200",
  "pan": "4111111111111111",
  "processingCode": "000000",
  "amount": "150.00",
  "stan": "123456",
  "time": "143022",
  "date": "0710",
  "merchantType": "5411",
  "posEntryMode": "021",
  "cardSequence": "001",
  "acquirerId": "123456",
  "forwarderId": "654321",
  "track2": "4111111111111111=123456789012345",
  "retrievalRef": "123456789012",
  "authCode": "123456",
  "responseCode": "00",
  "cardAcceptor": "12345678",
  "cardAcceptorId": "123456789012345",
  "cardAcceptorName": "DEMO MERCHANT",
  "currencyCode": "840"
}
```
- **Expected Response**:
```json
{
  "status": "success",
  "input": { /* original ISO 8583 message */ },
  "output": {
    "msgId": "MSG123456789",
    "creDtTm": "2025-07-11T14:30:22",
    "nbOfTxs": "1",
    "ctrlSum": "150.00",
    "initgPty": { /* initiating party details */ },
    "pmtInf": { /* payment information */ }
  }
}
```

#### 3.3 Test ISO Conversion via NATS
- **Method**: POST
- **URL**: `http://localhost:8080/api/iso/test-conversion`
- **Headers**: `Content-Type: application/json`
- **Body**: `{}`
- **Expected Response**:
```json
{
  "status": "success",
  "message": "Test ISO 8583 message published for conversion",
  "details": "Check application logs for conversion results"
}
```

### 4. Advanced NATS Testing

#### 4.1 Publish to ISO 8583 Subject (Automatic Conversion)
- **Method**: POST
- **URL**: `http://localhost:8080/api/nats/publish`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "subject": "iso8583.messages",
  "message": "{\"MTI\":\"0200\",\"PAN\":\"4111111111111111\",\"AMOUNT\":\"200.00\",\"STAN\":\"789012\"}"
}
```
- **Note**: This will trigger automatic conversion to ISO 20022

#### 4.2 Publish User Event
- **Method**: POST
- **URL**: `http://localhost:8080/api/nats/publish/json`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "subject": "user.events",
  "payload": {
    "eventType": "USER_LOGIN",
    "userId": "user123",
    "timestamp": "2025-07-11T20:30:00Z",
    "sessionId": "sess_abc123",
    "ipAddress": "192.168.1.100"
  }
}
```

#### 4.3 Publish System Event
- **Method**: POST
- **URL**: `http://localhost:8080/api/nats/publish/json`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "subject": "system.events",
  "payload": {
    "eventType": "SYSTEM_STARTUP",
    "component": "NATS_DEMO_APP",
    "timestamp": "2025-07-11T20:30:00Z",
    "version": "1.0.0",
    "status": "RUNNING"
  }
}
```

## Testing Scenarios

### Scenario 1: Basic NATS Messaging
1. Run "Application Health" to verify the app is running
2. Run "NATS Connection Status" to verify NATS connection
3. Run "Publish Simple Message" to test basic messaging
4. Run "Publish JSON Message" to test JSON messaging
5. Check application logs to see message processing

### Scenario 2: ISO Conversion Testing
1. Run "ISO Converter Health" to verify the converter service
2. Run "Get Sample ISO 8583 Message" to get a sample message
3. Run "Convert ISO 8583 to ISO 20022" with the sample data
4. Run "Test ISO Conversion via NATS" to test automatic conversion
5. Check application logs for conversion details

### Scenario 3: Advanced NATS Testing
1. Run "Publish to ISO 8583 Subject" to trigger automatic conversion
2. Run "Publish User Event" to test user event processing
3. Run "Publish System Event" to test system event processing
4. Check application logs for all message processing

## Monitoring and Logs

While testing, monitor the application logs to see:
- Message publishing confirmations
- Message reception and processing
- ISO conversion activities
- Error messages (if any)

The application logs will show detailed information about:
- NATS message publishing
- Message subscription and processing
- ISO 8583 to ISO 20022 conversion
- Error handling

## Troubleshooting

### Common Issues:

1. **Connection Refused**: Make sure the application is running on port 8080
2. **NATS Connection Failed**: Ensure NATS server is running on port 4222
3. **JSON Parsing Errors**: Check that request bodies are valid JSON
4. **Timeout Errors**: Some operations may take time, especially request-reply patterns

### Debug Steps:

1. Check application health endpoints first
2. Verify NATS connection status
3. Check application logs for detailed error messages
4. Ensure all required fields are provided in request bodies

## Expected Behaviors

- **Health Checks**: Should return status "UP" or "healthy"
- **Message Publishing**: Should return success status
- **ISO Conversion**: Should return both input and output messages
- **NATS Status**: Should show "connected": true
- **Error Responses**: Should include error details and status "error"

## Next Steps

After successful testing, you can:
1. Modify request bodies to test different scenarios
2. Add more complex JSON payloads
3. Test error conditions by sending invalid data
4. Monitor real-time message processing in application logs 