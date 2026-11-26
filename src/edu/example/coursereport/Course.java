package edu.example.coursereport;

import java.time.LocalDate;
import java.util.List;

public record Course(String title, LocalDate deadline, List<Learner> learners) {
}
