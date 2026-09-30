package com.studentgrade.dao;

import com.studentgrade.database.DatabaseConnection;
import com.studentgrade.exception.DatabaseException;
import com.studentgrade.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    private static final String JOIN_SQL =
        "SELECT g.id, g.student_id, g.subject_id, g.marks, g.grade, g.created_at, " +
        "s.name AS student_name, s.roll_number, sub.subject_name, sub.subject_code " +
        "FROM grades g " +
        "JOIN students s ON g.student_id = s.id " +
        "JOIN subjects sub ON g.subject_id = sub.id ";

    /** Insert or update a grade (upsert). Uses ON DUPLICATE KEY UPDATE since (student_id, subject_id) is unique. */
    public void upsert(Grade grade) {
        String sql = "INSERT INTO grades (student_id, subject_id, marks, grade) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE marks = VALUES(marks), grade = VALUES(grade)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grade.getStudentId());
            ps.setInt(2, grade.getSubjectId());
            ps.setDouble(3, grade.getMarks());
            ps.setString(4, grade.getGradeLetter());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save grade.", e);
        }
    }

    public List<Grade> getAll() {
        List<Grade> list = new ArrayList<>();
        String sql = JOIN_SQL + "ORDER BY s.name, sub.subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve grades.", e);
        }
        return list;
    }

    public List<Grade> getByStudentId(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = JOIN_SQL + "WHERE g.student_id = ? ORDER BY sub.subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get grades for student.", e);
        }
        return list;
    }

    public List<Grade> getBySubjectId(int subjectId) {
        List<Grade> list = new ArrayList<>();
        String sql = JOIN_SQL + "WHERE g.subject_id = ? ORDER BY s.name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get grades for subject.", e);
        }
        return list;
    }

    public void delete(int id) {
        String sql = "DELETE FROM grades WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete grade.", e);
        }
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setSubjectId(rs.getInt("subject_id"));
        g.setMarks(rs.getDouble("marks"));
        g.setGradeLetter(rs.getString("grade"));
        g.setCreatedAt(rs.getTimestamp("created_at"));
        g.setStudentName(rs.getString("student_name"));
        g.setRollNumber(rs.getString("roll_number"));
        g.setSubjectName(rs.getString("subject_name"));
        g.setSubjectCode(rs.getString("subject_code"));
        return g;
    }
}
