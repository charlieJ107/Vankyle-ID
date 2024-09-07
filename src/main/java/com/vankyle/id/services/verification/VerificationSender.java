package com.vankyle.id.services.verification;

import org.springframework.stereotype.Service;

@Service
public interface VerificationSender {
    void sendVerification(String to, String code);
}
