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
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public boolean isBanGiamHieu() {
        return currentUser != null && "BAN_GIAM_HIEU".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public boolean isTruongKhoa() {
        return currentUser != null && "TRUONG_KHOA".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public boolean isPhongDaoTao() {
        return currentUser != null && "PHONG_DAO_TAO".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public boolean isGiangVien() {
        return currentUser != null && "GIANG_VIEN".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public boolean isSinhVien() {
        return currentUser != null && "SINH_VIEN".equalsIgnoreCase(currentUser.getVaiTro());
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getHoTen() : "Khách";
    }

    public String getCurrentUserRole() {
        if (currentUser == null) return "";
        return getRoleDisplayName();
    }

    public String getRoleDisplayName() {
        if (currentUser == null) return "Khách";
        String role = currentUser.getVaiTro();
        if (role == null) return "Người dùng";
        switch (role.toUpperCase()) {
            case "ADMIN": return "Quản trị viên";
            case "BAN_GIAM_HIEU": return "Ban Giám Hiệu";
            case "TRUONG_KHOA": return "Trưởng Khoa CNTT";
            case "PHONG_DAO_TAO": return "Phòng Đào Tạo";
            case "GIANG_VIEN": return "Giảng Viên";
            case "SINH_VIEN": return "Sinh Viên";
            default: return role;
        }
    }

    public String getRoleBadgeText() {
        if (currentUser == null) return "KHÁCH";
        String role = currentUser.getVaiTro();
        if (role == null) return "USER";
        switch (role.toUpperCase()) {
            case "ADMIN": return "ADMIN";
            case "BAN_GIAM_HIEU": return "BAN GIÁM HIỆU";
            case "TRUONG_KHOA": return "TRƯỞNG KHOA";
            case "PHONG_DAO_TAO": return "PHÒNG ĐÀO TẠO";
            case "GIANG_VIEN": return "GIẢNG VIÊN";
            case "SINH_VIEN": return "SINH VIÊN";
            default: return role.toUpperCase();
        }
    }

    // ==========================================
    // PHÂN QUYỀN TRUY CẬP TỪNG CHỨC NĂNG (RBAC)
    // ==========================================

    public boolean canAccessDashboard() {
        return isLoggedIn();
    }

    public boolean canAccessTimetableGrid() {
        return isLoggedIn();
    }

    public boolean canAccessSchedule() {
        return isAdmin() || isPhongDaoTao();
    }

    public boolean canProposeSchedule() {
        return isAdmin() || isPhongDaoTao() || isGiangVien();
    }

    public boolean canApproveSchedule() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao();
    }

    public boolean canAccessCurriculum() {
        return isLoggedIn();
    }

    public boolean canAccessRooms() {
        return isAdmin() || isBanGiamHieu() || isPhongDaoTao();
    }

    public boolean canAccessTeachers() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao();
    }

    public boolean canAccessStudents() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao();
    }

    public boolean canAccessSubjects() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao();
    }

    public boolean canAccessClasses() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao();
    }

    public boolean canAccessStatistics() {
        return isAdmin() || isBanGiamHieu() || isTruongKhoa() || isPhongDaoTao() || isGiangVien();
    }

    public boolean canAccessAuditLog() {
        return isAdmin() || isBanGiamHieu();
    }

    public boolean canManageUsers() {
        return isAdmin();
    }
}
