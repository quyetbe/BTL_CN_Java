package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối JDBC tới cơ sở dữ liệu MySQL (XAMPP).
 * Sử dụng mô hình Singleton để cấu hình tham số kết nối tập trung,
 * và cung cấp kết nối an toàn cho các DAO.
 */
public class DBConnection {

    private static final String HOST = AppConfig.getDbHost();
    private static final int PORT = AppConfig.getDbPort();
    private static final String DB_NAME = AppConfig.getDbName();
    private static final String USER = AppConfig.getDbUser();
    private static final String PASSWORD = AppConfig.getDbPassword();

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Ho_Chi_Minh"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&characterSetResults=UTF-8"
            + "&connectionCollation=utf8mb4_unicode_ci";

    // Nạp driver MySQL Connector/J
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("LỖI: Không tìm thấy MySQL JDBC Driver! Hãy kiểm tra thư viện mysql-connector-j trong lib/.");
            e.printStackTrace();
        }
    }

    private DBConnection() {
        // Chặn khởi tạo trực tiếp
    }

    /**
     * Mở một kết nối mới tới cơ sở dữ liệu MySQL.
     * Người gọi chịu trách nhiệm đóng kết nối bằng try-with-resources.
     *
     * @return Đối tượng Connection
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Kiểm tra trạng thái kết nối tới CSDL.
     *
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra kết nối CSDL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tiện ích đóng an toàn một kết nối nếu cần.
     */
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
