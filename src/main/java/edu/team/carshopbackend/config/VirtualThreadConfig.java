package edu.team.carshopbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {

    /**
     * Returns an ExecutorService that creates virtual threads per task.
     * Useful for lightweight asynchronous tasks such as email sending.
     *
     * @return executor service using virtual threads
     */
    @Bean
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

