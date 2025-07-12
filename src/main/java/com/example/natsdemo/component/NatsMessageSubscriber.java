package com.example.natsdemo.component;

import com.example.natsdemo.service.NatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component that subscribes to NATS messages and demonstrates message handling.
 * Implements CommandLineRunner to start subscriptions when the application starts.
 */
@Component
public class NatsMessageSubscriber implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(NatsMessageSubscriber.class);

    private final NatsService natsService;

    @Autowired
    public NatsMessageSubscriber(NatsService natsService) {
        this.natsService = natsService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Subscribe to a demo subject
        subscribeToDemoSubject();
        
        // Subscribe to a JSON messages subject
        subscribeToJsonMessages();
        
        logger.info("NATS message subscribers initialized");
    }

    /**
     * Subscribes to a demo subject for simple text messages.
     */
    private void subscribeToDemoSubject() {
        natsService.subscribeToSubject("demo.messages", (subject, message) -> {
            logger.info("Received message on subject '{}': {}", subject, message);
            
            // Here you can add your custom message processing logic
            processDemoMessage(message);
        });
    }

    /**
     * Subscribes to a subject for JSON messages.
     */
    private void subscribeToJsonMessages() {
        natsService.subscribeToSubject("demo.json", (subject, message) -> {
            logger.info("Received JSON message on subject '{}': {}", subject, message);
            
            // Here you can add your custom JSON message processing logic
            processJsonMessage(message);
        });
    }

    /**
     * Processes demo messages with custom business logic.
     *
     * @param message The received message
     */
    private void processDemoMessage(String message) {
        try {
            // Add your custom message processing logic here
            logger.debug("Processing demo message: {}", message);
            
            // Example: Check if message contains specific keywords
            if (message.toLowerCase().contains("error")) {
                logger.warn("Error message detected: {}", message);
            } else if (message.toLowerCase().contains("warning")) {
                logger.warn("Warning message detected: {}", message);
            } else {
                logger.info("Normal message processed: {}", message);
            }
            
        } catch (Exception e) {
            logger.error("Error processing demo message: {}", message, e);
        }
    }

    /**
     * Processes JSON messages with custom business logic.
     *
     * @param message The received JSON message
     */
    private void processJsonMessage(String message) {
        try {
            // Add your custom JSON message processing logic here
            logger.debug("Processing JSON message: {}", message);
            
            // Example: Parse JSON and process based on message type
            if (message.contains("\"type\"")) {
                logger.info("Typed JSON message received: {}", message);
            } else {
                logger.info("Generic JSON message received: {}", message);
            }
            
        } catch (Exception e) {
            logger.error("Error processing JSON message: {}", message, e);
        }
    }
} 