package com.notificationbridge.service;

/**
 * Sends a JSON payload to a Slack webhook URL.
 */
public interface SlackNotifier {

    void post(String webhookUrl, String jsonPayload);
}
