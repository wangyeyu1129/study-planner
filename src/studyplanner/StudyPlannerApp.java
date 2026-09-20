package studyplanner;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import studyplanner.model.Course;
import studyplanner.model.Priority;
import studyplanner.model.Task;
import studyplanner.service.StudyPlanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class StudyPlannerApp extends Application {

    private final StudyPlanner planner = new StudyPlanner();

    private final ListView<String> courseList = new ListView<>();
    private final TableView<Task> taskTable = new TableView<>();
    private final Label summary = new Label();

    @Override
    public void start(Stage stage) {
        loadSampleData();
        configureTable();

        // 左侧：课程列表
        courseList.getItems().add("全部课程");

        for (Course course : planner.getCourses()) {
            courseList.getItems().add(course.getCourseCode());
        }

        courseList.setPrefWidth(180);

        courseList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    refreshTasks();
                });

        VBox sidebar = new VBox(
                12,
                new Label("我的课程"),
                courseList
        );
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);

        // 顶部：标题和统计
        Label title = new Label("Study Planner");
        title.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        Button addButton = new Button("添加任务");
        Button statusButton = new Button("完成 / 恢复");
        Button deleteButton = new Button("删除任务");

        addButton.setOnAction(event -> showAddTaskDialog());
        statusButton.setOnAction(event -> changeSelectedTaskStatus());
        deleteButton.setOnAction(event -> deleteSelectedTask());

// 没选中任务时，不能修改或删除
        statusButton.disableProperty().bind(
                taskTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
        );

        deleteButton.disableProperty().bind(
                taskTable.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
        );

        HBox toolbar = new HBox(
                10,
                addButton,
                statusButton,
                deleteButton
        );

        VBox header = new VBox(12, title, summary, toolbar);
        header.setPadding(new Insets(20));

        // 整体布局
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(taskTable);

        BorderPane.setMargin(
                taskTable,
                new Insets(20, 20, 20, 0)
        );

        courseList.getSelectionModel().selectFirst();

        stage.setTitle("Study Planner");
        stage.setScene(new Scene(root, 1050, 650));
        stage.setMinWidth(800);
        stage.setMinHeight(450);
        stage.show();
    }

    private void configureTable() {
        TableColumn<Task, String> titleColumn =
                new TableColumn<>("任务名称");

        titleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getTitle()
                )
        );
        titleColumn.setPrefWidth(260);

        TableColumn<Task, String> dateColumn =
                new TableColumn<>("截止日期");

        dateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDueDate().toString()
                )
        );
        dateColumn.setPrefWidth(140);

        TableColumn<Task, String> priorityColumn =
                new TableColumn<>("优先级");

        priorityColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getPriority().name()
                )
        );
        priorityColumn.setPrefWidth(110);

        TableColumn<Task, String> statusColumn =
                new TableColumn<>("状态");

        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getStatusText(cell.getValue())
                )
        );
        statusColumn.setPrefWidth(120);

        taskTable.getColumns().add(titleColumn);
        taskTable.getColumns().add(dateColumn);
        taskTable.getColumns().add(priorityColumn);
        taskTable.getColumns().add(statusColumn);

        taskTable.setPlaceholder(new Label("这门课程还没有任务"));
    }

    private String getStatusText(Task task) {
        if (task.isCompleted()) {
            return "已完成";
        }

        if (task.isOverdue()) {
            return "已逾期";
        }

        return "未完成";
    }

    private void refreshTasks() {
        String selectedCourse =
                courseList.getSelectionModel().getSelectedItem();

        ArrayList<Task> tasks = new ArrayList<>();

        if ("全部课程".equals(selectedCourse)) {
            tasks = planner.getAllTasks();
        } else {
            Course course = planner.findCourseByCode(selectedCourse);

            if (course != null) {
                tasks = course.getTasks();
            }
        }

        tasks.sort(Comparator.comparing(Task::getDueDate));
        taskTable.getItems().setAll(tasks);
        taskTable.sort();

        int completed = 0;
        int overdue = 0;

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completed++;
            }

            if (task.isOverdue()) {
                overdue++;
            }
        }

        summary.setText(
                "当前列表：共 " + tasks.size() + " 个任务"
                        + "   |   已完成 " + completed
                        + "   |   未完成 " + (tasks.size() - completed)
                        + "   |   已逾期 " + overdue
        );
    }

    private void loadSampleData() {
        Course csc207 = new Course("CSC207", "Software Design");
        Course mat244 = new Course("MAT244", "Mathematics");

        planner.addCourse(csc207);
        planner.addCourse(mat244);

        LocalDate today = LocalDate.now();

        csc207.addTask(new Task(
                "Assignment 1",
                today.plusDays(3),
                Priority.HIGH
        ));

        Task quiz = new Task(
                "Quiz 1",
                today.plusDays(7),
                Priority.MEDIUM
        );
        quiz.markCompleted();
        csc207.addTask(quiz);

        mat244.addTask(new Task(
                "Midterm Review",
                today.plusDays(1),
                Priority.HIGH
        ));

        mat244.addTask(new Task(
                "Reading",
                today.minusDays(1),
                Priority.LOW
        ));
    }

    private void showAddTaskDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("添加任务");
        dialog.setHeaderText("填写任务信息");
        dialog.initOwner(taskTable.getScene().getWindow());

        ButtonType addType = new ButtonType(
                "添加",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                addType,
                ButtonType.CANCEL
        );

        // 课程选择
        ComboBox<String> courseBox = new ComboBox<>();

        for (Course course : planner.getCourses()) {
            courseBox.getItems().add(course.getCourseCode());
        }

        String selectedCourse =
                courseList.getSelectionModel().getSelectedItem();

        if (courseBox.getItems().contains(selectedCourse)) {
            courseBox.setValue(selectedCourse);
        } else if (!courseBox.getItems().isEmpty()) {
            courseBox.getSelectionModel().selectFirst();
        }

        // 任务名称
        TextField titleField = new TextField();
        titleField.setPromptText("例如：Assignment 2");

        // 截止日期，通过日历选择
        DatePicker datePicker = new DatePicker(
                LocalDate.now().plusDays(1)
        );
        datePicker.setEditable(false);

        // 优先级
        ComboBox<Priority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Priority.values());
        priorityBox.setValue(Priority.MEDIUM);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #c62828;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("所属课程"), courseBox);
        form.addRow(1, new Label("任务名称"), titleField);
        form.addRow(2, new Label("截止日期"), datePicker);
        form.addRow(3, new Label("优先级"), priorityBox);
        form.add(errorLabel, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(form);

        // 输入不完整时，提示错误并保持弹窗打开
        Button confirmButton = (Button)
                dialog.getDialogPane().lookupButton(addType);

        confirmButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {
                    if (courseBox.getValue() == null) {
                        errorLabel.setText("请选择课程。");
                        event.consume();
                    } else if (titleField.getText().trim().isEmpty()) {
                        errorLabel.setText("请输入任务名称。");
                        event.consume();
                    } else if (datePicker.getValue() == null) {
                        errorLabel.setText("请选择截止日期。");
                        event.consume();
                    } else if (priorityBox.getValue() == null) {
                        errorLabel.setText("请选择优先级。");
                        event.consume();
                    }
                }
        );

        dialog.showAndWait().ifPresent(result -> {
            if (result == addType) {
                Task task = new Task(
                        titleField.getText().trim(),
                        datePicker.getValue(),
                        priorityBox.getValue()
                );

                planner.addTask(courseBox.getValue(), task);

                // 切换到任务所属课程，方便立即看到新增任务
                courseList.getSelectionModel()
                        .select(courseBox.getValue());

                refreshTasks();
                taskTable.getSelectionModel().select(task);
                taskTable.scrollTo(task);
            }
        });
    }

    private void changeSelectedTaskStatus() {
        Task task = taskTable.getSelectionModel().getSelectedItem();

        if (task == null) {
            return;
        }

        Course course = findCourseForTask(task);

        if (course == null) {
            return;
        }

        if (task.isCompleted()) {
            planner.markTaskIncomplete(course.getCourseCode(), task);
        } else {
            planner.markTaskCompleted(course.getCourseCode(), task);
        }

        refreshTasks();

        // Task 使用普通 Java 字段，需要刷新表格显示
        taskTable.refresh();
        taskTable.getSelectionModel().select(task);
    }

    private void deleteSelectedTask() {
        Task task = taskTable.getSelectionModel().getSelectedItem();

        if (task == null) {
            return;
        }

        Course course = findCourseForTask(task);

        if (course == null) {
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );
        confirmation.initOwner(taskTable.getScene().getWindow());
        confirmation.setTitle("删除任务");
        confirmation.setHeaderText("确定删除这个任务吗？");
        confirmation.setContentText(
                course.getCourseCode() + " · " + task.getTitle()
        );

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                planner.removeTask(course.getCourseCode(), task);
                refreshTasks();
            }
        });
    }

    private Course findCourseForTask(Task task) {
        for (Course course : planner.getCourses()) {
            if (course.getTasks().contains(task)) {
                return course;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}