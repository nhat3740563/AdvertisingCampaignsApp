// src/main/java/com/yourcompany/campaignapp/service/UserService.java
package com.yourcompany.campaignapp.service;

import com.yourcompany.campaignapp.dao.UserDAO;
import com.yourcompany.campaignapp.model.User;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO(); // Khởi tạo UserDAO
    }

    // Constructor để inject UserDAO (tốt cho kiểm thử)
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerUser(String username, String plainTextPassword, String email, String role) throws SQLException {
        // Kiểm tra logic nghiệp vụ trước khi tạo người dùng
        if (username == null || username.trim().isEmpty() || plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }
        if (userDAO.readByUsername(username) != null) {
            System.out.println("Username '" + username + "' already exists.");
            return null; // Hoặc ném một ngoại lệ nghiệp vụ cụ thể
        }

        User newUser = new User(username, plainTextPassword, email, role);
        return userDAO.create(newUser);
    }

    public User login(String username, String plainTextPassword) throws SQLException {
        if (username == null || username.trim().isEmpty() || plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }
        return userDAO.authenticateUser(username, plainTextPassword);
    }

    public User getUserById(int userId) throws SQLException {
        return userDAO.read(userId);
    }

    public boolean updateUser(User user) throws SQLException {
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be valid for update.");
        }
        return userDAO.update(user);
    }

    public boolean updatePassword(int userId, String newPlainTextPassword) throws SQLException {
        if (userId <= 0 || newPlainTextPassword == null || newPlainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID and new password must be valid for update.");
        }
        return userDAO.updatePassword(userId, newPlainTextPassword);
    }

    public boolean deleteUser(int userId) throws SQLException {
        return userDAO.delete(userId);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.readAll();
    }

    public static void main(String[] args) {
        UserService userService = new UserService();
        try {
            // --- TEST REGISTER ---
            System.out.println("--- Test Register User ---");
            User registeredUser = userService.registerUser("servicetestuser", "servicepass123", "service.test@example.com", "user");
            if (registeredUser != null) {
                System.out.println("Registered user: " + registeredUser);
            } else {
                System.out.println("User registration failed or user already exists.");
            }

            // --- TEST LOGIN ---
            System.out.println("\n--- Test Login ---");
            User loggedInUser = userService.login("servicetestuser", "servicepass123");
            if (loggedInUser != null) {
                System.out.println("Login successful for: " + loggedInUser.getUsername());
            } else {
                System.out.println("Login failed.");
            }
            User failedLogin = userService.login("servicetestuser", "wrongpass");
            if (failedLogin == null) {
                System.out.println("Login failed (correctly) for wrong password.");
            }


            // --- TEST GET USER BY ID ---
            System.out.println("\n--- Test Get User by ID ---");
            if (registeredUser != null) {
                User fetchedUser = userService.getUserById(registeredUser.getUserId());
                if (fetchedUser != null) {
                    System.out.println("Fetched user by ID: " + fetchedUser);
                }
            }

            // --- TEST UPDATE USER ---
            System.out.println("\n--- Test Update User ---");
            if (registeredUser != null) {
                registeredUser.setEmail("updated.service@example.com");
                registeredUser.setRole("editor");
                boolean updated = userService.updateUser(registeredUser);
                System.out.println("User update successful? " + updated);
                if (updated) {
                    System.out.println("User after service update: " + userService.getUserById(registeredUser.getUserId()));
                }
            }

            // --- TEST UPDATE PASSWORD ---
            System.out.println("\n--- Test Update Password ---");
            if (registeredUser != null) {
                boolean passwordUpdated = userService.updatePassword(registeredUser.getUserId(), "newservicepass");
                System.out.println("Password update successful? " + passwordUpdated);
                if (passwordUpdated) {
                    User authWithNewPass = userService.login("servicetestuser", "newservicepass");
                    if (authWithNewPass != null) {
                        System.out.println("Login with new password successful.");
                    } else {
                        System.out.println("Login with new password failed.");
                    }
                }
            }


            // --- TEST GET ALL USERS ---
            System.out.println("\n--- Test Get All Users ---");
            List<User> allUsers = userService.getAllUsers();
            System.out.println("All users in DB:");
            for (User u : allUsers) {
                System.out.println("- " + u.getUsername() + " (" + u.getRole() + ")");
            }

            // --- TEST DELETE USER ---
            System.out.println("\n--- Test Delete User ---");
            if (registeredUser != null) {
                boolean deleted = userService.deleteUser(registeredUser.getUserId());
                System.out.println("User deletion successful? " + deleted);
                if (deleted) {
                    System.out.println("User after deletion (should be null): " + userService.getUserById(registeredUser.getUserId()));
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Business logic error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error during UserService test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}