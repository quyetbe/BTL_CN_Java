package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import service.AuthService;
import util.PasswordUtil;

/**
 * Controller xử lý nghiệp vụ xác thực và phân quyền (Authentication & Authorization).
 */
public class AuthController {

    private final AuthService authService;
    private final TaiKhoanDAO taiKhoanDAO;

    public AuthController() {
        this.authService = AuthService.getInstance();
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public boolean login(String username, String password) {
        return authService.login(username, password) != null;
    }

    public TaiKhoan getCurrentUser() {
        return authService.getCurrentUser();
    }

    public boolean isStudent() {
        TaiKhoan u = getCurrentUser();
        return u != null && "SINH_VIEN".equalsIgnoreCase(u.getVaiTro());
    }

    public boolean isLecturer() {
        TaiKhoan u = getCurrentUser();
        return u != null && ("GIANG_VIEN".equalsIgnoreCase(u.getVaiTro()) || "TRUONG_BO_MON".equalsIgnoreCase(u.getVaiTro()));
    }

    public boolean isDeptHead() {
        TaiKhoan u = getCurrentUser();
        return u != null && ("TRUONG_BO_MON".equalsIgnoreCase(u.getVaiTro()) || "ADMIN".equalsIgnoreCase(u.getVaiTro()) || "BAN_GIAM_HIEU".equalsIgnoreCase(u.getVaiTro()));
    }

    public boolean isAcademicAdminOrRector() {
        TaiKhoan u = getCurrentUser();
        return u != null && ("PHONG_DAO_TAO".equalsIgnoreCase(u.getVaiTro()) || "BAN_GIAM_HIEU".equalsIgnoreCase(u.getVaiTro()) || "ADMIN".equalsIgnoreCase(u.getVaiTro()));
    }

    public void logout() {
        authService.logout();
    }
}
