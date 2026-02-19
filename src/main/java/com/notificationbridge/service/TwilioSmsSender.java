package com.notificationbridge.service;

/**
 * Sends an SMS to a phone number.
 */
public interface TwilioSmsSender {

    void send(String toPhoneNumber, String messageBody);
}
