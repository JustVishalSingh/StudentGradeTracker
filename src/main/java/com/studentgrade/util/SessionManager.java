package com.studentgrade.util;

import com.studentgrade.model.Student;
import com.studentgrade.model.Teacher;
import com.studentgrade.model.User;

/**
 * Holds the currently logged-in user's session data.
 * A simple static class — in production you'd use a DI framework.
 */
public class SessionManager {
    private static User currentUser;
    private static Student currentStudent; // Set when a STUDENT logs in
    private static Teacher currentTeacher; // Set when a TEACHER logs in

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }

    public static void setCurrentStudent(Student student) { currentStudent = student; }
    public static Student getCurrentStudent() { return currentStudent; }

    public static void setCurrentTeacher(Teacher teacher) { currentTeacher = teacher; }
    public static Teacher getCurrentTeacher() { return currentTeacher; }

    public static void logout() {
        currentUser = null;
        currentStudent = null;
        currentTeacher = null;
    }
}
