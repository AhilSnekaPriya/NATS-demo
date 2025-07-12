package com.example.natsdemo.service;

import com.example.natsdemo.model.Iso8583Message;
import com.example.natsdemo.model.Iso20022Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for file-based ISO 8583 to ISO 20022 conversion.
 * Reads ISO 8583 messages from input files and writes converted ISO 20022 messages to output files.
 */
@Service
public class FileBasedIsoConverter {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedIsoConverter.class);
    
    private final IsoConverterService isoConverterService;
    private final ObjectMapper objectMapper;
    
    // Default file paths
    private static final String DEFAULT_INPUT_DIR = "iso8583_input";
    private static final String DEFAULT_OUTPUT_DIR = "iso20022_output";
    private static final String INPUT_FILE_EXTENSION = ".json";
    private static final String OUTPUT_FILE_EXTENSION = ".json";
    
    @Autowired
    public FileBasedIsoConverter(IsoConverterService isoConverterService, ObjectMapper objectMapper) {
        this.isoConverterService = isoConverterService;
        this.objectMapper = objectMapper;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Create directories if they don't exist
        createDirectoriesIfNotExist();
    }
    
    /**
     * Converts a single ISO 8583 message from file to ISO 20022 and writes to output file.
     *
     * @param inputFilePath Path to the input ISO 8583 file
     * @param outputFilePath Path to the output ISO 20022 file
     * @return Conversion result with status and details
     */
    public ConversionResult convertFile(String inputFilePath, String outputFilePath) {
        try {
            logger.info("Starting file conversion: {} -> {}", inputFilePath, outputFilePath);
            
            // Read ISO 8583 message from input file
            Iso8583Message iso8583Message = readIso8583FromFile(inputFilePath);
            logger.info("Read ISO 8583 message from file: {}", iso8583Message);
            
            // Convert to ISO 20022
            Iso20022Message iso20022Message = isoConverterService.convertIso8583ToIso20022(iso8583Message);
            logger.info("Converted to ISO 20022 message: {}", iso20022Message);
            
            // Write ISO 20022 message to output file
            writeIso20022ToFile(iso20022Message, outputFilePath);
            logger.info("Successfully wrote ISO 20022 message to file: {}", outputFilePath);
            
            return new ConversionResult(true, "Conversion completed successfully", 
                    inputFilePath, outputFilePath, iso8583Message, iso20022Message);
                    
        } catch (Exception e) {
            logger.error("Error converting file: {} -> {}", inputFilePath, outputFilePath, e);
            return new ConversionResult(false, "Conversion failed: " + e.getMessage(), 
                    inputFilePath, outputFilePath, null, null);
        }
    }
    
    /**
     * Converts multiple ISO 8583 messages from a directory to ISO 20022 format.
     *
     * @param inputDir Directory containing ISO 8583 files
     * @param outputDir Directory to write ISO 20022 files
     * @return Batch conversion result
     */
    public BatchConversionResult convertDirectory(String inputDir, String outputDir) {
        try {
            logger.info("Starting batch conversion from directory: {} to {}", inputDir, outputDir);
            
            Path inputPath = Paths.get(inputDir);
            Path outputPath = Paths.get(outputDir);
            
            // Create output directory if it doesn't exist
            Files.createDirectories(outputPath);
            
            List<ConversionResult> results = new ArrayList<>();
            final int[] successCount = {0};
            final int[] failureCount = {0};
            
            // Process all JSON files in input directory
            Files.list(inputPath)
                    .filter(path -> path.toString().endsWith(INPUT_FILE_EXTENSION))
                    .forEach(inputFile -> {
                        String fileName = inputFile.getFileName().toString();
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        Path outputFile = outputPath.resolve(baseName + OUTPUT_FILE_EXTENSION);
                        
                        ConversionResult result = convertFile(inputFile.toString(), outputFile.toString());
                        results.add(result);
                        
                        if (result.isSuccess()) {
                            successCount[0]++;
                        } else {
                            failureCount[0]++;
                        }
                    });
            
            logger.info("Batch conversion completed. Success: {}, Failures: {}", successCount[0], failureCount[0]);
            
            return new BatchConversionResult(successCount[0], failureCount[0], results);
            
        } catch (Exception e) {
            logger.error("Error during batch conversion", e);
            return new BatchConversionResult(0, 0, new ArrayList<>());
        }
    }
    
    /**
     * Converts ISO 8583 message using default file paths.
     *
     * @param inputFileName Name of the input file (without extension)
     * @return Conversion result
     */
    public ConversionResult convertWithDefaultPaths(String inputFileName) {
        String workingDir = System.getProperty("user.dir");
        String inputPath = new java.io.File(workingDir, DEFAULT_INPUT_DIR + "/" + inputFileName + INPUT_FILE_EXTENSION).getAbsolutePath();
        String outputPath = new java.io.File(workingDir, DEFAULT_OUTPUT_DIR + "/" + inputFileName + OUTPUT_FILE_EXTENSION).getAbsolutePath();
        
        return convertFile(inputPath, outputPath);
    }
    
    /**
     * Creates a sample ISO 8583 file for testing.
     *
     * @param fileName Name of the file to create
     * @return Path to the created file
     */
    public String createSampleIso8583File(String fileName) {
        try {
            Iso8583Message sampleMessage = createSampleIso8583Message();
            String workingDir = System.getProperty("user.dir");
            String filePath = new java.io.File(workingDir, DEFAULT_INPUT_DIR + "/" + fileName + INPUT_FILE_EXTENSION).getAbsolutePath();
            
            writeIso8583ToFile(sampleMessage, filePath);
            logger.info("Created sample ISO 8583 file: {}", filePath);
            
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error creating sample ISO 8583 file", e);
            return null;
        }
    }
    
    /**
     * Reads ISO 8583 message from file.
     */
    private Iso8583Message readIso8583FromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        return objectMapper.readValue(content, Iso8583Message.class);
    }
    
    /**
     * Writes ISO 8583 message to file.
     */
    private void writeIso8583ToFile(Iso8583Message message, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String content = objectMapper.writeValueAsString(message);
        Files.writeString(path, content);
    }
    
    /**
     * Writes ISO 20022 message to file.
     */
    private void writeIso20022ToFile(Iso20022Message message, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String content = objectMapper.writeValueAsString(message);
        Files.writeString(path, content);
    }
    
    /**
     * Creates directories if they don't exist.
     */
    private void createDirectoriesIfNotExist() {
        try {
            String workingDir = System.getProperty("user.dir");
            java.io.File inputDir = new java.io.File(workingDir, DEFAULT_INPUT_DIR);
            java.io.File outputDir = new java.io.File(workingDir, DEFAULT_OUTPUT_DIR);
            
            if (!inputDir.exists()) {
                inputDir.mkdirs();
                logger.info("Created input directory: {}", inputDir.getAbsolutePath());
            }
            
            if (!outputDir.exists()) {
                outputDir.mkdirs();
                logger.info("Created output directory: {}", outputDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Error creating directories", e);
        }
    }
    
    /**
     * Creates a sample ISO 8583 message for testing.
     */
    private Iso8583Message createSampleIso8583Message() {
        Iso8583Message message = new Iso8583Message();
        message.setMti("0200");
        message.setPan("4111111111111111");
        message.setProcessingCode("000000");
        message.setAmount("150.00");
        message.setStan("123456");
        message.setTime("143022");
        message.setDate("0710");
        message.setMerchantType("5411");
        message.setPosEntryMode("021");
        message.setCardSequence("001");
        message.setAcquirerId("123456");
        message.setForwarderId("654321");
        message.setTrack2("4111111111111111=123456789012345");
        message.setRetrievalRef("123456789012");
        message.setAuthCode("123456");
        message.setResponseCode("00");
        message.setCardAcceptor("12345678");
        message.setCardAcceptorId("123456789012345");
        message.setCardAcceptorName("DEMO MERCHANT");
        message.setCurrencyCode("840");
        
        return message;
    }
    
    /**
     * Result of a single file conversion.
     */
    public static class ConversionResult {
        private final boolean success;
        private final String message;
        private final String inputFilePath;
        private final String outputFilePath;
        private final Iso8583Message inputMessage;
        private final Iso20022Message outputMessage;
        
        public ConversionResult(boolean success, String message, String inputFilePath, 
                             String outputFilePath, Iso8583Message inputMessage, Iso20022Message outputMessage) {
            this.success = success;
            this.message = message;
            this.inputFilePath = inputFilePath;
            this.outputFilePath = outputFilePath;
            this.inputMessage = inputMessage;
            this.outputMessage = outputMessage;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getInputFilePath() { return inputFilePath; }
        public String getOutputFilePath() { return outputFilePath; }
        public Iso8583Message getInputMessage() { return inputMessage; }
        public Iso20022Message getOutputMessage() { return outputMessage; }
    }
    
    /**
     * Result of a batch directory conversion.
     */
    public static class BatchConversionResult {
        private final int successCount;
        private final int failureCount;
        private final List<ConversionResult> results;
        
        public BatchConversionResult(int successCount, int failureCount, List<ConversionResult> results) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.results = results;
        }
        
        // Getters
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public List<ConversionResult> getResults() { return results; }
        public int getTotalCount() { return successCount + failureCount; }
    }
} 