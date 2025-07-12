package com.example.natsdemo.component;

import com.example.natsdemo.model.Iso8583Message;
import com.example.natsdemo.service.NatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Component that publishes test ISO 8583 messages to NATS for testing conversion.
 * Implements CommandLineRunner to start publishing when the application starts.
 */
@Component
public class IsoTestPublisher implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(IsoTestPublisher.class);

    private final NatsService natsService;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public IsoTestPublisher(NatsService natsService, ObjectMapper objectMapper) {
        this.natsService = natsService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Subscribe to converted ISO 20022 messages
        subscribeToConvertedMessages();
        
        // Schedule periodic test message publishing
        scheduleTestMessagePublishing();
        
        logger.info("ISO test publisher initialized");
    }

    /**
     * Subscribes to converted ISO 20022 messages to verify conversion.
     */
    private void subscribeToConvertedMessages() {
        natsService.subscribeToSubject("iso20022.messages", (subject, message) -> {
            logger.info("Received converted ISO 20022 message on subject '{}': {}", subject, message);
            
            try {
                // Parse and log the converted message
                Object convertedMessage = objectMapper.readValue(message, Object.class);
                logger.info("Successfully parsed converted ISO 20022 message");
                
            } catch (Exception e) {
                logger.error("Error parsing converted ISO 20022 message: {}", message, e);
            }
        });
        
        logger.info("Subscribed to converted ISO 20022 messages");
    }

    /**
     * Schedules periodic publishing of test ISO 8583 messages.
     */
    private void scheduleTestMessagePublishing() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                publishTestIso8583Message();
            } catch (Exception e) {
                logger.error("Error publishing test ISO 8583 message", e);
            }
        }, 30, 60, TimeUnit.SECONDS); // Start after 30 seconds, then every 60 seconds
        
        logger.info("Scheduled periodic ISO 8583 test message publishing");
    }

    /**
     * Publishes a test ISO 8583 message to NATS.
     */
    private void publishTestIso8583Message() {
        try {
            Iso8583Message testMessage = createTestIso8583Message();
            String testMessageJson = objectMapper.writeValueAsString(testMessage);
            
            natsService.publishMessage("iso8583.messages", testMessageJson);
            
            logger.info("Published test ISO 8583 message: {}", testMessage);
            
        } catch (Exception e) {
            logger.error("Error publishing test ISO 8583 message", e);
        }
    }

    /**
     * Creates a test ISO 8583 message with realistic data.
     */
    private Iso8583Message createTestIso8583Message() {
        Iso8583Message message = new Iso8583Message();
        
        // Set basic transaction data
        message.setMti("0200"); // Financial transaction request
        message.setPan("4111111111111111");
        message.setProcessingCode("000000"); // Purchase
        message.setAmount("150.00");
        message.setStan(generateStan());
        message.setTime(getCurrentTime());
        message.setDate(getCurrentDate());
        
        // Set merchant and transaction details
        message.setMerchantType("5411"); // Grocery stores
        message.setPosEntryMode("021"); // Manual entry
        message.setCardSequence("001");
        message.setAcquirerId("123456");
        message.setForwarderId("654321");
        message.setTrack2("4111111111111111=123456789012345");
        message.setRetrievalRef(generateRetrievalRef());
        message.setAuthCode("123456");
        message.setResponseCode("00"); // Approved
        message.setCardAcceptor("12345678");
        message.setCardAcceptorId("123456789012345");
        message.setCardAcceptorName("DEMO GROCERY STORE");
        message.setCurrencyCode("840"); // USD
        
        return message;
    }

    /**
     * Generates a unique System Trace Audit Number.
     */
    private String generateStan() {
        return String.format("%06d", (int) (Math.random() * 999999));
    }

    /**
     * Generates a unique retrieval reference number.
     */
    private String generateRetrievalRef() {
        return String.format("%012d", (long) (Math.random() * 999999999999L));
    }

    /**
     * Gets the current time in HHMMSS format.
     */
    private String getCurrentTime() {
        return java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
    }

    /**
     * Gets the current date in MMDD format.
     */
    private String getCurrentDate() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));
    }
} 