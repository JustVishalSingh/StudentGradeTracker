package com.studentgrade.controller;

import com.studentgrade.dao.StudentDAO;
import com.studentgrade.dao.TeacherDAO;
import com.studentgrade.exception.AuthenticationException;
import com.studentgrade.model.Role;
import com.studentgrade.model.Student;
import com.studentgrade.model.Teacher;
import com.studentgrade.model.User;
import com.studentgrade.service.AuthenticationService;
import com.studentgrade.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final AuthenticationService authService = new AuthenticationService();
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        try {
            User user = authService.login(username, password);
            SessionManager.setCurrentUser(user);
            errorLabel.setVisible(false);

            String fxmlFile;

            if (user.getRole() == Role.ADMIN) {
                fxmlFile = "/fxml/AdminDashboard.fxml";

            } else if (user.getRole() == Role.TEACHER) {
                Teacher teacher = teacherDAO.findByUserId(user.getId());
                if (teacher == null) throw new AuthenticationException("Teacher profile not found. Contact admin.");
                SessionManager.setCurrentTeacher(teacher);
                fxmlFile = "/fxml/TeacherDashboard.fxml";

            } else { // STUDENT
                Student student = studentDAO.findByUserId(user.getId());
                if (student == null) throw new AuthenticationException("Student profile not linked to this account. Contact admin.");
                SessionManager.setCurrentStudent(student);
                fxmlFile = "/fxml/StudentDashboard.fxml";
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1050, 680));
            stage.setTitle("Student Academic Performance Management System");
            stage.show();

        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Connection failed. Check database settings.");
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }
}
