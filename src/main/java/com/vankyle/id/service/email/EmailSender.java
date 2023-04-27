package com.vankyle.id.service.email;

import jakarta.mail.MessagingException;

public interface EmailSender {
    void sendMail(String to, String subject, String content) throws MessagingException;
}
