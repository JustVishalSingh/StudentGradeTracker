package com.studentgrade.controller;

import com.studentgrade.model.Grade;
import com.studentgrade.model.Student;
import com.studentgrade.model.Subject;
import com.studentgrade.model.Teacher;
import com.studentgrade.service.GradeService;
import com.studentgrade.service.StudentService;
import com.studentgrade.service.SubjectService;
import com.studentgrade.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class TeacherDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Button btnSubjects, btnGrades, btnAnalytics;

    private Button activeBtn;
    private Teacher teacher;
    private final GradeService gradeService = new GradeService();
    private final SubjectService subjectService = new SubjectService();
    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        teacher = SessionManager.getCurrentTeacher();
        userLabel.setText("Teacher: " + (teacher != null ? teacher.getName() : "Unknown"));
        showSubjects(null);
    }

    @FXML private void showSubjects(javafx.event.ActionEvent e)    { setActive(btnSubjects);  setCenter(buildSubjectsPanel()); }
    @FXML private void showGrades(javafx.event.ActionEvent e)      { setActive(btnGrades);    setCenter(buildGradesPanel()); }
    @FXML private void showClassSummary(javafx.event.ActionEvent e){ setActive(btnAnalytics); setCenter(buildSummaryPanel()); }

    @FXML
    private void handleLogout(javafx.event.ActionEvent e) {
        SessionManager.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void setCenter(javafx.scene.Node node) { contentArea.getChildren().setAll(node); }
    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("nav-btn-active");
        activeBtn = btn; if (btn != null) btn.getStyleClass().add("nav-btn-active");
    }

    // ===== MY SUBJECTS PANEL =====
    private ScrollPane buildSubjectsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("My Assigned Subjects"); title.getStyleClass().add("section-title");

        TableView<Subject> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No subjects assigned to you yet."));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Subject, String> c1 = new TableColumn<>("Code"); c1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subjectCode"));
        TableColumn<Subject, String> c2 = new TableColumn<>("Subject Name"); c2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subjectName"));
        TableColumn<Subject, String> c3 = new TableColumn<>("Credits"); c3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("credits"));
        table.getColumns().addAll(c1, c2, c3);

        if (teacher != null) {
            try { table.setItems(FXCollections.observableArrayList(subjectService.getSubjectsByTeacher(teacher.getId()))); }
            catch (Exception ex) { showError("Could not load subjects."); }
        }

        panel.getChildren().addAll(title, table);
        return scrollPane(panel);
    }

    // ===== GRADE ENTRY PANEL =====
    private ScrollPane buildGradesPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("Enter / Update Grades"); title.getStyleClass().add("section-title");

        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.setPromptText("Select Subject"); subjectCombo.setPrefWidth(260);
        ComboBox<Student> studentCombo = new ComboBox<>();
        studentCombo.setPromptText("Select Student"); studentCombo.setPrefWidth(260);
        TextField marksField = new TextField(); marksField.setPromptText("Marks (0-100)"); marksField.setPrefWidth(150);
        Label gradeDisplay = new Label("--");
        gradeDisplay.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");

        // Load teacher's subjects only
        if (teacher != null) {
            try {
                subjectCombo.setItems(FXCollections.observableArrayList(subjectService.getSubjectsByTeacher(teacher.getId())));
                subjectCombo.setConverter(new javafx.util.StringConverter<>() {
                    public String toString(Subject s) { return s != null ? s.getSubjectCode() + " - " + s.getSubjectName() : ""; }
                    public Subject fromString(String s) { return null; }
                });
                studentCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
                studentCombo.setConverter(new javafx.util.StringConverter<>() {
                    public String toString(Student s) { return s != null ? s.getRollNumber() + " - " + s.getName() : ""; }
                    public Student fromString(String s) { return null; }
                });
            } catch (Exception ignored) {}
        }

        marksField.textProperty().addListener((obs, old, val) -> {
            try { gradeDisplay.setText(gradeService.calculateGrade(Double.parseDouble(val))); }
            catch (Exception ex) { gradeDisplay.setText("--"); }
        });

        Button saveBtn = new Button("💾 Save Grade"); saveBtn.getStyleClass().add("btn-primary");
        Button clrBtn  = new Button("✖ Clear");       clrBtn.getStyleClass().add("btn-secondary");

        GridPane form = new GridPane(); form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: white; -fx-background-radius:8;");
        Label sl = new Label("Subject:"); sl.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        Label stl = new Label("Student:"); stl.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        Label ml = new Label("Marks:"); ml.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        Label gl = new Label("Grade:"); gl.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        form.add(sl, 0, 0); form.add(subjectCombo, 1, 0);
        form.add(stl, 0, 1); form.add(studentCombo, 1, 1);
        form.add(ml, 0, 2); form.add(marksField, 1, 2);
        form.add(gl, 0, 3); form.add(gradeDisplay, 1, 3);
        form.add(new HBox(10, saveBtn, clrBtn), 1, 4);

        // Grades table for selected subject
        TableView<Grade> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Select a subject to view grades."));
        VBox.setVgrow(table, Priority.ALWAYS);
        TableColumn<Grade, String> c1 = new TableColumn<>("Roll No."); c1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber()));
        TableColumn<Grade, String> c2 = new TableColumn<>("Student");  c2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        TableColumn<Grade, String> c3 = new TableColumn<>("Marks");    c3.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getMarks())));
        TableColumn<Grade, String> c4 = new TableColumn<>("Grade");    c4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGradeLetter()));
        table.getColumns().add(c1);
        table.getColumns().add(c2);
        table.getColumns().add(c3);
        table.getColumns().add(c4);

        subjectCombo.setOnAction(e -> {
            if (subjectCombo.getValue() != null) {
                try { table.setItems(FXCollections.observableArrayList(gradeService.getGradesForSubject(subjectCombo.getValue().getId()))); }
                catch (Exception ex) { showError("Could not load grades."); }
            }
        });

        saveBtn.setOnAction(e -> {
            if (subjectCombo.getValue() == null || studentCombo.getValue() == null) { showError("Please select both a student and a subject."); return; }
            try {
                gradeService.saveGrade(new Grade(0, studentCombo.getValue().getId(), subjectCombo.getValue().getId(), Double.parseDouble(marksField.getText()), ""));
                alert("Grade saved!");
                subjectCombo.getOnAction().handle(null); // refresh table
                studentCombo.setValue(null); marksField.clear(); gradeDisplay.setText("--");
            } catch (Exception ex) { showError("Error: " + ex.getMessage()); }
        });
        clrBtn.setOnAction(e -> { studentCombo.setValue(null); marksField.clear(); gradeDisplay.setText("--"); });

        panel.getChildren().addAll(title, form, new Label("Grades for Selected Subject:"), table);
        return scrollPane(panel);
    }

    // ===== CLASS SUMMARY PANEL =====
    private ScrollPane buildSummaryPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        Label title = new Label("Class Summary by Subject"); title.getStyleClass().add("section-title");

        if (teacher == null) { panel.getChildren().addAll(title, new Label("Teacher profile not found.")); return scrollPane(panel); }

        try {
            List<Subject> subjects = subjectService.getSubjectsByTeacher(teacher.getId());
            for (Subject subject : subjects) {
                List<Grade> grades = gradeService.getGradesForSubject(subject.getId());
                if (grades.isEmpty()) continue;
                List<Double> marks = new ArrayList<>();
                for (Grade g : grades) marks.add(g.getMarks());

                VBox card = new VBox(8);
                card.setPadding(new Insets(15));
                card.setStyle("-fx-background-color: white; -fx-background-radius:8; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.06),6,0,0,3);");

                Label subTitle = new Label(subject.getSubjectCode() + " - " + subject.getSubjectName());
                subTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

                long passed = marks.stream().filter(m -> m >= 40).count();
                double passPct = (passed * 100.0) / marks.size();

                Label stats = new Label(String.format(
                    "Students: %d  |  Average: %.1f  |  Highest: %.1f  |  Lowest: %.1f  |  Pass: %.1f%%",
                    marks.size(), gradeService.calculateAverage(marks),
                    gradeService.calculateHighest(marks), gradeService.calculateLowest(marks), passPct));
                stats.setStyle("-fx-text-fill: #4b5563;");

                card.getChildren().addAll(subTitle, stats);
                panel.getChildren().add(card);
            }
            if (subjects.isEmpty()) panel.getChildren().add(new Label("No subjects assigned."));
        } catch (Exception ex) {
            panel.getChildren().add(new Label("Error: " + ex.getMessage()));
        }

        panel.getChildren().add(0, title);
        return scrollPane(panel);
    }

    private ScrollPane scrollPane(VBox content) {
        ScrollPane sp = new ScrollPane(content); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc;"); return sp;
    }
    private void alert(String msg)      { Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
    private void showError(String msg)  { Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
}
