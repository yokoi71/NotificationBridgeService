package com.notificationbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class NotificationBridgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationBridgeServiceApplication.class, args);
    }
}
