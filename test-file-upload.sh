#!/bin/bash

# Simple test script for file upload and conversion
BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api/file-iso"

echo "=========================================="
echo "Testing File Upload and Conversion"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "SUCCESS" ]; then
        echo -e "${GREEN}✓ $message${NC}"
    elif [ "$status" = "ERROR" ]; then
        echo -e "${RED}✗ $message${NC}"
    elif [ "$status" = "INFO" ]; then
        echo -e "${BLUE}ℹ $message${NC}"
    fi
}

# Check if application is running
echo -e "\n${BLUE}Checking application status...${NC}"
health_response=$(curl -s "$BASE_URL/health")
if [ $? -eq 0 ] && echo "$health_response" | grep -q '"status":"UP"'; then
    print_status "SUCCESS" "Application is running"
else
    print_status "ERROR" "Application is not running. Please start the application first."
    exit 1
fi

# Test 1: Get service information
echo -e "\n${BLUE}Test 1: Get File Converter Service Information${NC}"
info_response=$(curl -s "$API_BASE/info")
echo "Response: $info_response"

if echo "$info_response" | grep -q '"service"'; then
    print_status "SUCCESS" "Service information retrieved"
else
    print_status "ERROR" "Failed to get service information"
fi

# Test 2: Create a test ISO 8583 file
echo -e "\n${BLUE}Test 2: Create Test ISO 8583 File${NC}"
test_file="test_upload.json"
cat > "$test_file" << 'EOF'
{
  "mti": "0200",
  "pan": "4111111111111111",
  "processingCode": "000000",
  "amount": "100.00",
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
  "cardAcceptorName": "TEST MERCHANT",
  "currencyCode": "840"
}
EOF

print_status "SUCCESS" "Created test file: $test_file"

# Test 3: Upload and convert the file
echo -e "\n${BLUE}Test 3: Upload and Convert File${NC}"
upload_response=$(curl -s -X POST "$API_BASE/upload-and-convert" \
    -F "file=@$test_file" \
    -F "outputFileName=uploaded_test")

echo "Upload Response: $upload_response"

if echo "$upload_response" | grep -q '"success":true'; then
    print_status "SUCCESS" "File upload and conversion completed"
else
    print_status "ERROR" "File upload and conversion failed"
    echo "Error details: $upload_response"
fi

# Test 4: Convert an existing file
echo -e "\n${BLUE}Test 4: Convert Existing File${NC}"
convert_response=$(curl -s -X POST "$API_BASE/convert/sample_payment")

echo "Convert Response: $convert_response"

if echo "$convert_response" | grep -q '"success":true'; then
    print_status "SUCCESS" "File conversion completed"
else
    print_status "ERROR" "File conversion failed"
    echo "Error details: $convert_response"
fi

# Test 5: Check output files
echo -e "\n${BLUE}Test 5: Check Output Files${NC}"
if [ -d "iso20022_output" ]; then
    echo "Output directory exists"
    output_files=$(ls iso20022_output/*.json 2>/dev/null | wc -l)
    if [ "$output_files" -gt 0 ]; then
        print_status "SUCCESS" "Found $output_files output file(s)"
        echo "Output files:"
        ls -la iso20022_output/*.json
    else
        print_status "WARNING" "No output files found"
    fi
else
    print_status "ERROR" "Output directory does not exist"
fi

# Clean up test file
rm -f "$test_file"
print_status "INFO" "Cleaned up test file"

echo -e "\n${BLUE}=========================================="
echo "Testing Summary"
echo "==========================================${NC}"

echo -e "\n${GREEN}File upload and conversion testing completed!${NC}"
echo -e "\n${BLUE}Available endpoints:${NC}"
echo "  GET  $API_BASE/info                    - Get service information"
echo "  POST $API_BASE/convert/{fileName}      - Convert single file"
echo "  POST $API_BASE/upload-and-convert      - Upload and convert file"

echo -e "\n${BLUE}File structure:${NC}"
echo "  Input directory:  iso8583_input/"
echo "  Output directory: iso20022_output/"
echo "  File format:      JSON"

echo -e "\n${GREEN}Testing completed successfully!${NC}" 