package com.example.natsdemo.controller;

import com.example.natsdemo.model.Iso8583Message;
import com.example.natsdemo.model.Iso20022Message;
import com.example.natsdemo.service.IsoConverterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for ISO 8583 to ISO 20022 conversion testing.
 * Provides endpoints to test the conversion functionality.
 */
@RestController
@RequestMapping("/api/iso")
public class IsoConverterController {

    private static final Logger logger = LoggerFactory.getLogger(IsoConverterController.class);
    
    private final IsoConverterService isoConverterService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public IsoConverterController(IsoConverterService isoConverterService, ObjectMapper objectMapper) {
        this.isoConverterService = isoConverterService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Test endpoint to trigger ISO 8583 to ISO 20022 conversion.
     * Publishes a test ISO 8583 message to NATS for conversion.
     */
    @PostMapping("/test-conversion")
    public ResponseEntity<Map<String, String>> testConversion() {
        try {
            isoConverterService.publishTestIso8583Message();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Test ISO 8583 message published for conversion");
            response.put("details", "Check application logs for conversion results");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error testing ISO conversion", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to test ISO conversion: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Direct conversion endpoint that converts ISO 8583 to ISO 20022 without NATS.
     */
    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertDirect(@RequestBody Iso8583Message iso8583Message) {
        try {
            logger.info("Converting ISO 8583 message: {}", iso8583Message);
            
            Iso20022Message iso20022Message = isoConverterService.convertIso8583ToIso20022(iso8583Message);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("input", iso8583Message);
            response.put("output", iso20022Message);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error converting ISO 8583 to ISO 20022", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to convert: " + e.getMessage());
            response.put("input", iso8583Message);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Health check endpoint for the ISO converter service.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "ISO 8583 to ISO 20022 Converter");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get sample ISO 8583 message for testing.
     */
    @GetMapping("/sample-iso8583")
    public ResponseEntity<Iso8583Message> getSampleIso8583() {
        Iso8583Message sampleMessage = new Iso8583Message();
        sampleMessage.setMti("0200");
        sampleMessage.setPan("4111111111111111");
        sampleMessage.setProcessingCode("000000");
        sampleMessage.setAmount("100.00");
        sampleMessage.setStan("123456");
        sampleMessage.setTime("143022");
        sampleMessage.setDate("0710");
        sampleMessage.setMerchantType("5411");
        sampleMessage.setPosEntryMode("021");
        sampleMessage.setCardSequence("001");
        sampleMessage.setAcquirerId("123456");
        sampleMessage.setForwarderId("654321");
        sampleMessage.setTrack2("4111111111111111=123456789012345");
        sampleMessage.setRetrievalRef("123456789012");
        sampleMessage.setAuthCode("123456");
        sampleMessage.setResponseCode("00");
        sampleMessage.setCardAcceptor("12345678");
        sampleMessage.setCardAcceptorId("123456789012345");
        sampleMessage.setCardAcceptorName("DEMO MERCHANT");
        sampleMessage.setCurrencyCode("840");
        
        return ResponseEntity.ok(sampleMessage);
    }
} 