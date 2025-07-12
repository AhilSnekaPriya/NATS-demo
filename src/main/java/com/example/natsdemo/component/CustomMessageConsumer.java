package com.example.natsdemo.component;

import com.example.natsdemo.service.NatsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Custom message consumer that demonstrates how to consume messages
 * with specific business logic and processing.
 */
@Component
public class CustomMessageConsumer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CustomMessageConsumer.class);

    private final NatsService natsService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CustomMessageConsumer(NatsService natsService, ObjectMapper objectMapper) {
        this.natsService = natsService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Subscribe to custom subjects
        subscribeToUserEvents();
        subscribeToSystemEvents();
        
        logger.info("Custom message consumers initialized");
    }

    /**
     * Subscribes to user-related events.
     */
    private void subscribeToUserEvents() {
        natsService.subscribeToSubject("user.events", (subject, message) -> {
            logger.info("🔄 Processing user event: {}", message);
            
            try {
                // Parse JSON message
                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("type").asText();
                
                switch (eventType) {
                    case "user.created":
                        handleUserCreated(jsonNode);
                        break;
                    case "user.updated":
                        handleUserUpdated(jsonNode);
                        break;
                    case "user.deleted":
                        handleUserDeleted(jsonNode);
                        break;
                    default:
                        logger.warn("Unknown user event type: {}", eventType);
                }
            } catch (Exception e) {
                logger.error("Error processing user event: {}", message, e);
            }
        });
    }

    /**
     * Subscribes to system events.
     */
    private void subscribeToSystemEvents() {
        natsService.subscribeToSubject("system.events", (subject, message) -> {
            logger.info("⚙️ Processing system event: {}", message);
            
            try {
                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("type").asText();
                
                switch (eventType) {
                    case "system.startup":
                        handleSystemStartup(jsonNode);
                        break;
                    case "system.shutdown":
                        handleSystemShutdown(jsonNode);
                        break;
                    case "system.error":
                        handleSystemError(jsonNode);
                        break;
                    default:
                        logger.warn("Unknown system event type: {}", eventType);
                }
            } catch (Exception e) {
                logger.error("Error processing system event: {}", message, e);
            }
        });
    }

    private void handleUserCreated(JsonNode userData) {
        String userId = userData.get("id").asText();
        String userName = userData.get("name").asText();
        logger.info("✅ User created - ID: {}, Name: {}", userId, userName);
        
        // Add your business logic here
        // e.g., send welcome email, create user profile, etc.
    }

    private void handleUserUpdated(JsonNode userData) {
        String userId = userData.get("id").asText();
        logger.info("🔄 User updated - ID: {}", userId);
        
        // Add your business logic here
        // e.g., update cache, notify other services, etc.
    }

    private void handleUserDeleted(JsonNode userData) {
        String userId = userData.get("id").asText();
        logger.info("❌ User deleted - ID: {}", userId);
        
        // Add your business logic here
        // e.g., cleanup data, notify other services, etc.
    }

    private void handleSystemStartup(JsonNode systemData) {
        String serviceName = systemData.get("service").asText();
        logger.info("🚀 System startup - Service: {}", serviceName);
        
        // Add your business logic here
        // e.g., initialize connections, load configurations, etc.
    }

    private void handleSystemShutdown(JsonNode systemData) {
        String serviceName = systemData.get("service").asText();
        logger.info("🛑 System shutdown - Service: {}", serviceName);
        
        // Add your business logic here
        // e.g., cleanup resources, save state, etc.
    }

    private void handleSystemError(JsonNode systemData) {
        String serviceName = systemData.get("service").asText();
        String errorMessage = systemData.get("error").asText();
        logger.error("💥 System error - Service: {}, Error: {}", serviceName, errorMessage);
        
        // Add your business logic here
        // e.g., alert monitoring, retry logic, etc.
    }
} 