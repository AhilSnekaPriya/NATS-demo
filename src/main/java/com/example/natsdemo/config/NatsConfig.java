package com.example.natsdemo.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.Duration;

/**
 * NATS Configuration class following Spring Boot best practices.
 * Provides centralized configuration for NATS connection and settings.
 */
@Configuration
public class NatsConfig {

    @Value("${nats.server.url:nats://localhost:4222}")
    private String natsServerUrl;

    @Value("${nats.connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${nats.reconnect.wait:1000}")
    private int reconnectWait;

    @Value("${nats.max.reconnect:60}")
    private int maxReconnect;

    /**
     * Creates and configures the NATS connection bean.
     * Uses Spring's @Primary annotation to ensure this is the default connection.
     *
     * @return Configured NATS connection
     * @throws IOException if connection fails
     * @throws InterruptedException if connection is interrupted
     */
    @Bean
    @Primary
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(natsServerUrl)
                .connectionTimeout(Duration.ofMillis(connectionTimeout))
                .reconnectWait(Duration.ofMillis(reconnectWait))
                .maxReconnects(maxReconnect)
                .connectionName("spring-boot-nats-demo")
                .build();

        return Nats.connect(options);
    }
} 