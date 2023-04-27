package com.vankyle.id.service.email;

import org.apache.commons.logging.Log;

/**
 * A no-op implementation of {@link EmailSender} that does nothing. For use in tests and debugging.
 */
public class NoOpEmailSender implements EmailSender {
    private static final Log logger = org.apache.commons.logging.LogFactory.getLog(NoOpEmailSender.class);

    @Override
    public void sendMail(String to, String subject, String content) {
        // do nothing
        logger.info("to: " + to + ", subject: " + subject + ", content: " + content);
    }
}
