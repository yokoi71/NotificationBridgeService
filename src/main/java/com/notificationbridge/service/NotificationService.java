package com.notificationbridge.service;

import com.notificationbridge.model.LeadEvent;

/**
 * Contract for sending a notification for a lead to a single channel (e.g. Slack or SMS).
 */
public interface NotificationService {

    /**
     * Send notification for the given lead. May throw after retries are exhausted.
     */
    void send(LeadEvent lead);
}
