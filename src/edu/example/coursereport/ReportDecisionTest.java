package edu.example.coursereport;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public final class ReportDecisionTest {
    public static void main(String[] args) {
        Clock today = Clock.fixed(Instant.parse("2026-10-23T00:00:00Z"), ZoneOffset.UTC);
        Course course = new Course("Teaching with Evidence", LocalDate.of(2026, 10, 20), List.of(
                new Learner("Maya", 1, 2), new Learner("Leo", 2, 2)));
        String report = new CourseReportService(today).buildEducatorReport(course);
        if (!report.contains("Late learners: 1")) throw new AssertionError(report);
        System.out.println("decision test passed: 1 late learner");
    }
}
