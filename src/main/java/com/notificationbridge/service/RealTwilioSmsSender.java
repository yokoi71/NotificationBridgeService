package com.notificationbridge.service;

import com.notificationbridge.config.AppConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Profile("!dev")
@Component
@Slf4j
public class RealTwilioSmsSender implements TwilioSmsSender {

    private final AppConfig.TwilioProperties twilioProperties;

    public RealTwilioSmsSender(AppConfig.TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        if (twilioProperties.getAccountSid() != null && !twilioProperties.getAccountSid().isBlank()) {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        }
    }

    @Override
    public void send(String toPhoneNumber, String messageBody) {
        if (twilioProperties.getFromNumber() == null || twilioProperties.getFromNumber().isBlank()) {
            throw new IllegalArgumentException("Twilio from-number is not configured");
        }
        Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioProperties.getFromNumber()),
                messageBody
        ).create();
        log.debug("SMS sent to {}", toPhoneNumber);
    }
}
