package com.notificationbridge.service;

import com.notificationbridge.config.AppConfig;
import com.notificationbridge.exception.NotificationFailedException;
import com.notificationbridge.model.LeadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackNotificationService implements NotificationService {

    private final AppConfig.SlackProperties slackProperties;
    private final SlackNotifier slackNotifier;

    @Override
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void send(LeadEvent lead) {
        String payload = buildSlackPayload(lead);
        slackNotifier.post(slackProperties.getWebhookUrl(), payload);
    }

    @Recover
    public void recover(Exception e, LeadEvent lead) {
        log.error("Slack notification failed for lead {} after retries: {}", lead.getLeadId(), e.getMessage(), e);
        throw new NotificationFailedException("slack", "Slack notification failed: " + e.getMessage(), e);
    }

    private String buildSlackPayload(LeadEvent lead) {
        // Slack webhook accepts a JSON payload with "text" or "blocks". Use text with full lead info.
        String text = String.format(
                "*New HIGH priority lead*\n" +
                        "• Lead ID: %s\n" +
                        "• Name: %s\n" +
                        "• Company: %s\n" +
                        "• Email: %s\n" +
                        "• Phone: %s\n" +
                        "• Priority: %s\n" +
                        "• Created: %s",
                lead.getLeadId(),
                lead.getName(),
                lead.getCompany() != null ? lead.getCompany() : "—",
                lead.getEmail() != null ? lead.getEmail() : "—",
                lead.getPhone() != null ? lead.getPhone() : "—",
                lead.getPriority(),
                lead.getCreatedAt() != null
                        ? lead.getCreatedAt().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        : "—"
        );
        Map<String, Object> body = new HashMap<>();
        body.put("text", text);
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
        } catch (Exception ex) {
            throw new NotificationFailedException("slack", "Failed to build Slack payload", ex);
        }
    }
}
