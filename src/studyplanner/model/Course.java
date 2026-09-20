package studyplanner.model;

import java.util.ArrayList;
import java.util.Objects;

public class Course {
    private final String courseCode;
    private final String courseName;
    private final ArrayList<Task> tasks = new ArrayList<>();

    public Course(String courseCode, String courseName) {
        if (courseCode == null || courseCode.trim().isEmpty()
                || courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code and name must not be blank.");
        }
        this.courseCode = courseCode.trim();
        this.courseName = courseName.trim();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void addTask(Task task) {
        Objects.requireNonNull(task, "Task is required.");
        if (tasks.contains(task)) {
            throw new IllegalArgumentException("This task is already in the course.");
        }
        tasks.add(task);
    }

    public boolean removeTask(Task task) {
        return tasks.remove(task);
    }

    /** Returns all exact title matches, ignoring case; duplicate titles are allowed. */
    public ArrayList<Task> findTasksByTitle(String title) {
        ArrayList<Task> matches = new ArrayList<>();
        if (title != null) {
            for (Task task : tasks) {
                if (task.getTitle().equalsIgnoreCase(title.trim())) {
                    matches.add(task);
                }
            }
        }
        return matches;
    }
}
