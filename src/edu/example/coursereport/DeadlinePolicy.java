package edu.example.coursereport;

import java.time.Clock;
import java.time.LocalDate;

public final class DeadlinePolicy {
    private final Clock clock;

    public DeadlinePolicy(Clock clock) {
        this.clock = clock;
    }

    public boolean isLate(Course course, Learner learner) {
        return LocalDate.now(clock).isAfter(course.deadline())
                && learner.completedLessons() < learner.totalLessons();
    }
}
