package com.chatapp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Sends an SMS via the Twilio API to notify a user they received a chat
 * message while they were offline.
 *
 * This is OFF by default (twilio.enabled=false in application.properties)
 * so the app runs and can be demoed without needing a Twilio account.
 * Set twilio.enabled=true and fill in your Account SID / Auth Token / phone
 * number (from a free Twilio trial account) to turn it on.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.from-number:}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (twilioEnabled) {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio SMS notifications enabled.");
        } else {
            logger.info("Twilio SMS notifications disabled (set twilio.enabled=true to turn on).");
        }
    }

    /**
     * Sends an SMS to notify a user of a new message.
     * Fails silently (logs a warning) if Twilio isn't configured or the
     * request fails - a notification failure should never crash the chat flow.
     */
    public void notifyOfflineUser(String toPhoneNumber, String fromUser, String messagePreview) {
        if (!twilioEnabled) {
            logger.debug("Skipped SMS notification (Twilio disabled): {} -> {}", fromUser, messagePreview);
            return;
        }

        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromNumber),
                    "New chat message from " + fromUser + ": " + messagePreview
            ).create();
            logger.info("SMS notification sent to {}", toPhoneNumber);
        } catch (Exception e) {
            logger.warn("Failed to send SMS notification: {}", e.getMessage());
        }
    }
}
