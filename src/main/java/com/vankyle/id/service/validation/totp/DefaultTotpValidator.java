package com.vankyle.id.service.validation.totp;

import lombok.Setter;


public class DefaultTotpValidator implements TotpValidator {
    @Setter
    private long defaultPeriod = 30;
    @Setter
    private int defaultDigits = 6;
    @Setter
    private int allowedTimeDrift = 1;
    @Setter
    private TimeProvider timeProvider;

    /**
     * Default constructor with 30 seconds period and 6 digits, using system time provider
     */
    public DefaultTotpValidator() {
        this(new SystemTimeProvider());
    }

    /**
     * Constructor using custom provider
     */
    public DefaultTotpValidator(TimeProvider timeProvider) {

        this.timeProvider = timeProvider;
    }

    @Override
    public boolean verify(String code, byte[] secret) {
        long currentTime = timeProvider.getTime();
        long time = currentTime / 1000 / defaultPeriod;
        return verify(code, secret, time);
    }

    @Override
    public boolean verify(String code, byte[] secret, long time) {
        return verify(code, secret, time, defaultDigits);
    }

    @Override
    public boolean verify(String code, byte[] secret, long time, int digits) {
        return verify(code, secret, time, digits, TotpAlgorithm.SHA1);
    }

    @Override
    public boolean verify(String code, byte[] secret, long time, int digits, String algorithm) {
        boolean success = false;
        for (int i = -allowedTimeDrift; i <= allowedTimeDrift; i++) {
            String hash = TotpGenerator.generateTOTP(secret, time + i, digits, algorithm);
            success = timeSafeStringCompare(hash, code) || success;
        }
        return success;
    }

    private boolean timeSafeStringCompare(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
