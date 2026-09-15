package hr.smit.gls.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MyGLS API requires the password as a SHA-512 hash, sent as an array of unsigned byte
 * values (0-255) rather than a base64 string - see "Appendix C: Password SHA512
 * implementations" in the API documentation.
 */
public final class GlsPasswordEncoder {

    private GlsPasswordEncoder() {
    }

    public static int[] toUnsignedByteArray(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            int[] result = new int[hash.length];
            for (int i = 0; i < hash.length; i++) {
                result[i] = hash[i] & 0xFF;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm not available", e);
        }
    }
}
