package com.notificationbridge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("!dev")
@Component
@RequiredArgsConstructor
@Slf4j
public class RealSlackNotifier implements SlackNotifier {

    private final RestTemplate restTemplate;

    @Override
    public void post(String webhookUrl, String jsonPayload) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalArgumentException("Slack webhook URL is not configured");
        }
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(jsonPayload, headers);
        var response = restTemplate.postForEntity(webhookUrl, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Slack webhook returned " + response.getStatusCode());
        }
        log.debug("Slack notification sent successfully");
    }
}
