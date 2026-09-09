package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

import java.util.List;

/**
 * Controller quản lý tài khoản người dùng và sinh viên/giảng viên.
 */
public class UserController {

    private final TaiKhoanDAO taiKhoanDAO;

    public UserController() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public List<TaiKhoan> getAllUsers() {
        return taiKhoanDAO.getAll();
    }

    public TaiKhoan getById(int id) {
        return taiKhoanDAO.getById(id);
    }

    public boolean createUser(TaiKhoan tk) {
        return taiKhoanDAO.insert(tk);
    }

    public boolean updateUser(TaiKhoan tk) {
        return taiKhoanDAO.update(tk);
    }

    public boolean deleteUser(int id) {
        return taiKhoanDAO.delete(id);
    }

    public boolean toggleStatus(int id, boolean currentStatus) {
        return taiKhoanDAO.setStatus(id, !currentStatus);
    }
}
