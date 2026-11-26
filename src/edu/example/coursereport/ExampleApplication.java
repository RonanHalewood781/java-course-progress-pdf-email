package edu.example.coursereport;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public final class ExampleApplication {
    public static void main(String[] args) throws Exception {
        String key = required("INFRAI_API_KEY");
        String recipient = System.getenv().getOrDefault("REPORT_TO", "educator@example.edu");
        Course course = new Course("Foundations of Learning Design", LocalDate.now().minusDays(2), List.of(
                new Learner("Maya", 3, 5), new Learner("Leo", 5, 5)));
        CourseReportService reports = new CourseReportService(Clock.systemUTC());
        byte[] pdf = new PdfReportRenderer().render(reports.buildEducatorReport(course));
        String id = new InfraiEmailGateway(HttpClient.newHttpClient(), key)
                .send(recipient, "Course progress report: " + course.title(), pdf, "course-report-" + course.title().hashCode());
        System.out.println("report sent: message_id=" + id);
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is required");
        return value;
    }
}
