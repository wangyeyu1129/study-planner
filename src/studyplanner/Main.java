package studyplanner;

import studyplanner.model.Course;
import studyplanner.model.Task;

public class Main {
    public static void main(String[] args) {
        Course csc207 = new Course("CSC207", "Software Design");

        Task assignment = new Task(
                "Assignment 1",
                "2026-09-25"
        );

        Task quiz = new Task(
                "Quiz 1",
                "2026-10-02"
        );

        csc207.addTask(assignment);
        csc207.addTask(quiz);

        System.out.println(csc207.getCourseCode());
        System.out.println(csc207.getCourseName());

        for (Task task : csc207.getTasks()) {
            System.out.println(
                    task.getTitle() + " - " + task.getDueDate()
            );
        }
    }
}
