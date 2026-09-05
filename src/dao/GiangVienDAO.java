package dao;

import connection.DBConnection;
import model.GiangVien;
import util.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng giang_vien.
 */
public class GiangVienDAO {

    public List<GiangVien> getAll() {
        List<GiangVien> list = new ArrayList<>();
        String sql = "SELECT ma_gv, ho_ten, khoa_bo_mon, email, so_dien_thoai FROM giang_vien ORDER BY ho_ten ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll GiangVien: " + e.getMessage());
        }
        return list;
    }

    public GiangVien getById(String maGv) {
        String sql = "SELECT ma_gv, ho_ten, khoa_bo_mon, email, so_dien_thoai FROM giang_vien WHERE ma_gv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById GiangVien: " + e.getMessage());
        }
        return null;
    }

    public List<GiangVien> search(String keyword, String khoaBoMon) {
        List<GiangVien> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ma_gv, ho_ten, khoa_bo_mon, email, so_dien_thoai FROM giang_vien WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!ValidationUtil.isNullOrEmpty(keyword)) {
            sql.append("AND (ma_gv LIKE ? OR ho_ten LIKE ? OR email LIKE ? OR so_dien_thoai LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (!ValidationUtil.isNullOrEmpty(khoaBoMon) && !"TẤT CẢ".equalsIgnoreCase(khoaBoMon)) {
            sql.append("AND khoa_bo_mon = ? ");
            params.add(khoaBoMon.trim());
        }

        sql.append("ORDER BY ho_ten ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi search GiangVien: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(GiangVien gv) {
        String sql = "INSERT INTO giang_vien (ma_gv, ho_ten, khoa_bo_mon, email, so_dien_thoai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gv.getMaGv().trim().toUpperCase());
            ps.setString(2, gv.getHoTen().trim());
            ps.setString(3, gv.getKhoaBoMon().trim());
            ps.setString(4, gv.getEmail().trim());
            ps.setString(5, gv.getSoDienThoai() != null ? gv.getSoDienThoai().trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert GiangVien: " + e.getMessage());
            return false;
        }
    }

    public boolean update(GiangVien gv) {
        String sql = "UPDATE giang_vien SET ho_ten = ?, khoa_bo_mon = ?, email = ?, so_dien_thoai = ? WHERE ma_gv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gv.getHoTen().trim());
            ps.setString(2, gv.getKhoaBoMon().trim());
            ps.setString(3, gv.getEmail().trim());
            ps.setString(4, gv.getSoDienThoai() != null ? gv.getSoDienThoai().trim() : "");
            ps.setString(5, gv.getMaGv().trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update GiangVien: " + e.getMessage());
            return false;
        }
    }

    public boolean isReferencedInSchedule(String maGv) {
        String sql = "SELECT COUNT(*) FROM thoi_khoa_bieu WHERE ma_gv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi isReferencedInSchedule GiangVien: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String maGv) {
        if (isReferencedInSchedule(maGv)) {
            return false; // Ràng buộc khóa ngoại
        }
        String sql = "DELETE FROM giang_vien WHERE ma_gv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete GiangVien: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllDepartments() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT khoa_bo_mon FROM giang_vien ORDER BY khoa_bo_mon ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("khoa_bo_mon"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllDepartments: " + e.getMessage());
        }
        return list;
    }

    private GiangVien mapResultSet(ResultSet rs) throws SQLException {
        return new GiangVien(
                rs.getString("ma_gv"),
                rs.getString("ho_ten"),
                rs.getString("khoa_bo_mon"),
                rs.getString("email"),
                rs.getString("so_dien_thoai")
        );
    }
}
