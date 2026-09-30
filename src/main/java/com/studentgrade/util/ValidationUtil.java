package com.studentgrade.util;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true; // phone is optional
        return phone.matches("^[0-9]{10}$");
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= 100;
    }

    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 8;
    }

    public static boolean isValidCredits(int credits) {
        return credits >= 1 && credits <= 6;
    }
}
