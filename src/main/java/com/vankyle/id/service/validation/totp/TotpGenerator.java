package com.vankyle.id.service.validation.totp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;

public class TotpGenerator {
    /**
     * This method uses the JCE to provide the crypto algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto:   the crypto algorithm (HmacSHA1, HmacSHA256,
     *                  HmacSHA512)
     * @param keyBytes: the bytes to use for the HMAC key
     * @param text:     the message or text to be authenticated
     */
    private static byte[] hmac_sha(String crypto, byte[] keyBytes,
                                   byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                    new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private static byte[] long2Byte(long time) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (time & 0xff);
            time >>= 8;
        }
        return result;
    }

    private static final int[] DIGITS_POWER
            // 0  1   2    3     4      5       6        7         8
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     * <p>
     * For SHA1 secret, 20 bytes required;
     * <br>
     * For SHA256 secret, 32 bytes required;
     * <br>
     * For SHA512 secret, 64 bytes required;
     * </p>
     *
     * @param key:        the shared secret byte array
     * @param time:       a value that reflects a time
     * @param codeDigits: number of digits to return
     * @param crypto:     the crypto function to use
     * @return a numeric String in base 10 that includes
     * truncation digits
     */

    public static String generateTOTP(byte[] key,
                                      long time,
                                      int codeDigits,
                                      String crypto) {
        // Get the HEX in a Byte[]
        byte[] msg = long2Byte(time);
        byte[] hash = hmac_sha(crypto, key, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary =
                ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        StringBuilder result = new StringBuilder(Integer.toString(otp));
        while (result.length() < codeDigits) {
            result.insert(0, "0");
        }
        return result.toString();
    }
}
