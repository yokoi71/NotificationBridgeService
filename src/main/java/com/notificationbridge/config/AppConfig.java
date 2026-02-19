package com.notificationbridge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "app")
    public AppSecurityProperties appSecurityProperties() {
        return new AppSecurityProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "slack")
    public SlackProperties slackProperties() {
        return new SlackProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "twilio")
    public TwilioProperties twilioProperties() {
        return new TwilioProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "retry")
    public RetryProperties retryProperties() {
        return new RetryProperties();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Data
    public static class AppSecurityProperties {
        private Security security = new Security();
        private Jwt jwt = new Jwt();

        @Data
        public static class Security {
            private String apiKey = "dev-api-key-change-me";
        }

        @Data
        public static class Jwt {
            private String issuer = "https://notification-bridge";
            private String audience = "notification-bridge";
        }
    }

    @Data
    public static class SlackProperties {
        private String webhookUrl = "";
    }

    @Data
    public static class TwilioProperties {
        private String accountSid = "";
        private String authToken = "";
        private String fromNumber = "";
    }

    @Data
    public static class RetryProperties {
        private int maxAttempts = 3;
        private long delayMs = 1000;
        private double multiplier = 2;
    }
}
