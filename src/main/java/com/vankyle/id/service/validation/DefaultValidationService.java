package com.vankyle.id.service.validation;

import com.vankyle.id.data.repository.UserRepository;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.validation.totp.DefaultTotpValidator;
import com.vankyle.id.service.validation.totp.TotpGenerator;
import com.vankyle.id.service.validation.totp.TotpValidator;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class DefaultValidationService implements ValidationService {
    private final UserRepository userRepository;
    private final TotpValidator totpValidator = new DefaultTotpValidator();

    public DefaultValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CodeVerification validateVerificationLinkCode(String code, String purpose) {
        var secureRandom = new SecureRandom();
        String fakeUsername = Integer.toString(secureRandom.nextInt(100000, 999999));
        String fakeCode = Integer.toString(secureRandom.nextInt(100000, 999999));
        var fakeSecret = new byte[64];
        secureRandom.nextBytes(fakeSecret);
        if (code == null) {
            return new CodeVerification(false, fakeUsername);
        }
        // Decode the code from base64
        String decodedCode = new String(Base64.getDecoder().decode(code));
        // Split the code into username, timestamp and code
        String[] codeParts = decodedCode.split(":");
        Instant verificationCodeTimeStamp = null;
        String username = null;
        String verificationCode = null;
        String purposeInCode = null;
        boolean success = codeParts.length == 4;
        if (!success) {
            // Avoiding time attack
            username = fakeUsername;
            verificationCodeTimeStamp = Instant.ofEpochSecond(Long.parseLong("0"));
            verificationCode = fakeCode;
            purposeInCode = fakeUsername;
        } else {
            username = codeParts[0];
            verificationCodeTimeStamp = Instant.ofEpochSecond(Long.parseLong(codeParts[1]));
            verificationCode = codeParts[2];
            purposeInCode = codeParts[3];
        }
        byte[] verificationSecret = null;
        var user = userRepository.findByUsername(username);
        if (user != null && user.getVerificationSecret() != null) {
            verificationSecret = user.getVerificationSecret();
            success = true;
        } else {
            verificationSecret = fakeSecret;
            success = false;
        }
        // Check if code is expired
        success = success && verificationCodeTimeStamp.plus(30, ChronoUnit.MINUTES).isAfter(Instant.now());
        // Check if the code is valid
        success = success && totpValidator.verify(
                verificationCode,
                verificationSecret,
                verificationCodeTimeStamp.getEpochSecond(), 8, "HmacSHA512");
        success = success && timeSafeStringCompare(purposeInCode.getBytes(), purpose.getBytes());
        return new CodeVerification(success, username);
    }

    @Override
    public String generateVerificationLinkCode(User user, String purpose) {
        var secureRandom = new SecureRandom();
        var fakeSecret = new byte[64];
        secureRandom.nextBytes(fakeSecret);
        long timestamp = Instant.now().getEpochSecond();
        var verificationSecret = user.getSecurityStamp() == null ? fakeSecret : user.getSecurityStamp();
        var verificationCode = TotpGenerator.generateTOTP(verificationSecret, timestamp, 8, "HmacSHA512");
        var verificationCodeTimeStampString = Long.toString(timestamp);
        var username = user.getUsername();
        var code = username + ":" + verificationCodeTimeStampString + ":" + verificationCode + ":" + purpose;
        return Base64.getEncoder().encodeToString(code.getBytes());
    }

    @Override
    public String generateEmailConfirmationCode(User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        long timestamp = Instant.now().getEpochSecond() / 60 / 60;
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 32);
        return TotpGenerator.generateTOTP(verificationSecret, timestamp, 6, "HmacSHA256");
    }

    @Override
    public CodeVerification validateEmailConfirmationCode(String code, User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        long timestamp = Instant.now().getEpochSecond() / 60 / 60;
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 32);
        var success = totpValidator.verify(code, verificationSecret, timestamp, 6, "HmacSHA256");
        return new CodeVerification(success, user.getUsername());
    }

    @Override
    public String generatePhoneConfirmationCode(User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        long timestamp = Instant.now().getEpochSecond() / 60 / 20;
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 32);
        return TotpGenerator.generateTOTP(verificationSecret, timestamp, 6, "HmacSHA256");
    }

    @Override
    public CodeVerification validatePhoneConfirmationCode(String code, User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        long timestamp = Instant.now().getEpochSecond() / 60 / 20;
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 32);
        var success = totpValidator.verify(code, verificationSecret, timestamp, 6, "HmacSHA256");
        return new CodeVerification(success, user.getUsername());
    }

    @Override
    public String generate2FACode(User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        long timestamp = Instant.now().getEpochSecond() / 30;
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 20);
        return TotpGenerator.generateTOTP(verificationSecret, timestamp, 6, "HmacSHA1");
    }

    @Override
    public CodeVerification validate2FACode(String code, User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getSecurityStamp(), "User must have a verification secret");
        Assert.notNull(code, "Code must not be null");
        var verificationSecret = sliceArray(user.getSecurityStamp(), 0, 20);
        var success = totpValidator.verify(code, verificationSecret);
        return new CodeVerification(success, user.getUsername());
    }

    private boolean timeSafeStringCompare(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private byte[] sliceArray(byte[] arr, int start, int end) {
        byte[] result = new byte[end - start];
        if (end - start >= 0) System.arraycopy(arr, start, result, 0, end - start);
        return result;
    }
}
