package com.yourcompany.campaignapp.model;

public class User {
    private int userId;
    private String username;
    private String passwordHash; // Sẽ chứa mật khẩu băm, nhưng khi tạo/cập nhật là plain text
    private String email;
    private String role; // Ví dụ: "admin", "user", "editor"

    // Constructor mặc định (cần thiết cho một số framework hoặc khi đọc từ DB)
    public User() {
    }

    // Constructor đầy đủ (thường dùng khi tạo User mới)
    public User(String username, String passwordHash, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
    }

    // --- Getters and Setters ---

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Khi set passwordHash, chúng ta chấp nhận plain text password
    // Lớp DAO sẽ băm nó trước khi lưu vào DB
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
        // KHÔNG BAO GỒM passwordHash trong toString để bảo mật
    }
}