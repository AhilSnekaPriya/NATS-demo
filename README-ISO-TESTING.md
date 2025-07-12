# ISO 8583 to ISO 20022 Conversion Testing Guide

This guide explains how to test the ISO 8583 to ISO 20022 conversion functionality in the NATS demo application.

## Overview

The application provides a complete pipeline for converting ISO 8583 financial messages to ISO 20022 format using NATS messaging. The conversion includes:

- **ISO 8583 Message Model**: Simplified representation of ISO 8583 fields
- **ISO 20022 Message Model**: Payment Initiation (pain.001) structure
- **Conversion Service**: Maps ISO 8583 fields to ISO 20022 elements
- **NATS Integration**: Asynchronous message processing via NATS subjects
- **REST API**: Direct conversion endpoints for testing

## Architecture

```
ISO 8583 Message → NATS (iso8583.messages) → Converter Service → NATS (iso20022.messages) → ISO 20022 Message
```

## Testing Methods

### 1. REST API Testing

#### Health Check
```bash
curl http://localhost:8080/api/iso/health
```

#### Get Sample ISO 8583 Message
```bash
curl http://localhost:8080/api/iso/sample-iso8583
```

#### Direct Conversion (without NATS)
```bash
curl -X POST http://localhost:8080/api/iso/convert \
  -H "Content-Type: application/json" \
  -d '{
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
```

#### Trigger NATS-based Conversion
```bash
curl -X POST http://localhost:8080/api/iso/test-conversion
```

### 2. Automated Testing

Use the provided test script:
```bash
./test-iso-conversion.sh
```

This script performs all the above tests automatically and formats the output with `jq`.

### 3. NATS Message Flow Testing

The application automatically:
1. Subscribes to `iso8583.messages` subject
2. Converts received ISO 8583 messages to ISO 20022
3. Publishes converted messages to `iso20022.messages` subject
4. Logs all conversion activities

## Message Flow

### ISO 8583 Message Structure
```json
{
  "MTI": "0200",                    // Message Type Indicator
  "PAN": "4111111111111111",        // Primary Account Number
  "PROC_CODE": "000000",            // Processing Code
  "AMOUNT": "100.00",               // Transaction Amount
  "STAN": "123456",                 // System Trace Audit Number
  "TIME": "143022",                 // Transaction Time
  "DATE": "0710",                   // Transaction Date
  "MERCHANT_TYPE": "5411",          // Merchant Type
  "POS_ENTRY_MODE": "021",          // POS Entry Mode
  "CARD_SEQUENCE": "001",           // Card Sequence Number
  "ACQUIRER_ID": "123456",          // Acquirer Institution ID
  "FORWARDER_ID": "654321",         // Forwarding Institution ID
  "TRACK2": "4111111111111111=123456789012345", // Track 2 Data
  "RETRIEVAL_REF": "123456789012",  // Retrieval Reference Number
  "AUTH_CODE": "123456",            // Authorization ID Response
  "RESPONSE_CODE": "00",            // Response Code
  "CARD_ACCEPTOR": "12345678",      // Card Acceptor Terminal ID
  "CARD_ACCEPTOR_ID": "123456789012345", // Card Acceptor ID Code
  "CARD_ACCEPTOR_NAME": "DEMO MERCHANT", // Card Acceptor Name/Location
  "CURRENCY_CODE": "840"            // Currency Code
}
```

### ISO 20022 Message Structure (pain.001)
```json
{
  "MsgId": "MSG8A38784D",
  "CreDtTm": "2025-07-10T12:02:33",
  "NbOfTxs": "1",
  "CtrlSum": "100.00",
  "InitgPty": {
    "Nm": "Demo Bank",
    "Id": {
      "OrgId": {
        "Othr": {
          "Id": "DEMO001",
          "SchmeNm": {
            "Cd": "BANK"
          }
        }
      }
    }
  },
  "PmtInf": {
    "PmtInfId": "PMTD27984CF",
    "PmtMtd": "TRF",
    "BtchBookg": "true",
    "NbOfTxs": "1",
    "CtrlSum": "100.00",
    "PmtTpInf": {
      "SvcLvl": {
        "Cd": "SEPA"
      }
    },
    "ReqdExctnDt": "2025-07-10",
    "Dbtr": { /* Debtor information */ },
    "DbtrAcct": { /* Debtor account */ },
    "DbtrAgt": { /* Debtor agent */ },
    "CdtTrfTxInf": {
      "PmtId": {
        "InstrId": "INS0F0DDF93",
        "EndToEndId": "123456"
      },
      "Amt": {
        "InstdAmt": "100.00"
      },
      "CdtrAgt": { /* Creditor agent */ },
      "Cdtr": { /* Creditor */ },
      "CdtrAcct": { /* Creditor account */ }
    }
  }
}
```

## Field Mapping

| ISO 8583 Field | ISO 20022 Element | Description |
|----------------|-------------------|-------------|
| PAN (Field 2) | DbtrAcct.Id.Othr.Id | Debtor Account Number |
| AMOUNT (Field 4) | CdtTrfTxInf.Amt.InstdAmt | Transaction Amount |
| STAN (Field 11) | CdtTrfTxInf.PmtId.EndToEndId | End-to-End Identification |
| TIME (Field 7) | CreDtTm (derived) | Creation Date Time |
| DATE (Field 13) | ReqdExctnDt (derived) | Requested Execution Date |
| CARD_ACCEPTOR_NAME (Field 43) | Cdtr.Nm | Creditor Name |

## Monitoring

### Application Logs
Monitor the application logs for:
- ISO 8583 message reception
- Conversion processing
- ISO 20022 message publication
- Error handling

### NATS Subjects
- `iso8583.messages`: Incoming ISO 8583 messages
- `iso20022.messages`: Converted ISO 20022 messages

### REST Endpoints
- `GET /api/iso/health`: Service health check
- `GET /api/iso/sample-iso8583`: Get sample message
- `POST /api/iso/convert`: Direct conversion
- `POST /api/iso/test-conversion`: Trigger NATS conversion

## Error Handling

The application includes comprehensive error handling:
- JSON parsing errors
- NATS connection issues
- Conversion mapping errors
- Invalid message format handling

## Performance Considerations

- Messages are processed asynchronously via NATS
- Conversion service is stateless and scalable
- JSON serialization/deserialization is optimized
- Logging levels can be adjusted for performance

## Security Notes

This is a demonstration application. For production use:
- Implement proper authentication and authorization
- Add message encryption
- Validate all input data
- Implement audit logging
- Use secure NATS configuration

## Troubleshooting

### Common Issues

1. **Application won't start**: Check if NATS server is running on port 4222
2. **Conversion fails**: Verify JSON format of input messages
3. **NATS connection issues**: Check NATS server logs and connection configuration
4. **Memory issues**: Monitor JVM heap size for large message volumes

### Debug Mode

Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.example.natsdemo=DEBUG
logging.level.io.nats=DEBUG
```

## Next Steps

1. **Production Deployment**: Add proper security, monitoring, and error handling
2. **Performance Optimization**: Implement message batching and caching
3. **Schema Validation**: Add XML schema validation for ISO 20022 messages
4. **Message Types**: Support additional ISO 20022 message types (pain.002, pacs.008, etc.)
5. **Integration**: Connect to real financial systems and databases 