package hr.smit.gls.client;

import java.util.List;

/**
 * MyGLS JSON responses carry PDF/byte payloads (labels, POD) as a plain JSON array of
 * unsigned integers (0-255) rather than a base64 string, so Jackson can't map them
 * directly to a Java byte[].
 */
public final class GlsBinaryUtil {

    private GlsBinaryUtil() {
    }

    public static byte[] toBytes(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return new byte[0];
        }
        byte[] result = new byte[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = (byte) (int) values.get(i);
        }
        return result;
    }
}
