package com.myproject.app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thông tin cấu hình cơ sở dữ liệu
    // Bạn cần thay đổi các giá trị này cho phù hợp với cài đặt MySQL của mình
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/campaign_app_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "mysql.infoschema"; // Thay thế bằng username MySQL của bạn
    private static final String PASSWORD = "123456"; // Thay thế bằng password MySQL của bạn

    private static Connection connection = null;

    // Phương thức để lấy kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Đăng ký JDBC driver (có thể bỏ qua nếu bạn dùng JDBC 4.0 trở lên)
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Thiết lập kết nối
                connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
            } catch (ClassNotFoundException e) {
                System.err.println("Không tìm thấy JDBC Driver: " + e.getMessage());
                throw new SQLException("Không tìm thấy JDBC Driver.", e);
            } catch (SQLException e) {
                System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
                throw e; // Ném lại ngoại lệ để lớp gọi xử lý
            }
        }
        return connection;
    }

    // Phương thức để đóng kết nối cơ sở dữ liệu
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối cơ sở dữ liệu.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối cơ sở dữ liệu: " + e.getMessage());
            }
        }
    }

    // Phương thức main để kiểm tra kết nối (có thể xóa sau khi kiểm tra)
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Nếu kết nối thành công, bạn có thể thực hiện một truy vấn đơn giản để kiểm tra
            // Ví dụ: conn.createStatement().execute("SELECT 1");
        } catch (SQLException e) {
            System.err.println("Kiểm tra kết nối thất bại: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
Lưu ý quan trọng:

Bạn cần thay thế your_mysql_username và your_mysql_password bằng tên người dùng và mật khẩu MySQL thực tế của bạn.
Đảm bảo bạn đã thêm MySQL JDBC Driver vào project của mình (thường là trong file pom.xml nếu bạn dùng Maven, hoặc thêm file JAR vào project).
1.2. Lớp PasswordHasher.java
Lớp này sẽ chứa logic để băm mật khẩu người dùng, sử dụng thuật toán như BCrypt để đảm bảo an toàn.

src/main/java/com/yourcompany/campaignapp/util/PasswordHasher.java
Java

package com.yourcompany.campaignapp.util;

import org.mindrot.jbcrypt.BCrypt;

import com.yourcompany.campaignapp.dao.PasswordHasher;

public class PasswordHasher {

    // Phương thức để băm mật khẩu
    public static String hashPassword(String plainTextPassword) {
        // BCrypt tự động sinh ra salt (muối) và kết hợp với mật khẩu để băm
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Phương thức để kiểm tra mật khẩu đã nhập với mật khẩu đã băm
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        // So sánh mật khẩu nhập vào với mật khẩu đã băm
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
