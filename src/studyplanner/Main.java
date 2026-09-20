package studyplanner;

import studyplanner.model.Course;
import studyplanner.model.Priority;
import studyplanner.model.Task;
import studyplanner.service.StudyPlanner;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StudyPlanner planner = new StudyPlanner();
        planner.addCourse(new Course("CSC207", "Software Design"));
        planner.addCourse(new Course("MAT244", "Ordinary Differential Equations"));

        LocalDate today = LocalDate.now();
        Task assignment = new Task("Assignment 1", today.plusDays(3), Priority.HIGH);
        Task quiz = new Task("Quiz 1", today.plusDays(7), Priority.MEDIUM);
        Task review = new Task("Midterm Review", today.plusDays(1), Priority.HIGH);
        Task reading = new Task("Reading", today.minusDays(1), Priority.LOW);

        planner.addTask("CSC207", quiz);
        planner.addTask("CSC207", assignment);
        planner.addTask("MAT244", review);
        planner.addTask("MAT244", reading);

        printTasks("Upcoming tasks (earliest first)", planner.getUpcomingTasks());
        printTasks("High priority", planner.getTasksByPriority(Priority.HIGH));

        planner.markTaskCompleted("CSC207", assignment);
        printTasks("Completed tasks", planner.getCompletedTasks());
        printTasks("Incomplete high priority", planner.getUpcomingTasks(Priority.HIGH));

        planner.markTaskIncomplete("CSC207", assignment);
        System.out.println("Assignment reopened: " + !assignment.isCompleted());
        printTasks("Find Quiz 1", planner.findTasksByTitle("csc207", "Quiz 1"));
        System.out.println("Quiz deleted: " + planner.removeTask("CSC207", quiz));
        printTasks("Remaining tasks by deadline", planner.getTasksSortedByDueDate());
    }

    private static void printTasks(String heading, List<Task> tasks) {
        System.out.println("\n" + heading + ":");
        for (Task task : tasks) {
            System.out.println("  " + task + " | Overdue: " + task.isOverdue());
        }
    }
}
