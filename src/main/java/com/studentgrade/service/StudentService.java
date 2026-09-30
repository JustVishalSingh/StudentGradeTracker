package com.studentgrade.service;

import com.studentgrade.dao.StudentDAO;
import com.studentgrade.exception.ValidationException;
import com.studentgrade.model.Student;
import com.studentgrade.util.ValidationUtil;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public void addStudent(Student student) {
        validate(student);
        studentDAO.addStudent(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }

    public List<Student> searchStudents(String query) {
        return studentDAO.search(query);
    }

    public void updateStudent(Student student) {
        validate(student);
        studentDAO.update(student);
    }

    public void deleteStudent(int id) {
        studentDAO.delete(id);
    }

    private void validate(Student s) {
        if (!ValidationUtil.isNotEmpty(s.getRollNumber()))
            throw new ValidationException("Roll number is required.");
        if (!ValidationUtil.isNotEmpty(s.getName()))
            throw new ValidationException("Name is required.");
        if (!ValidationUtil.isNotEmpty(s.getEmail()))
            throw new ValidationException("Email is required.");
        if (!ValidationUtil.isValidEmail(s.getEmail()))
            throw new ValidationException("Invalid email format.");
        if (!ValidationUtil.isValidPhone(s.getPhone()))
            throw new ValidationException("Phone must be 10 digits.");
        if (!ValidationUtil.isNotEmpty(s.getCourse()))
            throw new ValidationException("Course is required.");
        if (!ValidationUtil.isValidSemester(s.getSemester()))
            throw new ValidationException("Semester must be between 1 and 8.");
    }
}
