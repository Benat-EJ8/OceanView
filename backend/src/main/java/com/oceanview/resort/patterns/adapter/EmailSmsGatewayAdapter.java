package com.oceanview.resort.patterns.adapter;


 // Adapter: Placeholder implementation for Email/SMS to integrate with real provider.

public class EmailSmsGatewayAdapter implements NotificationGateway {
    @Override
    public boolean sendEmail(String to, String subject, String body) {
        // Placeholder: wire to SendGrid, AWS SES, etc.
        return to != null && !to.isEmpty();
    }

    @Override
    public boolean sendSms(String to, String message) {
        // Placeholder: wire to Twilio, etc.
        return to != null && !to.isEmpty();
    }
}
