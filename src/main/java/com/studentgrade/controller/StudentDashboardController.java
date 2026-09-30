package com.studentgrade.controller;

import com.studentgrade.model.Grade;
import com.studentgrade.model.Student;
import com.studentgrade.model.Subject;
import com.studentgrade.service.GradeService;
import com.studentgrade.service.SubjectService;
import com.studentgrade.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class StudentDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Button btnProfile, btnGrades, btnAnalytics;

    private Button activeBtn;
    private Student student;
    private final GradeService gradeService = new GradeService();
    private final SubjectService subjectService = new SubjectService();

    @FXML
    public void initialize() {
        student = SessionManager.getCurrentStudent();
        userLabel.setText("Student: " + (student != null ? student.getName() : "Unknown"));
        showProfile(null);
    }

    @FXML private void showProfile(javafx.event.ActionEvent e)    { setActive(btnProfile);   setCenter(buildProfilePanel()); }
    @FXML private void showGrades(javafx.event.ActionEvent e)     { setActive(btnGrades);    setCenter(buildGradesPanel()); }
    @FXML private void showAnalytics(javafx.event.ActionEvent e)  { setActive(btnAnalytics); setCenter(buildAnalyticsPanel()); }

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
        activeBtn = btn;
        if (btn != null) btn.getStyleClass().add("nav-btn-active");
    }

    // ===== PROFILE PANEL =====
    private ScrollPane buildProfilePanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        if (student == null) {
            panel.getChildren().add(new Label("Student profile not found. Contact admin."));
            return scrollPane(panel);
        }

        Label title = new Label("My Profile"); title.getStyleClass().add("section-title");

        VBox card = new VBox(12);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.08),10,0,0,4);");
        card.setMaxWidth(500);

        card.getChildren().addAll(
            profileRow("Roll Number", student.getRollNumber()),
            profileRow("Full Name",   student.getName()),
            profileRow("Email",       student.getEmail()),
            profileRow("Phone",       student.getPhone() != null ? student.getPhone() : "N/A"),
            profileRow("Course",      student.getCourse()),
            profileRow("Semester",    String.valueOf(student.getSemester()))
        );

        panel.getChildren().addAll(title, card);
        return scrollPane(panel);
    }

    private HBox profileRow(String label, String value) {
        Label lbl = new Label(label + ":"); lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-min-width: 120;");
        Label val = new Label(value); val.setStyle("-fx-text-fill: #1e293b;");
        return new HBox(10, lbl, val);
    }

    // ===== GRADES PANEL =====
    private ScrollPane buildGradesPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("My Grades"); title.getStyleClass().add("section-title");

        if (student == null) { panel.getChildren().addAll(title, new Label("Profile not found.")); return scrollPane(panel); }

        TableView<Grade> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No grades recorded yet."));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Grade, String> c1 = new TableColumn<>("Subject Code");
        c1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectCode()));
        TableColumn<Grade, String> c2 = new TableColumn<>("Subject Name");
        c2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));
        TableColumn<Grade, String> c3 = new TableColumn<>("Marks");
        c3.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getMarks())));
        TableColumn<Grade, String> c4 = new TableColumn<>("Grade");
        c4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGradeLetter()));
        TableColumn<Grade, String> c5 = new TableColumn<>("Status");
        c5.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMarks() >= 40 ? "✅ Pass" : "❌ Fail"));
        table.getColumns().addAll(c1, c2, c3, c4, c5);

        try {
            List<Grade> grades = gradeService.getGradesForStudent(student.getId());
            table.setItems(FXCollections.observableArrayList(grades));
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }

        panel.getChildren().addAll(title, table);
        return scrollPane(panel);
    }

    // ===== ANALYTICS PANEL =====
    private ScrollPane buildAnalyticsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        Label title = new Label("My Academic Performance"); title.getStyleClass().add("section-title");

        if (student == null) { panel.getChildren().addAll(title, new Label("Profile not found.")); return scrollPane(panel); }

        try {
            List<Grade> grades = gradeService.getGradesForStudent(student.getId());
            List<Subject> subjects = subjectService.getAllSubjects();
            List<Double> marksList = grades.stream().map(Grade::getMarks).collect(Collectors.toList());

            double avg  = gradeService.calculateAverage(marksList);
            double high = gradeService.calculateHighest(marksList);
            double low  = gradeService.calculateLowest(marksList);
            double pct  = gradeService.calculatePercentage(marksList);
            double sgpa = gradeService.calculateSGPA(grades, subjects);
            boolean pass = gradeService.isPassOverall(marksList);
            long passed  = grades.stream().filter(g -> g.getMarks() >= 40).count();
            long failed  = grades.size() - passed;

            HBox statsRow = new HBox(20);
            statsRow.setAlignment(Pos.CENTER_LEFT);
            statsRow.getChildren().addAll(
                statCard("Average",     String.format("%.1f", avg),  "#3b82f6"),
                statCard("Highest",     String.format("%.1f", high), "#10b981"),
                statCard("Lowest",      String.format("%.1f", low),  "#ef4444"),
                statCard("Percentage",  String.format("%.1f%%", pct),"#f59e0b"),
                statCard("SGPA",        String.format("%.2f", sgpa), "#8b5cf6")
            );

            Label statusLabel = new Label(pass ? "✅ OVERALL STATUS: PASS" : "❌ OVERALL STATUS: FAIL");
            statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (pass ? "#10b981" : "#ef4444") + ";");

            Label passLbl = new Label("Passed Subjects: " + passed + "    |    Failed Subjects: " + failed);
            passLbl.setStyle("-fx-text-fill: #374151;");

            panel.getChildren().addAll(title, statsRow, statusLabel, passLbl);
        } catch (Exception ex) {
            panel.getChildren().addAll(title, new Label("Could not load performance data: " + ex.getMessage()));
        }
        return scrollPane(panel);
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6); card.getStyleClass().add("stat-card");
        Label num = new Label(value); num.getStyleClass().add("stat-number"); num.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label); lbl.getStyleClass().add("stat-label");
        card.getChildren().addAll(num, lbl); return card;
    }

    private ScrollPane scrollPane(VBox content) {
        ScrollPane sp = new ScrollPane(content); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc;"); return sp;
    }
}
