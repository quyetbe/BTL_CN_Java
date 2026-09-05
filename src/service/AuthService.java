package service;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

/**
 * Dịch vụ xác thực và lưu trữ phiên làm việc (Session) của người dùng hiện tại.
 */
public class AuthService {

    private static AuthService instance;
    private TaiKhoan currentUser;
    private final TaiKhoanDAO taiKhoanDAO;

    private AuthService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Thực hiện đăng nhập.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu thô
     * @return TaiKhoan nếu thành công, null nếu thất bại
     */
    public TaiKhoan login(String username, String password) {
        TaiKhoan tk = taiKhoanDAO.login(username, password);
        if (tk != null) {
            if (!tk.isTrangThai()) {
                // Tài khoản bị khóa
                return null;
            }
            this.currentUser = tk;
        }
        return tk;
    }

    public void logout() {
        this.currentUser = null;
    }

    public TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(TaiKhoan user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getHoTen() : "Khách";
    }

    public String getCurrentUserRole() {
        if (currentUser == null) return "";
        return currentUser.isAdmin() ? "Quản trị viên" : "Nhân viên đào tạo";
    }
}
