package studyplanner.service;

import studyplanner.model.Course;
import studyplanner.model.Priority;
import studyplanner.model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class StudyPlanner {
    private final ArrayList<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        Objects.requireNonNull(course, "Course is required.");
        if (findCourseByCode(course.getCourseCode()) != null) {
            throw new IllegalArgumentException("Course code already exists.");
        }
        courses.add(course);
    }

    public ArrayList<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public Course findCourseByCode(String courseCode) {
        if (courseCode != null) {
            for (Course course : courses) {
                if (course.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                    return course;
                }
            }
        }
        return null;
    }

    public boolean removeCourse(String courseCode) {
        return courses.remove(findCourseByCode(courseCode));
    }

    public boolean addTask(String courseCode, Task task) {
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            return false;
        }
        course.addTask(task);
        return true;
    }

    public boolean removeTask(String courseCode, Task task) {
        Course course = findCourseByCode(courseCode);
        return course != null && course.removeTask(task);
    }

    public boolean markTaskCompleted(String courseCode, Task task) {
        return setTaskCompleted(courseCode, task, true);
    }

    public boolean markTaskIncomplete(String courseCode, Task task) {
        return setTaskCompleted(courseCode, task, false);
    }

    private boolean setTaskCompleted(String courseCode, Task task, boolean completed) {
        Course course = findCourseByCode(courseCode);
        if (course == null || !course.getTasks().contains(task)) {
            return false;
        }
        if (completed) {
            task.markCompleted();
        } else {
            task.markIncomplete();
        }
        return true;
    }

    public ArrayList<Task> findTasksByTitle(String courseCode, String title) {
        Course course = findCourseByCode(courseCode);
        return course == null ? new ArrayList<>() : course.findTasksByTitle(title);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Course course : courses) {
            tasks.addAll(course.getTasks());
        }
        return tasks;
    }

    public ArrayList<Task> getCompletedTasks() {
        return getTasksByCompletion(true);
    }

    public ArrayList<Task> getIncompleteTasks() {
        return getTasksByCompletion(false);
    }

    private ArrayList<Task> getTasksByCompletion(boolean completed) {
        ArrayList<Task> tasks = getAllTasks();
        tasks.removeIf(task -> task.isCompleted() != completed);
        return tasks;
    }

    /** Includes both completed and incomplete tasks. */
    public ArrayList<Task> getTasksByPriority(Priority priority) {
        Objects.requireNonNull(priority, "Priority is required.");
        ArrayList<Task> tasks = getAllTasks();
        tasks.removeIf(task -> task.getPriority() != priority);
        return tasks;
    }

    /** Includes overdue tasks; equal dates retain their original order. */
    public ArrayList<Task> getUpcomingTasks() {
        ArrayList<Task> tasks = getIncompleteTasks();
        tasks.sort(Comparator.comparing(Task::getDueDate));
        return tasks;
    }

    public ArrayList<Task> getUpcomingTasks(Priority priority) {
        Objects.requireNonNull(priority, "Priority is required.");
        ArrayList<Task> tasks = getUpcomingTasks();
        tasks.removeIf(task -> task.getPriority() != priority);
        return tasks;
    }

    public ArrayList<Task> getTasksSortedByDueDate() {
        ArrayList<Task> tasks = getAllTasks();
        tasks.sort(Comparator.comparing(Task::getDueDate));
        return tasks;
    }
}
