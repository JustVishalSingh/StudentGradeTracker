package com.studentgrade.dao;

import com.studentgrade.database.DatabaseConnection;
import com.studentgrade.exception.DatabaseException;
import com.studentgrade.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Student student) {
        String sql = "INSERT INTO students (roll_number, name, email, phone, course, semester, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNumber());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getCourse());
            ps.setInt(6, student.getSemester());
            if (student.getUserId() != null) ps.setInt(7, student.getUserId());
            else ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatabaseException("A student with this roll number or email already exists.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add student: " + e.getMessage(), e);
        }
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve students.", e);
        }
        return list;
    }

    public List<Student> search(String query) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR roll_number LIKE ? OR email LIKE ? ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Search failed.", e);
        }
        return list;
    }

    public void update(Student student) {
        String sql = "UPDATE students SET name=?, email=?, phone=?, course=?, semester=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getCourse());
            ps.setInt(5, student.getSemester());
            ps.setInt(6, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update student.", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete student.", e);
        }
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by user ID.", e);
        }
        return null;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count students.", e);
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setUserId((Integer) rs.getObject("user_id"));
        return s;
    }
}
