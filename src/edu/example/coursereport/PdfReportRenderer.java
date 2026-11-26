package edu.example.coursereport;

import java.nio.charset.StandardCharsets;

public final class PdfReportRenderer {
    public byte[] render(String text) {
        String escaped = text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")
                .replace("\n", ") Tj 0 -14 Td (");
        String body = "BT /F1 11 Tf 50 760 Td (" + escaped + ") Tj ET";
        String pdf = "%PDF-1.4\n" +
                "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
                "2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n" +
                "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Resources<</Font<</F1 4 0 R>>>>/Contents 5 0 R>>endobj\n" +
                "4 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n" +
                "5 0 obj<</Length " + body.getBytes(StandardCharsets.US_ASCII).length + ">>stream\n" + body + "\nendstream endobj\n" +
                "xref\n0 6\n0000000000 65535 f \ntrailer<</Size 6/Root 1 0 R>>\nstartxref\n0\n%%EOF";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }
}
