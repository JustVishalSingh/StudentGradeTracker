package com.studentgrade.dao;

import com.studentgrade.database.DatabaseConnection;
import com.studentgrade.exception.DatabaseException;
import com.studentgrade.model.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public void add(Teacher teacher) {
        String sql = "INSERT INTO teachers (name, email, department, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getName());
            ps.setString(2, teacher.getEmail());
            ps.setString(3, teacher.getDepartment());
            if (teacher.getUserId() != null) ps.setInt(4, teacher.getUserId());
            else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatabaseException("A teacher with this email already exists.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add teacher: " + e.getMessage(), e);
        }
    }

    public List<Teacher> getAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve teachers.", e);
        }
        return list;
    }

    public void update(Teacher teacher) {
        String sql = "UPDATE teachers SET name=?, email=?, department=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getName());
            ps.setString(2, teacher.getEmail());
            ps.setString(3, teacher.getDepartment());
            ps.setInt(4, teacher.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update teacher.", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM teachers WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete teacher.", e);
        }
    }

    public Teacher findByUserId(int userId) {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find teacher.", e);
        }
        return null;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM teachers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count teachers.", e);
        }
        return 0;
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setEmail(rs.getString("email"));
        t.setDepartment(rs.getString("department"));
        t.setUserId((Integer) rs.getObject("user_id"));
        return t;
    }
}
