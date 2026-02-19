package com.notificationbridge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@Slf4j
public class MockTwilioSmsSender implements TwilioSmsSender {

    @Override
    public void send(String toPhoneNumber, String messageBody) {
        log.info("[MOCK TWILIO] Would send SMS to {}: {}", toPhoneNumber, messageBody);
        // Simulate success
    }
}
