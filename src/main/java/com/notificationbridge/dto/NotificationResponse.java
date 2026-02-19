package com.notificationbridge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private NotificationStatus status;
    private ChannelStatus slack;
    private ChannelStatus sms;

    public enum NotificationStatus {
        NOTIFICATIONS_SENT,
        PARTIAL_FAILURE,
        SKIPPED
    }

    public enum ChannelStatus {
        SUCCESS,
        FAILED
    }
}
