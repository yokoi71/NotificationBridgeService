package com.notificationbridge.service;

import com.notificationbridge.dto.NotificationResponse;
import com.notificationbridge.model.LeadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.notificationbridge.dto.NotificationResponse.ChannelStatus;
import static com.notificationbridge.dto.NotificationResponse.NotificationStatus;
import static com.notificationbridge.model.LeadEvent.LeadPriority;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadNotificationService {

    private final SlackNotificationService slackNotificationService;
    private final TwilioNotificationService twilioNotificationService;

    public NotificationResponse notify(LeadEvent lead) {
        if (lead.getPriority() != LeadPriority.HIGH) {
            log.debug("Skipping non-HIGH priority lead {}", lead.getLeadId());
            return NotificationResponse.builder()
                    .status(NotificationStatus.SKIPPED)
                    .slack(null)
                    .sms(null)
                    .build();
        }

        boolean slackSuccess = false;
        boolean smsSuccess = false;

        try {
            slackNotificationService.send(lead);
            slackSuccess = true;
        } catch (Exception e) {
            log.error("Slack notification failed for lead {}: {}", lead.getLeadId(), e.getMessage(), e);
        }

        try {
            twilioNotificationService.send(lead);
            smsSuccess = true;
        } catch (Exception e) {
            log.error("SMS notification failed for lead {}: {}", lead.getLeadId(), e.getMessage(), e);
        }

        NotificationStatus status = (slackSuccess && smsSuccess)
                ? NotificationStatus.NOTIFICATIONS_SENT
                : NotificationStatus.PARTIAL_FAILURE;

        return NotificationResponse.builder()
                .status(status)
                .slack(slackSuccess ? ChannelStatus.SUCCESS : ChannelStatus.FAILED)
                .sms(smsSuccess ? ChannelStatus.SUCCESS : ChannelStatus.FAILED)
                .build();
    }
}
