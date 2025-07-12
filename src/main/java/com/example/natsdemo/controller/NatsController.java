package com.example.natsdemo.controller;

import com.example.natsdemo.service.NatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for NATS operations.
 * Provides endpoints for publishing messages and checking connection status.
 */
@RestController
@RequestMapping("/api/nats")
public class NatsController {

    private static final Logger logger = LoggerFactory.getLogger(NatsController.class);

    private final NatsService natsService;

    @Autowired
    public NatsController(NatsService natsService) {
        this.natsService = natsService;
    }

    /**
     * Publishes a simple text message to a NATS subject.
     *
     * @param request The request containing subject and message
     * @return Response with status and message
     */
    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publishMessage(@RequestBody PublishRequest request) {
        try {
            natsService.publishMessage(request.getSubject(), request.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Message published successfully");
            response.put("subject", request.getSubject());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to publish message", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to publish message: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Publishes a JSON object to a NATS subject.
     *
     * @param request The request containing subject and payload
     * @return Response with status and message
     */
    @PostMapping("/publish/json")
    public ResponseEntity<Map<String, Object>> publishJsonMessage(@RequestBody JsonPublishRequest request) {
        try {
            natsService.publishJsonMessage(request.getSubject(), request.getPayload());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "JSON message published successfully");
            response.put("subject", request.getSubject());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to publish JSON message", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to publish JSON message: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Sends a request message and waits for a response.
     *
     * @param request The request containing subject and request data
     * @return Response with the received data
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestMessage(@RequestBody RequestMessageRequest request) {
        try {
            String response = natsService.requestMessage(request.getSubject(), request.getRequestData());
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", "success");
            responseMap.put("response", response);
            responseMap.put("subject", request.getSubject());
            
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Failed to request message", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to request message: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Checks the NATS connection status.
     *
     * @return Response with connection status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("connected", natsService.isConnected());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Request DTO for publishing simple text messages.
     */
    public static class PublishRequest {
        private String subject;
        private String message;

        // Getters and setters
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Request DTO for publishing JSON messages.
     */
    public static class JsonPublishRequest {
        private String subject;
        private Object payload;

        // Getters and setters
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public Object getPayload() { return payload; }
        public void setPayload(Object payload) { this.payload = payload; }
    }

    /**
     * Request DTO for request-reply messages.
     */
    public static class RequestMessageRequest {
        private String subject;
        private String requestData;

        // Getters and setters
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getRequestData() { return requestData; }
        public void setRequestData(String requestData) { this.requestData = requestData; }
    }
} 