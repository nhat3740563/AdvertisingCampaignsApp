package com.yourcompany.campaignapp.dao;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    
    public static String hashPassword(String plainTextPassword) {
        
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

   
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    // Phương thức main để kiểm tra (có thể xóa sau khi kiểm tra)
    public static void main(String[] args) {
        String originalPassword = "mysecretpassword123";

        // Băm mật khẩu
        String hashedPassword = PasswordHasher.hashPassword(originalPassword);
        System.out.println("Mật khẩu gốc: " + originalPassword);
        System.out.println("Mật khẩu đã băm: " + hashedPassword);

        // Kiểm tra mật khẩu đúng
        boolean isMatch = PasswordHasher.checkPassword(originalPassword, hashedPassword);
        System.out.println("Mật khẩu khớp? " + isMatch); // Nên là true

        // Kiểm tra mật khẩu sai
        boolean isMismatch = PasswordHasher.checkPassword("wrongpassword", hashedPassword);
        System.out.println("Mật khẩu sai khớp? " + isMismatch); // Nên là false
    }
}