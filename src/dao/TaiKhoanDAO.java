package dao;

import connection.DBConnection;
import model.TaiKhoan;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng tai_khoan.
 * 100% sử dụng PreparedStatement và try-with-resources.
 */
public class TaiKhoanDAO {

    /**
     * Xác thực đăng nhập người dùng.
     */
    public TaiKhoan login(String username, String rawPassword) {
        String sql = "SELECT id, ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai, ngay_tao "
                   + "FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("mat_khau");
                    if (PasswordUtil.verifyPassword(rawPassword, storedHash)) {
                        return mapResultSetToTaiKhoan(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT id, ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai, ngay_tao "
                   + "FROM tai_khoan ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTaiKhoan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll TaiKhoan: " + e.getMessage());
        }
        return list;
    }

    public TaiKhoan getById(int id) {
        String sql = "SELECT id, ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai, ngay_tao "
                   + "FROM tai_khoan WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTaiKhoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById TaiKhoan: " + e.getMessage());
        }
        return null;
    }

    public TaiKhoan getByUsername(String username) {
        String sql = "SELECT id, ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai, ngay_tao "
                   + "FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTaiKhoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByUsername: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap().trim());
            ps.setString(2, tk.getMatKhau()); // Đã băm trước khi đưa vào DAO
            ps.setString(3, tk.getHoTen().trim());
            ps.setString(4, tk.getEmail() != null ? tk.getEmail().trim() : null);
            ps.setString(5, tk.getVaiTro());
            ps.setBoolean(6, tk.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert TaiKhoan: " + e.getMessage());
            return false;
        }
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE tai_khoan SET ho_ten = ?, email = ?, vai_tro = ?, trang_thai = ? "
                   + "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getHoTen().trim());
            ps.setString(2, tk.getEmail() != null ? tk.getEmail().trim() : null);
            ps.setString(3, tk.getVaiTro());
            ps.setBoolean(4, tk.isTrangThai());
            ps.setInt(5, tk.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update TaiKhoan: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int id, String newHashedPassword) {
        String sql = "UPDATE tai_khoan SET mat_khau = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi changePassword: " + e.getMessage());
            return false;
        }
    }

    public boolean setStatus(int id, boolean status) {
        String sql = "UPDATE tai_khoan SET trang_thai = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi setStatus: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tai_khoan WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete TaiKhoan: " + e.getMessage());
            return false;
        }
    }

    private TaiKhoan mapResultSetToTaiKhoan(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getInt("id"),
                rs.getString("ten_dang_nhap"),
                rs.getString("mat_khau"),
                rs.getString("ho_ten"),
                rs.getString("email"),
                rs.getString("vai_tro"),
                rs.getBoolean("trang_thai"),
                rs.getTimestamp("ngay_tao")
        );
    }
}
