package com.vankyle.id.services.verification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailVerificationSender implements VerificationSender {
    private static final Log logger = LogFactory.getLog(SmtpEmailVerificationSender.class);
    private final JavaMailSender mailSender;

    public SmtpEmailVerificationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerification(String to, String code) {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        try {
            helper.setTo(to);
            helper.setText("Your verification code is: " + code, true);
            helper.setSubject("Verification code");
            mailSender.send(msg);
        } catch (MessagingException e) {
            logger.error("Failed to set message content", e);
        } catch (Exception e) {
            logger.error("Failed to send email", e);
        }

    }
}
