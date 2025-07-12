package com.example.natsdemo.controller;

import com.example.natsdemo.service.FileBasedIsoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for file-based ISO 8583 to ISO 20022 conversion.
 * Provides endpoints for uploading ISO 8583 files and downloading converted ISO 20022 files.
 */
@RestController
@RequestMapping("/api/file-iso")
public class FileBasedIsoController {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedIsoController.class);
    
    private final FileBasedIsoConverter fileBasedIsoConverter;
    
    @Autowired
    public FileBasedIsoController(FileBasedIsoConverter fileBasedIsoConverter) {
        this.fileBasedIsoConverter = fileBasedIsoConverter;
    }
    
    /**
     * Converts a single ISO 8583 file to ISO 20022 format.
     *
     * @param inputFileName Name of the input file (without extension)
     * @return Conversion result
     */
    @PostMapping("/convert/{fileName}")
    public ResponseEntity<Map<String, Object>> convertFile(@PathVariable String fileName) {
        try {
            logger.info("Converting file: {}", fileName);
            
            FileBasedIsoConverter.ConversionResult result = fileBasedIsoConverter.convertWithDefaultPaths(fileName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("inputFilePath", result.getInputFilePath());
            response.put("outputFilePath", result.getOutputFilePath());
            
            if (result.isSuccess()) {
                response.put("inputMessage", result.getInputMessage());
                response.put("outputMessage", result.getOutputMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error converting file: {}", fileName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error converting file: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Converts multiple ISO 8583 files from a directory to ISO 20022 format.
     *
     * @param inputDir Input directory path
     * @param outputDir Output directory path
     * @return Batch conversion result
     */
    @PostMapping("/convert-batch")
    public ResponseEntity<Map<String, Object>> convertBatch(
            @RequestParam String inputDir,
            @RequestParam String outputDir) {
        try {
            logger.info("Starting batch conversion: {} -> {}", inputDir, outputDir);
            
            FileBasedIsoConverter.BatchConversionResult result = fileBasedIsoConverter.convertDirectory(inputDir, outputDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getFailureCount() == 0);
            response.put("totalFiles", result.getTotalCount());
            response.put("successCount", result.getSuccessCount());
            response.put("failureCount", result.getFailureCount());
            response.put("message", String.format("Processed %d files: %d success, %d failures", 
                    result.getTotalCount(), result.getSuccessCount(), result.getFailureCount()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during batch conversion", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error during batch conversion: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Creates a sample ISO 8583 file for testing.
     *
     * @param fileName Name of the file to create
     * @return Result with file path
     */
    @PostMapping("/create-sample/{fileName}")
    public ResponseEntity<Map<String, Object>> createSampleFile(@PathVariable String fileName) {
        try {
            logger.info("Creating sample ISO 8583 file: {}", fileName);
            
            String filePath = fileBasedIsoConverter.createSampleIso8583File(fileName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", filePath != null);
            response.put("message", filePath != null ? "Sample file created successfully" : "Failed to create sample file");
            response.put("filePath", filePath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating sample file: {}", fileName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating sample file: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Uploads an ISO 8583 file and converts it to ISO 20022.
     *
     * @param file The uploaded ISO 8583 file
     * @param outputFileName Name for the output file (optional)
     * @return Conversion result
     */
    @PostMapping("/upload-and-convert")
    public ResponseEntity<Map<String, Object>> uploadAndConvert(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "outputFileName", required = false) String outputFileName) {
        try {
            logger.info("Uploading and converting file: {}", file.getOriginalFilename());
            
            // Validate file
            if (file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Uploaded file is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "File name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generate output filename if not provided
            if (outputFileName == null || outputFileName.trim().isEmpty()) {
                String originalName = file.getOriginalFilename();
                if (originalName != null && originalName.contains(".")) {
                    outputFileName = originalName.substring(0, originalName.lastIndexOf('.'));
                } else {
                    outputFileName = "converted_" + System.currentTimeMillis();
                }
            }
            
            // Get the application working directory
            String workingDir = System.getProperty("user.dir");
            logger.info("Working directory: {}", workingDir);
            
            // Create input directory if it doesn't exist
            java.io.File inputDir = new java.io.File(workingDir, "iso8583_input");
            if (!inputDir.exists()) {
                inputDir.mkdirs();
                logger.info("Created input directory: {}", inputDir.getAbsolutePath());
            }
            
            // Save uploaded file to input directory
            java.io.File inputFile = new java.io.File(inputDir, file.getOriginalFilename());
            
            // Ensure the directory exists
            inputFile.getParentFile().mkdirs();
            
            file.transferTo(inputFile);
            logger.info("File saved to: {}", inputFile.getAbsolutePath());
            
            // Use the absolute path for conversion
            String inputFilePath = inputFile.getAbsolutePath();
            
            // Convert the file
            java.io.File outputDir = new java.io.File(workingDir, "iso20022_output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
                logger.info("Created output directory: {}", outputDir.getAbsolutePath());
            }
            
            java.io.File outputFile = new java.io.File(outputDir, outputFileName + ".json");
            String outputFilePath = outputFile.getAbsolutePath();
            
            FileBasedIsoConverter.ConversionResult result = fileBasedIsoConverter.convertFile(inputFilePath, outputFilePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("inputFilePath", result.getInputFilePath());
            response.put("outputFilePath", result.getOutputFilePath());
            
            if (result.isSuccess()) {
                response.put("inputMessage", result.getInputMessage());
                response.put("outputMessage", result.getOutputMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error uploading and converting file", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error uploading and converting file: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Gets information about the file-based converter service.
     *
     * @return Service information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "File-based ISO 8583 to ISO 20022 Converter");
        response.put("inputDirectory", "iso8583_input");
        response.put("outputDirectory", "iso20022_output");
        response.put("inputFileExtension", ".json");
        response.put("outputFileExtension", ".json");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        // Check if directories exist
        String workingDir = System.getProperty("user.dir");
        java.io.File inputDir = new java.io.File(workingDir, "iso8583_input");
        java.io.File outputDir = new java.io.File(workingDir, "iso20022_output");
        response.put("inputDirectoryExists", inputDir.exists());
        response.put("outputDirectoryExists", outputDir.exists());
        response.put("workingDirectory", workingDir);
        
        // Count files in directories
        if (inputDir.exists()) {
            java.io.File[] inputFiles = inputDir.listFiles((dir, name) -> name.endsWith(".json"));
            response.put("inputFileCount", inputFiles != null ? inputFiles.length : 0);
        } else {
            response.put("inputFileCount", 0);
        }
        
        if (outputDir.exists()) {
            java.io.File[] outputFiles = outputDir.listFiles((dir, name) -> name.endsWith(".json"));
            response.put("outputFileCount", outputFiles != null ? outputFiles.length : 0);
        } else {
            response.put("outputFileCount", 0);
        }
        
        return ResponseEntity.ok(response);
    }
} 