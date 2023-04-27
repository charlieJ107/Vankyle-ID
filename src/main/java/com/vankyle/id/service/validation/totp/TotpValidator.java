package com.vankyle.id.service.validation.totp;

public interface TotpValidator {
    /**
     * Verify the code against the secret
     *
     * @param code   the code to verify
     * @param secret the secret
     * @return true if the code is valid, false otherwise
     */
    boolean verify(String code, byte[] secret);

    /**
     * Verify the code against the secret for a given time
     *
     * @param code   the code to verify
     * @param secret the secret
     * @param time   the time to use
     * @return true if the code is valid, false otherwise
     */
    boolean verify(String code, byte[] secret, long time);

    boolean verify(String code, byte[] secret, long time, int digits);
    boolean verify(String code, byte[] secret, long time, int digits, String algorithm);
}
