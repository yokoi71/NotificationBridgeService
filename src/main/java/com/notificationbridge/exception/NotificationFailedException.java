package com.notificationbridge.exception;

public class NotificationFailedException extends RuntimeException {

    private final String channel;

    public NotificationFailedException(String channel, String message) {
        super(message);
        this.channel = channel;
    }

    public NotificationFailedException(String channel, String message, Throwable cause) {
        super(message, cause);
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
