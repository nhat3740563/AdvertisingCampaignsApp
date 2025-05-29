package com.yourcompany.campaignapp.dao;

import com.yourcompany.campaignapp.model.User;
import com.yourcompany.campaignapp.util.DatabaseConnection;
import com.yourcompany.campaignapp.util.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements GenericDAO<User> {

    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordHasher.hashPassword(user.getPasswordHash()));
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                        user.setPasswordHash(null);
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public User read(int id) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email, role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    public User readByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email, role FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean updatePassword(int userId, String newPlainTextPassword) throws SQLException {