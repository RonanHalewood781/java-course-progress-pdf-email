package edu.example.coursereport;

import java.time.Clock;

public final class CourseReportService {
    private final DeadlinePolicy deadlinePolicy;

    public CourseReportService(Clock clock) {
        this.deadlinePolicy = new DeadlinePolicy(clock);
    }

    public String buildEducatorReport(Course course) {
        long late = course.learners().stream()
                .filter(learner -> deadlinePolicy.isLate(course, learner))
                .count();
        StringBuilder report = new StringBuilder();
        report.append("Course: ").append(course.title()).append('\n');
        report.append("Deadline: ").append(course.deadline()).append('\n');
        report.append("Learners: ").append(course.learners().size()).append('\n');
        report.append("Late learners: ").append(late).append('\n');
        for (Learner learner : course.learners()) {
            report.append(learner.name()).append(" - ")
                    .append(learner.completedLessons()).append('/')
                    .append(learner.totalLessons()).append(" lessons\n");
        }
        return report.toString();
    }
}
