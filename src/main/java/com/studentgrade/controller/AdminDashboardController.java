package com.studentgrade.controller;

import com.studentgrade.exception.ValidationException;
import com.studentgrade.model.*;
import com.studentgrade.service.*;
import com.studentgrade.util.CSVExportUtil;
import com.studentgrade.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class AdminDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Button btnOverview, btnStudents, btnTeachers, btnSubjects, btnGrades, btnAnalytics, btnReports;

    private Button activeNavBtn;

    private final StudentService studentService = new StudentService();
    private final TeacherService teacherService = new TeacherService();
    private final SubjectService subjectService = new SubjectService();
    private final GradeService gradeService = new GradeService();
    private final AnalyticsService analyticsService = new AnalyticsService();

    @FXML
    public void initialize() {
        userLabel.setText("Admin: " + SessionManager.getCurrentUser().getUsername());
        showOverview(null);
    }

    // ===== NAVIGATION =====

    @FXML private void showOverview(javafx.event.ActionEvent e)  { setActive(btnOverview);  setCenter(buildOverviewPanel()); }
    @FXML private void showStudents(javafx.event.ActionEvent e)  { setActive(btnStudents);  setCenter(buildStudentsPanel()); }
    @FXML private void showTeachers(javafx.event.ActionEvent e)  { setActive(btnTeachers);  setCenter(buildTeachersPanel()); }
    @FXML private void showSubjects(javafx.event.ActionEvent e)  { setActive(btnSubjects);  setCenter(buildSubjectsPanel()); }
    @FXML private void showGrades(javafx.event.ActionEvent e)    { setActive(btnGrades);    setCenter(buildGradesPanel()); }
    @FXML private void showAnalytics(javafx.event.ActionEvent e) { setActive(btnAnalytics); setCenter(buildAnalyticsPanel()); }
    @FXML private void showReports(javafx.event.ActionEvent e)   { setActive(btnReports);   setCenter(buildReportsPanel()); }

    @FXML
    private void handleLogout(javafx.event.ActionEvent e) {
        SessionManager.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
        } catch (Exception ex) { showError("Logout failed."); }
    }

    private void setCenter(Node node) { contentArea.getChildren().setAll(node); }

    private void setActive(Button btn) {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
        activeNavBtn = btn;
        if (btn != null) btn.getStyleClass().add("nav-btn-active");
    }

    // ===== OVERVIEW PANEL =====

    private ScrollPane buildOverviewPanel() {
        VBox panel = new VBox(25);
        panel.setPadding(new Insets(30));

        Label title = new Label("Dashboard Overview");
        title.getStyleClass().add("section-title");
        Label sub = new Label("Academic summary at a glance");
        sub.setStyle("-fx-text-fill: #6b7280;");

        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER_LEFT);
        try {
            cards.getChildren().addAll(
                statCard("Total Students",  String.valueOf(analyticsService.getTotalStudents()),  "#3b82f6"),
                statCard("Total Teachers",  String.valueOf(analyticsService.getTotalTeachers()),  "#10b981"),
                statCard("Total Subjects",  String.valueOf(analyticsService.getTotalSubjects()),  "#f59e0b"),
                statCard("Avg Performance", String.format("%.1f%%", analyticsService.getOverallAverage()), "#8b5cf6")
            );
        } catch (Exception ex) {
            cards.getChildren().add(new Label("Could not load stats: " + ex.getMessage()));
        }

        panel.getChildren().addAll(title, sub, cards);
        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc;");
        return sp;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        Label num = new Label(value);
        num.getStyleClass().add("stat-number");
        num.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        card.getChildren().addAll(num, lbl);
        return card;
    }

    // ===== STUDENTS PANEL =====

    private ScrollPane buildStudentsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));

        Label title = new Label("Student Management");
        title.getStyleClass().add("section-title");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, roll number or email...");
        searchField.setPrefWidth(320);
        Button searchBtn = new Button("Search"); searchBtn.getStyleClass().add("btn-primary");
        Button clearSearchBtn = new Button("Show All"); clearSearchBtn.getStyleClass().add("btn-secondary");
        HBox searchBar = new HBox(10, searchField, searchBtn, clearSearchBtn);

        // Form fields
        TextField rollF   = field("Roll Number *");
        TextField nameF   = field("Full Name *");
        TextField emailF  = field("Email *");
        TextField phoneF  = field("Phone (10 digits)");
        TextField courseF = field("Course *");
        TextField semF    = field("Semester (1-8) *");

        GridPane form = formGrid();
        form.add(lbl("Roll Number:"), 0, 0); form.add(rollF,   1, 0);
        form.add(lbl("Name:"),        2, 0); form.add(nameF,   3, 0);
        form.add(lbl("Email:"),       0, 1); form.add(emailF,  1, 1);
        form.add(lbl("Phone:"),       2, 1); form.add(phoneF,  3, 1);
        form.add(lbl("Course:"),      0, 2); form.add(courseF, 1, 2);
        form.add(lbl("Semester:"),    2, 2); form.add(semF,    3, 2);

        Button addBtn = new Button("➕ Add");      addBtn.getStyleClass().add("btn-primary");
        Button updBtn = new Button("✏️ Update");   updBtn.getStyleClass().add("btn-success");
        Button delBtn = new Button("🗑️ Delete");  delBtn.getStyleClass().add("btn-danger");
        Button clrBtn = new Button("✖ Clear");     clrBtn.getStyleClass().add("btn-secondary");
        HBox btns = new HBox(10, addBtn, updBtn, delBtn, clrBtn);

        // Table
        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("No students found."));
        addColumn(table, "Roll No.",  "rollNumber");
        addColumn(table, "Name",      "name");
        addColumn(table, "Email",     "email");
        addColumn(table, "Phone",     "phone");
        addColumn(table, "Course",    "course");
        addColumn(table, "Semester",  "semester");
        loadStudents(table, null);

        final Student[] sel = {null};
        table.getSelectionModel().selectedItemProperty().addListener((o, old, s) -> {
            if (s != null) {
                sel[0] = s;
                rollF.setText(s.getRollNumber()); nameF.setText(s.getName());
                emailF.setText(s.getEmail()); phoneF.setText(s.getPhone() != null ? s.getPhone() : "");
                courseF.setText(s.getCourse()); semF.setText(String.valueOf(s.getSemester()));
            }
        });

        addBtn.setOnAction(e -> {
            try {
                Student s = new Student(0, rollF.getText(), nameF.getText(), emailF.getText(),
                    phoneF.getText(), courseF.getText(), Integer.parseInt(semF.getText()), null);
                studentService.addStudent(s);
                alert("Student added successfully!");
                clearF(rollF, nameF, emailF, phoneF, courseF, semF); loadStudents(table, null);
            } catch (ValidationException ex) { showError(ex.getMessage());
            } catch (NumberFormatException ex) { showError("Semester must be a number."); }
        });

        updBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a student to update."); return; }
            try {
                sel[0].setName(nameF.getText()); sel[0].setEmail(emailF.getText());
                sel[0].setPhone(phoneF.getText()); sel[0].setCourse(courseF.getText());
                sel[0].setSemester(Integer.parseInt(semF.getText()));
                studentService.updateStudent(sel[0]);
                alert("Student updated!"); clearF(rollF, nameF, emailF, phoneF, courseF, semF);
                sel[0] = null; loadStudents(table, null);
            } catch (ValidationException ex) { showError(ex.getMessage());
            } catch (NumberFormatException ex) { showError("Semester must be a number."); }
        });

        delBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a student to delete."); return; }
            if (confirm("Delete student '" + sel[0].getName() + "'? All their grades will also be deleted.")) {
                studentService.deleteStudent(sel[0].getId());
                alert("Student deleted."); sel[0] = null;
                clearF(rollF, nameF, emailF, phoneF, courseF, semF); loadStudents(table, null);
            }
        });

        clrBtn.setOnAction(e -> { clearF(rollF, nameF, emailF, phoneF, courseF, semF); sel[0] = null; table.getSelectionModel().clearSelection(); });
        searchBtn.setOnAction(e -> loadStudents(table, searchField.getText().trim()));
        clearSearchBtn.setOnAction(e -> { searchField.clear(); loadStudents(table, null); });
        searchField.setOnAction(e -> loadStudents(table, searchField.getText().trim()));

        panel.getChildren().addAll(title, searchBar, form, btns, table);
        return scrollPane(panel);
    }

    private void loadStudents(TableView<Student> table, String query) {
        try {
            List<Student> list = (query == null || query.isEmpty()) ?
                studentService.getAllStudents() : studentService.searchStudents(query);
            table.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ex) { showError("Could not load students: " + ex.getMessage()); }
    }

    // ===== TEACHERS PANEL =====

    private ScrollPane buildTeachersPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("Teacher Management"); title.getStyleClass().add("section-title");

        TextField nameF = field("Full Name *");
        TextField emailF = field("Email *");
        TextField deptF = field("Department *");

        GridPane form = formGrid();
        form.add(lbl("Name:"),       0, 0); form.add(nameF,  1, 0);
        form.add(lbl("Email:"),      2, 0); form.add(emailF, 3, 0);
        form.add(lbl("Department:"), 0, 1); form.add(deptF,  1, 1);

        Button addBtn = new Button("➕ Add");    addBtn.getStyleClass().add("btn-primary");
        Button updBtn = new Button("✏️ Update"); updBtn.getStyleClass().add("btn-success");
        Button delBtn = new Button("🗑️ Delete"); delBtn.getStyleClass().add("btn-danger");
        Button clrBtn = new Button("✖ Clear");   clrBtn.getStyleClass().add("btn-secondary");
        HBox btns = new HBox(10, addBtn, updBtn, delBtn, clrBtn);

        TableView<Teacher> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("No teachers found."));
        addColumn(table, "Name",       "name");
        addColumn(table, "Email",      "email");
        addColumn(table, "Department", "department");
        loadTeachers(table);

        final Teacher[] sel = {null};
        table.getSelectionModel().selectedItemProperty().addListener((o, old, t) -> {
            if (t != null) { sel[0] = t; nameF.setText(t.getName()); emailF.setText(t.getEmail()); deptF.setText(t.getDepartment()); }
        });

        addBtn.setOnAction(e -> {
            try {
                teacherService.addTeacher(new Teacher(0, nameF.getText(), emailF.getText(), deptF.getText(), null));
                alert("Teacher added!"); clearF(nameF, emailF, deptF); loadTeachers(table);
            } catch (ValidationException ex) { showError(ex.getMessage()); }
        });
        updBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a teacher to update."); return; }
            try {
                sel[0].setName(nameF.getText()); sel[0].setEmail(emailF.getText()); sel[0].setDepartment(deptF.getText());
                teacherService.updateTeacher(sel[0]); alert("Teacher updated!");
                clearF(nameF, emailF, deptF); sel[0] = null; loadTeachers(table);
            } catch (ValidationException ex) { showError(ex.getMessage()); }
        });
        delBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a teacher to delete."); return; }
            if (confirm("Delete teacher '" + sel[0].getName() + "'?")) {
                teacherService.deleteTeacher(sel[0].getId()); alert("Teacher deleted.");
                clearF(nameF, emailF, deptF); sel[0] = null; loadTeachers(table);
            }
        });
        clrBtn.setOnAction(e -> { clearF(nameF, emailF, deptF); sel[0] = null; table.getSelectionModel().clearSelection(); });

        panel.getChildren().addAll(title, form, btns, table);
        return scrollPane(panel);
    }

    private void loadTeachers(TableView<Teacher> t) {
        try { t.setItems(FXCollections.observableArrayList(teacherService.getAllTeachers())); }
        catch (Exception ex) { showError("Could not load teachers."); }
    }

    // ===== SUBJECTS PANEL =====

    private ScrollPane buildSubjectsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("Subject Management"); title.getStyleClass().add("section-title");

        TextField codeF    = field("Subject Code *");
        TextField nameF    = field("Subject Name *");
        TextField creditsF = field("Credits (1-6) *");
        ComboBox<Teacher> teacherCombo = new ComboBox<>();
        teacherCombo.setPromptText("Assign Teacher (optional)");
        teacherCombo.setPrefWidth(220);
        try {
            teacherCombo.setItems(FXCollections.observableArrayList(teacherService.getAllTeachers()));
            teacherCombo.setConverter(teacherConverter());
        } catch (Exception ignored) {}

        GridPane form = formGrid();
        form.add(lbl("Subject Code:"),   0, 0); form.add(codeF,       1, 0);
        form.add(lbl("Subject Name:"),   2, 0); form.add(nameF,       3, 0);
        form.add(lbl("Credits:"),        0, 1); form.add(creditsF,    1, 1);
        form.add(lbl("Assign Teacher:"), 2, 1); form.add(teacherCombo,3, 1);

        Button addBtn = new Button("➕ Add");    addBtn.getStyleClass().add("btn-primary");
        Button updBtn = new Button("✏️ Update"); updBtn.getStyleClass().add("btn-success");
        Button delBtn = new Button("🗑️ Delete"); delBtn.getStyleClass().add("btn-danger");
        Button clrBtn = new Button("✖ Clear");   clrBtn.getStyleClass().add("btn-secondary");
        HBox btns = new HBox(10, addBtn, updBtn, delBtn, clrBtn);

        TableView<Subject> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("No subjects found."));
        addColumn(table, "Code",    "subjectCode");
        addColumn(table, "Subject", "subjectName");
        addColumn(table, "Credits", "credits");
        loadSubjects(table);

        final Subject[] sel = {null};
        table.getSelectionModel().selectedItemProperty().addListener((o, old, s) -> {
            if (s != null) {
                sel[0] = s; codeF.setText(s.getSubjectCode()); nameF.setText(s.getSubjectName()); creditsF.setText(String.valueOf(s.getCredits()));
                if (s.getTeacherId() != null) teacherCombo.getItems().stream().filter(t -> t.getId() == s.getTeacherId()).findFirst().ifPresent(teacherCombo::setValue);
            }
        });

        addBtn.setOnAction(e -> {
            try {
                Integer tid = teacherCombo.getValue() != null ? teacherCombo.getValue().getId() : null;
                subjectService.addSubject(new Subject(0, codeF.getText(), nameF.getText(), Integer.parseInt(creditsF.getText()), tid));
                alert("Subject added!"); clearF(codeF, nameF, creditsF); teacherCombo.setValue(null); loadSubjects(table);
            } catch (ValidationException ex) { showError(ex.getMessage());
            } catch (NumberFormatException ex) { showError("Credits must be a number."); }
        });
        updBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a subject to update."); return; }
            try {
                sel[0].setSubjectName(nameF.getText()); sel[0].setCredits(Integer.parseInt(creditsF.getText()));
                sel[0].setTeacherId(teacherCombo.getValue() != null ? teacherCombo.getValue().getId() : null);
                subjectService.updateSubject(sel[0]); alert("Subject updated!");
                clearF(codeF, nameF, creditsF); sel[0] = null; loadSubjects(table);
            } catch (Exception ex) { showError("Error: " + ex.getMessage()); }
        });
        delBtn.setOnAction(e -> {
            if (sel[0] == null) { showError("Select a subject to delete."); return; }
            if (confirm("Delete subject '" + sel[0].getSubjectName() + "'?")) {
                subjectService.deleteSubject(sel[0].getId()); alert("Subject deleted."); sel[0] = null; loadSubjects(table);
            }
        });
        clrBtn.setOnAction(e -> { clearF(codeF, nameF, creditsF); teacherCombo.setValue(null); sel[0] = null; table.getSelectionModel().clearSelection(); });

        panel.getChildren().addAll(title, form, btns, table);
        return scrollPane(panel);
    }

    private void loadSubjects(TableView<Subject> t) {
        try { t.setItems(FXCollections.observableArrayList(subjectService.getAllSubjects())); }
        catch (Exception ex) { showError("Could not load subjects."); }
    }

    // ===== GRADES PANEL =====

    private ScrollPane buildGradesPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        Label title = new Label("Grade Entry"); title.getStyleClass().add("section-title");

        ComboBox<Student> studentCombo = new ComboBox<>();
        studentCombo.setPromptText("Select Student"); studentCombo.setPrefWidth(260);
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.setPromptText("Select Subject"); subjectCombo.setPrefWidth(260);
        TextField marksField = field("Marks (0 - 100)");
        marksField.setPrefWidth(150);
        Label gradeDisplay = new Label("--");
        gradeDisplay.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");

        try {
            studentCombo.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
            studentCombo.setConverter(studentConverter());
            subjectCombo.setItems(FXCollections.observableArrayList(subjectService.getAllSubjects()));
            subjectCombo.setConverter(subjectConverter());
        } catch (Exception ignored) {}

        marksField.textProperty().addListener((obs, old, val) -> {
            try { gradeDisplay.setText(gradeService.calculateGrade(Double.parseDouble(val))); }
            catch (Exception ex) { gradeDisplay.setText("--"); }
        });

        Button saveBtn = new Button("💾 Save Grade"); saveBtn.getStyleClass().add("btn-primary");
        Button clrBtn  = new Button("✖ Clear");       clrBtn.getStyleClass().add("btn-secondary");

        GridPane form = formGrid();
        form.add(lbl("Student:"), 0, 0); form.add(studentCombo, 1, 0);
        form.add(lbl("Subject:"), 0, 1); form.add(subjectCombo, 1, 1);
        form.add(lbl("Marks:"),   0, 2); form.add(marksField,   1, 2);
        form.add(lbl("Grade:"),   0, 3); form.add(gradeDisplay, 1, 3);
        form.add(new HBox(10, saveBtn, clrBtn), 1, 4);

        // Grades table
        TableView<Grade> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("No grades entered yet."));

        TableColumn<Grade, String> c1 = new TableColumn<>("Roll No.");
        c1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber()));
        TableColumn<Grade, String> c2 = new TableColumn<>("Student");
        c2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        TableColumn<Grade, String> c3 = new TableColumn<>("Subject");
        c3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));
        TableColumn<Grade, String> c4 = new TableColumn<>("Marks");
        c4.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getMarks())));
        TableColumn<Grade, String> c5 = new TableColumn<>("Grade");
        c5.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGradeLetter()));
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        loadGrades(table);

        saveBtn.setOnAction(e -> {
            if (studentCombo.getValue() == null || subjectCombo.getValue() == null) {
                showError("Please select both a student and a subject."); return;
            }
            try {
                double marks = Double.parseDouble(marksField.getText());
                gradeService.saveGrade(new Grade(0, studentCombo.getValue().getId(), subjectCombo.getValue().getId(), marks, ""));
                alert("Grade saved!"); studentCombo.setValue(null); subjectCombo.setValue(null);
                marksField.clear(); gradeDisplay.setText("--"); loadGrades(table);
            } catch (ValidationException ex) { showError(ex.getMessage());
            } catch (NumberFormatException ex) { showError("Please enter a valid number for marks."); }
        });
        clrBtn.setOnAction(e -> { studentCombo.setValue(null); subjectCombo.setValue(null); marksField.clear(); gradeDisplay.setText("--"); });

        panel.getChildren().addAll(title, form, new Label("All Grades:"), table);
        return scrollPane(panel);
    }

    private void loadGrades(TableView<Grade> t) {
        try { t.setItems(FXCollections.observableArrayList(gradeService.getAllGrades())); }
        catch (Exception ex) { showError("Could not load grades."); }
    }

    // ===== ANALYTICS PANEL =====

    private ScrollPane buildAnalyticsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        Label title = new Label("Academic Analytics"); title.getStyleClass().add("section-title");

        try {
            HBox statsRow = new HBox(20);
            statsRow.getChildren().addAll(
                statCard("Average Marks",    String.format("%.1f", analyticsService.getOverallAverage()), "#3b82f6"),
                statCard("Highest Mark",     String.format("%.1f", analyticsService.getHighestMark()),    "#10b981"),
                statCard("Lowest Mark",      String.format("%.1f", analyticsService.getLowestMark()),     "#ef4444"),
                statCard("Pass Rate",        String.format("%.1f%%", analyticsService.getPassPercentage()), "#f59e0b")
            );

            Label distTitle = new Label("Grade Distribution");
            distTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            Map<String, Long> dist = analyticsService.getGradeDistribution();
            TableView<Map.Entry<String, Long>> distTable = new TableView<>();
            distTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            distTable.setPrefHeight(200);
            TableColumn<Map.Entry<String, Long>, String> gc = new TableColumn<>("Grade");
            gc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKey()));
            TableColumn<Map.Entry<String, Long>, String> cc = new TableColumn<>("Count");
            cc.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getValue())));
            distTable.getColumns().addAll(gc, cc);
            distTable.setItems(FXCollections.observableArrayList(new ArrayList<>(dist.entrySet())));

            Label avgTitle = new Label("Subject-wise Average Marks");
            avgTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            Map<String, Double> avgs = analyticsService.getSubjectAverages();
            TableView<Map.Entry<String, Double>> avgTable = new TableView<>();
            avgTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            avgTable.setPrefHeight(200);
            TableColumn<Map.Entry<String, Double>, String> sc = new TableColumn<>("Subject");
            sc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKey()));
            TableColumn<Map.Entry<String, Double>, String> ac = new TableColumn<>("Average Marks");
            ac.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue())));
            avgTable.getColumns().addAll(sc, ac);
            avgTable.setItems(FXCollections.observableArrayList(new ArrayList<>(avgs.entrySet())));

            panel.getChildren().addAll(title, statsRow, distTitle, distTable, avgTitle, avgTable);
        } catch (Exception ex) {
            panel.getChildren().addAll(title, new Label("Could not load analytics: " + ex.getMessage()));
        }
        return scrollPane(panel);
    }

    // ===== REPORTS PANEL =====

    private ScrollPane buildReportsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        Label title = new Label("Reports & Export"); title.getStyleClass().add("section-title");
        Label sub = new Label("Export data to CSV files saved in the 'reports/' folder.");
        sub.setStyle("-fx-text-fill: #6b7280;");

        Label status = new Label("");
        Button expStudents = new Button("📋 Export All Students to CSV"); expStudents.getStyleClass().add("btn-primary");
        Button expGrades   = new Button("📊 Export All Grades to CSV");   expGrades.getStyleClass().add("btn-primary");

        expStudents.setOnAction(e -> {
            try {
                new java.io.File("reports").mkdirs();
                CSVExportUtil.exportStudents(studentService.getAllStudents(), "reports/students_report.csv");
                status.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                status.setText("✅ Exported to reports/students_report.csv");
            } catch (Exception ex) { status.setStyle("-fx-text-fill: #ef4444;"); status.setText("❌ Error: " + ex.getMessage()); }
        });
        expGrades.setOnAction(e -> {
            try {
                new java.io.File("reports").mkdirs();
                CSVExportUtil.exportGrades(gradeService.getAllGrades(), "reports/grades_report.csv");
                status.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                status.setText("✅ Exported to reports/grades_report.csv");
            } catch (Exception ex) { status.setStyle("-fx-text-fill: #ef4444;"); status.setText("❌ Error: " + ex.getMessage()); }
        });

        panel.getChildren().addAll(title, sub, expStudents, expGrades, status);
        return scrollPane(panel);
    }

    // ===== HELPERS =====

    private <T> void addColumn(TableView<T> table, String title, String property) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        table.getColumns().add(col);
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(18));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06),6,0,0,3);");
        return grid;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(200);
        return tf;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        return l;
    }

    private ScrollPane scrollPane(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc;");
        return sp;
    }

    private void clearF(TextField... fields) { for (TextField f : fields) f.clear(); }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private javafx.util.StringConverter<Teacher> teacherConverter() {
        return new javafx.util.StringConverter<>() {
            public String toString(Teacher t)   { return t != null ? t.getName() + " (" + t.getDepartment() + ")" : ""; }
            public Teacher fromString(String s) { return null; }
        };
    }

    private javafx.util.StringConverter<Student> studentConverter() {
        return new javafx.util.StringConverter<>() {
            public String toString(Student s)   { return s != null ? s.getRollNumber() + " - " + s.getName() : ""; }
            public Student fromString(String s) { return null; }
        };
    }

    private javafx.util.StringConverter<Subject> subjectConverter() {
        return new javafx.util.StringConverter<>() {
            public String toString(Subject s)   { return s != null ? s.getSubjectCode() + " - " + s.getSubjectName() : ""; }
            public Subject fromString(String s) { return null; }
        };
    }
}
