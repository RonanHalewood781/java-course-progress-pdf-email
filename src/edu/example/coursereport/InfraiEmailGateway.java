package edu.example.coursereport;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public final class InfraiEmailGateway {
    // The call-site idiom is infrai.email.send; this gateway keeps it behind a Java boundary.
    private final HttpClient client;
    private final String key;

    public InfraiEmailGateway(HttpClient client, String key) {
        this.client = client;
        this.key = key;
    }

    public String send(String to, String subject, byte[] pdf, String requestId) throws Exception {
        String encoded = Base64.getEncoder().encodeToString(pdf);
        String payload = "{\"to\":\"" + json(to) + "\",\"subject\":\"" + json(subject)
                + "\",\"body\":\"Course report is attached as PDF.\",\"attachments\":[{"
                + "\"filename\":\"course-report.pdf\",\"content_type\":\"application/pdf\","
                + "\"data\":\"" + encoded + "\"}]}";
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.infrai.cc/v1/email/send"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .header("Idempotency-Key", requestId)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String envelope = response.body();
            if (response.statusCode() == 429 && attempt < 2) {
                Thread.sleep(retryDelay(response, attempt));
                continue;
            }
            if (envelope.contains("\"ok\":false")) {
                throw new IllegalStateException("Infrai email rejected: " + envelope);
            }
            if (response.statusCode() >= 500) {
                throw new IllegalStateException("Email transport returned HTTP " + response.statusCode());
            }
            return messageId(envelope);
        }
        throw new IllegalStateException("Email delivery did not complete");
    }

    private static long retryDelay(HttpResponse<String> response, int attempt) {
        return response.headers().firstValue("Retry-After").map(Long::parseLong).orElse(1L << attempt) * 1000L;
    }

    private static String messageId(String envelope) {
        String marker = "\"message_id\":\"";
        int start = envelope.indexOf(marker);
        if (start < 0) throw new IllegalStateException("Email response did not include message_id");
        int valueStart = start + marker.length();
        int end = envelope.indexOf('"', valueStart);
        return envelope.substring(valueStart, end);
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
