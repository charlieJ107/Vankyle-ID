package com.vankyle.id.service.validation.totp.base32;

/**
 * <p>
 * Operations on {@link CharSequence} that are {@code null} safe.
 * </p>
 * <p>
 * Copied from Apache Commons Lang r1586295 on April 10, 2014 (day of 3.3.2 release).
 * </p>
 *
 * @see CharSequence
 */
public class CharSequenceUtils {

    /**
     * Green implementation of regionMatches.
     *
     * <p>Note: This function differs from the current implementation in Apache Commons Lang
     * where the input indices are not valid. It is only used within this package.
     *
     * @param cs
     *            the {@code CharSequence} to be processed
     * @param ignoreCase
     *            whether or not to be case-insensitive
     * @param thisStart
     *            the index to start on the {@code cs} CharSequence
     * @param substring
     *            the {@code CharSequence} to be looked for
     * @param start
     *            the index to start on the {@code substring} CharSequence
     * @param length
     *            character length of the region
     * @return whether the region matched
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                                 final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) &&
                    Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }
}