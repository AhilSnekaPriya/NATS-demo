# File-based ISO 8583 to ISO 20022 Conversion

This document describes the file-based conversion system that allows you to convert ISO 8583 messages to ISO 20022 format by reading from input files and writing to output files.

## Overview

The file-based conversion system provides:
- **Input**: ISO 8583 messages stored in JSON files
- **Output**: ISO 20022 messages written to JSON files
- **Batch Processing**: Convert multiple files at once
- **File Upload**: Upload files via REST API
- **Sample Generation**: Create sample files for testing

## File Structure

```
nats-demo/
├── iso8583_input/          # Input directory for ISO 8583 files
│   ├── sample_payment.json
│   ├── atm_withdrawal.json
│   └── online_purchase.json
├── iso20022_output/        # Output directory for ISO 20022 files
│   ├── sample_payment.json
│   ├── atm_withdrawal.json
│   └── online_purchase.json
└── test-file-conversion.sh # Testing script
```

## API Endpoints

### 1. Service Information
```http
GET /api/file-iso/info
```
Returns information about the file-based converter service.

**Response:**
```json
{
  "service": "File-based ISO 8583 to ISO 20022 Converter",
  "inputDirectory": "iso8583_input",
  "outputDirectory": "iso20022_output",
  "inputFileExtension": ".json",
  "outputFileExtension": ".json",
  "timestamp": "2025-07-11T20:30:00"
}
```

### 2. Create Sample File
```http
POST /api/file-iso/create-sample/{fileName}
```
Creates a sample ISO 8583 file for testing.

**Response:**
```json
{
  "success": true,
  "message": "Sample file created successfully",
  "filePath": "iso8583_input/test_payment.json"
}
```

### 3. Convert Single File
```http
POST /api/file-iso/convert/{fileName}
```
Converts a single ISO 8583 file to ISO 20022 format.

**Response:**
```json
{
  "success": true,
  "message": "Conversion completed successfully",
  "inputFilePath": "iso8583_input/sample_payment.json",
  "outputFilePath": "iso20022_output/sample_payment.json",
  "inputMessage": { /* ISO 8583 message */ },
  "outputMessage": { /* ISO 20022 message */ }
}
```

### 4. Batch Conversion
```http
POST /api/file-iso/convert-batch?inputDir={inputDir}&outputDir={outputDir}
```
Converts all JSON files in the input directory to ISO 20022 format.

**Response:**
```json
{
  "success": true,
  "totalFiles": 3,
  "successCount": 3,
  "failureCount": 0,
  "message": "Processed 3 files: 3 success, 0 failures"
}
```

### 5. Upload and Convert
```http
POST /api/file-iso/upload-and-convert
Content-Type: multipart/form-data

file: [ISO 8583 JSON file]
outputFileName: [optional output filename]
```
Uploads an ISO 8583 file and converts it to ISO 20022 format.

## File Formats

### ISO 8583 Input Format
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
  "cardAcceptorName": "DEMO GROCERY STORE",
  "currencyCode": "840"
}
```

### ISO 20022 Output Format
```json
{
  "msgId": "MSG123456789",
  "creDtTm": "2025-07-11T14:30:22",
  "nbOfTxs": "1",
  "ctrlSum": "150.00",
  "initgPty": {
    "nm": "Demo Bank",
    "id": {
      "orgId": {
        "othr": {
          "id": "DEMO001",
          "schmeNm": {
            "cd": "BANK"
          }
        }
      }
    }
  },
  "pmtInf": {
    "pmtInfId": "PMTAF60E536",
    "pmtMtd": "TRF",
    "btchBookg": "true",
    "nbOfTxs": "1",
    "ctrlSum": "150.00",
    "pmtTpInf": {
      "svcLvl": {
        "cd": "SEPA"
      }
    },
    "reqdExctnDt": "2025-07-11",
    "dbtr": {
      "orgId": {
        "othr": {
          "id": "4111111111111111",
          "schmeNm": {
            "cd": "BANK"
          }
        }
      }
    },
    "dbtrAcct": {
      "id": {
        "othr": {
          "id": "4111111111111111",
          "schmeNm": null
        }
      }
    },
    "dbtrAgt": {
      "finInstnId": {
        "bicfi": "DEMOBANK"
      }
    },
    "cdtTrfTxInf": {
      "pmtId": {
        "instrId": "INSB27A9FBF",
        "endToEndId": "123456"
      },
      "amt": {
        "instdAmt": "150.00"
      },
      "cdtrAgt": {
        "finInstnId": {
          "bicfi": "TARGETBANK"
        }
      },
      "cdtr": {
        "orgId": {
          "othr": {
            "id": "TARGET001",
            "schmeNm": {
              "cd": "BANK"
            }
          }
        }
      },
      "cdtrAcct": {
        "id": {
          "othr": {
            "id": "TARGETACCOUNT",
            "schmeNm": null
          }
        }
      }
    }
  }
}
```

## Usage Examples

### 1. Using the REST API

#### Convert a single file:
```bash
curl -X POST http://localhost:8080/api/file-iso/convert/sample_payment
```

#### Create a sample file:
```bash
curl -X POST http://localhost:8080/api/file-iso/create-sample/my_payment
```

#### Batch convert all files:
```bash
curl -X POST "http://localhost:8080/api/file-iso/convert-batch?inputDir=iso8583_input&outputDir=iso20022_output"
```

#### Upload and convert a file:
```bash
curl -X POST http://localhost:8080/api/file-iso/upload-and-convert \
  -F "file=@my_iso8583_message.json" \
  -F "outputFileName=converted_message"
```

### 2. Using the Testing Script

Run the comprehensive testing script:
```bash
./test-file-conversion.sh
```

This script will:
1. Check if the application is running
2. Test all API endpoints
3. Create sample files
4. Convert files individually and in batch
5. Verify output files
6. Display results

### 3. Manual File Processing

1. **Create input files**: Place ISO 8583 JSON files in the `iso8583_input/` directory
2. **Convert files**: Use the REST API or batch conversion
3. **Check output**: Find converted ISO 20022 files in the `iso20022_output/` directory

## Sample Files

The system includes three sample ISO 8583 files:

1. **sample_payment.json** - Standard payment transaction
2. **atm_withdrawal.json** - ATM cash withdrawal
3. **online_purchase.json** - Online purchase transaction

## Error Handling

The system provides detailed error messages for:
- **File not found**: When input files don't exist
- **Invalid JSON**: When input files contain malformed JSON
- **Missing fields**: When required ISO 8583 fields are missing
- **Directory errors**: When input/output directories can't be accessed

## Configuration

### Default Directories
- **Input Directory**: `iso8583_input/`
- **Output Directory**: `iso20022_output/`
- **File Extensions**: `.json`

### Custom Directories
You can specify custom directories for batch conversion:
```bash
curl -X POST "http://localhost:8080/api/file-iso/convert-batch?inputDir=/path/to/input&outputDir=/path/to/output"
```

## Monitoring

### Application Logs
Monitor the application logs to see:
- File reading operations
- Conversion progress
- Error details
- Batch processing statistics

### File System
Check the file system for:
- Input files in `iso8583_input/`
- Output files in `iso20022_output/`
- File timestamps for processing verification

## Integration with NATS

The file-based converter can work alongside the NATS messaging system:
- Convert files manually using the REST API
- Use NATS for real-time message processing
- Combine both approaches for different use cases

## Troubleshooting

### Common Issues

1. **Application not running**: Start the Spring Boot application first
2. **Port conflicts**: Ensure port 8080 is available
3. **File permissions**: Check read/write permissions for directories
4. **Invalid JSON**: Validate input file format
5. **Missing directories**: The system creates directories automatically

### Debug Steps

1. Check application health: `GET /health`
2. Verify service info: `GET /api/file-iso/info`
3. Test with sample files first
4. Check application logs for detailed error messages
5. Verify file paths and permissions

## Next Steps

After successful file-based conversion, you can:
1. Integrate with external systems
2. Add more ISO 8583 message types
3. Implement custom conversion rules
4. Add validation and error handling
5. Scale for high-volume processing 