package com.oceanview.resort.patterns.adapter;

/**
 * Adapter target: External notification (Email/SMS) gateway.
 */
public interface NotificationGateway {
    boolean sendEmail(String to, String subject, String body);
    boolean sendSms(String to, String message);
}
