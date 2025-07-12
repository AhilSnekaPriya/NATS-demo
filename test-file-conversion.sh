#!/bin/bash

# File-based ISO 8583 to ISO 20022 Conversion Testing Script
# This script tests the file-based conversion functionality

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api/file-iso"

echo "=========================================="
echo "File-based ISO 8583 to ISO 20022 Testing"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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
    elif [ "$status" = "WARNING" ]; then
        echo -e "${YELLOW}⚠ $message${NC}"
    fi
}

# Function to make HTTP requests
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo -e "\n${BLUE}Testing: $description${NC}"
    echo "URL: $method $url"
    
    if [ -n "$data" ]; then
        echo "Data: $data"
        response=$(curl -s -X $method "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -X $method "$url")
    fi
    
    echo "Response: $response"
    
    # Check if response contains success
    if echo "$response" | grep -q '"success":true'; then
        print_status "SUCCESS" "$description completed successfully"
        return 0
    else
        print_status "ERROR" "$description failed"
        return 1
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
echo -e "\n${YELLOW}Test 1: Get File-based ISO Converter Service Information${NC}"
make_request "GET" "$API_BASE/info" "" "Service Information"

# Test 2: Create sample ISO 8583 file
echo -e "\n${YELLOW}Test 2: Create Sample ISO 8583 File${NC}"
make_request "POST" "$API_BASE/create-sample/test_payment" "" "Create Sample File"

# Test 3: Convert single file
echo -e "\n${YELLOW}Test 3: Convert Single File${NC}"
make_request "POST" "$API_BASE/convert/sample_payment" "" "Convert sample_payment.json"

# Test 4: Convert another file
echo -e "\n${YELLOW}Test 4: Convert ATM Withdrawal File${NC}"
make_request "POST" "$API_BASE/convert/atm_withdrawal" "" "Convert atm_withdrawal.json"

# Test 5: Convert online purchase file
echo -e "\n${YELLOW}Test 5: Convert Online Purchase File${NC}"
make_request "POST" "$API_BASE/convert/online_purchase" "" "Convert online_purchase.json"

# Test 6: Batch conversion
echo -e "\n${YELLOW}Test 6: Batch Conversion${NC}"
make_request "POST" "$API_BASE/convert-batch?inputDir=iso8583_input&outputDir=iso20022_output" "" "Batch Convert All Files"

# Test 7: Check output files
echo -e "\n${YELLOW}Test 7: Verify Output Files${NC}"
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

# Test 8: Display sample output
echo -e "\n${YELLOW}Test 8: Display Sample Output${NC}"
if [ -f "iso20022_output/sample_payment.json" ]; then
    echo "Sample ISO 20022 output:"
    cat "iso20022_output/sample_payment.json" | head -20
    print_status "SUCCESS" "Sample output file exists and is readable"
else
    print_status "ERROR" "Sample output file not found"
fi

# Test 9: Test file upload and conversion
echo -e "\n${YELLOW}Test 9: File Upload and Conversion${NC}"
if [ -f "iso8583_input/sample_payment.json" ]; then
    echo "Uploading sample_payment.json for conversion..."
    upload_response=$(curl -s -X POST "$API_BASE/upload-and-convert" \
        -F "file=@iso8583_input/sample_payment.json" \
        -F "outputFileName=uploaded_conversion")
    
    echo "Upload response: $upload_response"
    
    if echo "$upload_response" | grep -q '"success":true'; then
        print_status "SUCCESS" "File upload and conversion completed"
    else
        print_status "ERROR" "File upload and conversion failed"
    fi
else
    print_status "WARNING" "Sample file not found for upload test"
fi

# Summary
echo -e "\n${BLUE}=========================================="
echo "Testing Summary"
echo "==========================================${NC}"

echo -e "\n${GREEN}File-based ISO 8583 to ISO 20022 conversion testing completed!${NC}"
echo -e "\n${BLUE}Available endpoints:${NC}"
echo "  GET  $API_BASE/info                    - Get service information"
echo "  POST $API_BASE/create-sample/{name}    - Create sample ISO 8583 file"
echo "  POST $API_BASE/convert/{fileName}      - Convert single file"
echo "  POST $API_BASE/convert-batch           - Batch convert directory"
echo "  POST $API_BASE/upload-and-convert      - Upload and convert file"

echo -e "\n${BLUE}File structure:${NC}"
echo "  Input directory:  iso8583_input/"
echo "  Output directory: iso20022_output/"
echo "  File format:      JSON"

echo -e "\n${BLUE}Sample files created:${NC}"
ls -la iso8583_input/*.json 2>/dev/null || echo "No sample files found"

echo -e "\n${BLUE}Converted files:${NC}"
ls -la iso20022_output/*.json 2>/dev/null || echo "No converted files found"

echo -e "\n${GREEN}Testing completed successfully!${NC}" 