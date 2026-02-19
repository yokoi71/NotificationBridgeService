package com.notificationbridge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@Slf4j
public class MockSlackNotifier implements SlackNotifier {

    @Override
    public void post(String webhookUrl, String jsonPayload) {
        log.info("[MOCK SLACK] Would post to webhook (payload length={}): {}", jsonPayload.length(), jsonPayload);
        // Simulate success
    }
}
