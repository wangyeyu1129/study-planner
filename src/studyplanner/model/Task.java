package studyplanner.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    private final String title;
    private final LocalDate dueDate;
    private final Priority priority;
    private boolean completed;

    public Task(String title, LocalDate dueDate, Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title must not be blank.");
        }
        this.title = title.trim();
        this.dueDate = Objects.requireNonNull(dueDate, "Due date is required.");
        this.priority = Objects.requireNonNull(priority, "Priority is required.");
    }

    public Task(String title, LocalDate dueDate) {
        this(title, dueDate, Priority.MEDIUM);
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        completed = true;
    }

    public void markIncomplete() {
        completed = false;
    }

    public boolean isOverdue() {
        return !completed && dueDate.isBefore(LocalDate.now());
    }

    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    @Override
    public String toString() {
        return title + " | Due: " + dueDate + " | Priority: " + priority
                + " | " + (completed ? "Completed" : "Incomplete");
    }
}
