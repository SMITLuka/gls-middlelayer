package hr.smit.gls.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Hand-rolled minimal single-page PDF (no external library needed) used only by
 * {@code gls.mock-enabled} - lets the Pantheon/ARES side of the integration be
 * exercised (save-to-disk, open in default viewer) before real GLS credentials exist.
 */
public final class MockPdfGenerator {

    private MockPdfGenerator() {
    }

    public static byte[] generate(String clientReference, long parcelNumber) {
        List<String> lines = List.of(
                "GLS TEST NALJEPNICA (MOCK)",
                "Client Reference: " + clientReference,
                "Parcel Number: " + parcelNumber,
                "Ovo NIJE stvarna GLS naljepnica."
        );

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new java.util.ArrayList<>();

            write(out, "%PDF-1.4\n");

            offsets.add(out.size());
            write(out, "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n");

            offsets.add(out.size());
            write(out, "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n");

            offsets.add(out.size());
            write(out, "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 400 220]"
                    + "/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n");

            String content = buildContentStream(lines);
            offsets.add(out.size());
            write(out, "4 0 obj<</Length " + content.getBytes(StandardCharsets.US_ASCII).length + ">>\nstream\n");
            write(out, content);
            write(out, "\nendstream\nendobj\n");

            offsets.add(out.size());
            write(out, "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n");

            int xrefStart = out.size();
            write(out, "xref\n0 " + (offsets.size() + 1) + "\n");
            write(out, "0000000000 65535 f \n");
            for (int offset : offsets) {
                write(out, String.format("%010d 00000 n \n", offset));
            }
            write(out, "trailer<</Size " + (offsets.size() + 1) + "/Root 1 0 R>>\n");
            write(out, "startxref\n" + xrefStart + "\n%%EOF");

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String buildContentStream(List<String> lines) {
        StringBuilder sb = new StringBuilder("BT /F1 14 Tf 20 180 Td\n");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append("0 -22 Td\n");
            }
            sb.append('(').append(escape(lines.get(i))).append(") Tj\n");
        }
        sb.append("ET");
        return sb.toString();
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void write(ByteArrayOutputStream out, String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.US_ASCII));
    }
}
