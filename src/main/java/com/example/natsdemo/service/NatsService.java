package com.example.natsdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * NATS Service class providing high-level operations for publishing and subscribing to NATS messages.
 * Follows Spring Boot service layer best practices with proper error handling and logging.
 */
@Service
public class NatsService {

    private static final Logger logger = LoggerFactory.getLogger(NatsService.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    @Autowired
    public NatsService(Connection natsConnection, ObjectMapper objectMapper) {
        this.natsConnection = natsConnection;
        this.objectMapper = objectMapper;
    }

    /**
     * Publishes a message to a NATS subject.
     *
     * @param subject The NATS subject to publish to
     * @param message The message to publish
     */
    public void publishMessage(String subject, String message) {
        try {
            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
            logger.info("Message published to subject: {}", subject);
        } catch (Exception e) {
            logger.error("Failed to publish message to subject: {}", subject, e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    /**
     * Publishes a JSON object to a NATS subject.
     *
     * @param subject The NATS subject to publish to
     * @param payload The object to serialize and publish
     */
    public void publishJsonMessage(String subject, Object payload) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            publishMessage(subject, jsonMessage);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize payload for subject: {}", subject, e);
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }

    /**
     * Subscribes to a NATS subject and processes messages asynchronously.
     *
     * @param subject The NATS subject to subscribe to
     * @param messageHandler The handler to process received messages
     */
    public void subscribeToSubject(String subject, MessageHandler messageHandler) {
        try {
            io.nats.client.Dispatcher dispatcher = natsConnection.createDispatcher((msg) -> {
                try {
                    String messageContent = new String(msg.getData(), StandardCharsets.UTF_8);
                    logger.debug("Received message on subject: {}", subject);
                    messageHandler.handleMessage(subject, messageContent);
                } catch (Exception e) {
                    logger.error("Error processing message from subject: {}", subject, e);
                }
            });
            dispatcher.subscribe(subject);
            logger.info("Subscribed to subject: {}", subject);
        } catch (Exception e) {
            logger.error("Failed to subscribe to subject: {}", subject, e);
            throw new RuntimeException("Failed to subscribe to subject", e);
        }
    }

    /**
     * Requests a message from a NATS subject and waits for a response.
     *
     * @param subject The NATS subject to request from
     * @param requestData The request data to send
     * @return The response message
     */
    public String requestMessage(String subject, String requestData) {
        try {
            Message response = natsConnection.request(subject, requestData.getBytes(StandardCharsets.UTF_8), DEFAULT_TIMEOUT);
            if (response != null) {
                return new String(response.getData(), StandardCharsets.UTF_8);
            } else {
                throw new RuntimeException("No response received within timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted for subject: {}", subject, e);
            throw new RuntimeException("Request interrupted", e);
        }
    }

    /**
     * Requests a message from a NATS subject with a custom timeout.
     *
     * @param subject The NATS subject to request from
     * @param requestData The request data to send
     * @param timeout The timeout duration
     * @return The response message
     */
    public String requestMessage(String subject, String requestData, Duration timeout) {
        try {
            Message response = natsConnection.request(subject, requestData.getBytes(StandardCharsets.UTF_8), timeout);
            if (response != null) {
                return new String(response.getData(), StandardCharsets.UTF_8);
            } else {
                throw new RuntimeException("No response received within timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted for subject: {}", subject, e);
            throw new RuntimeException("Request interrupted", e);
        }
    }

    /**
     * Functional interface for handling NATS messages.
     */
    @FunctionalInterface
    public interface MessageHandler {
        void handleMessage(String subject, String message);
    }

    /**
     * Gets the NATS connection status.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return natsConnection != null && natsConnection.getStatus() == Connection.Status.CONNECTED;
    }
} 