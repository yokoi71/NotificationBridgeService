package com.notificationbridge.service;

import com.notificationbridge.exception.NotificationFailedException;
import com.notificationbridge.model.LeadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioNotificationService implements NotificationService {

    private final TwilioSmsSender twilioSmsSender;

    @Override
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void send(LeadEvent lead) {
        String to = lead.getPhone() != null && !lead.getPhone().isBlank()
                ? lead.getPhone()
                : null;
        if (to == null) {
            throw new NotificationFailedException("sms", "Lead phone number is missing");
        }
        String body = buildSmsBody(lead);
        twilioSmsSender.send(to, body);
    }

    @Recover
    public void recover(Exception e, LeadEvent lead) {
        log.error("SMS notification failed for lead {} after retries: {}", lead.getLeadId(), e.getMessage(), e);
        throw new NotificationFailedException("sms", "SMS notification failed: " + e.getMessage(), e);
    }

    private String buildSmsBody(LeadEvent lead) {
        return String.format("New HIGH lead: %s, %s, %s",
                lead.getName(),
                lead.getCompany() != null ? lead.getCompany() : "—",
                lead.getPhone());
    }
}
