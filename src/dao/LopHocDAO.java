package dao;

import connection.DBConnection;
import model.LopHoc;
import util.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng lop_hoc.
 */
public class LopHocDAO {

    public List<LopHoc> getAll() {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT ma_lop, ten_lop, si_so, khoa_hoc FROM lop_hoc ORDER BY ma_lop ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll LopHoc: " + e.getMessage());
        }
        return list;
    }

    public LopHoc getById(String maLop) {
        String sql = "SELECT ma_lop, ten_lop, si_so, khoa_hoc FROM lop_hoc WHERE ma_lop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById LopHoc: " + e.getMessage());
        }
        return null;
    }

    public List<LopHoc> search(String keyword, String khoaHoc) {
        List<LopHoc> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ma_lop, ten_lop, si_so, khoa_hoc FROM lop_hoc WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!ValidationUtil.isNullOrEmpty(keyword)) {
            sql.append("AND (ma_lop LIKE ? OR ten_lop LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        if (!ValidationUtil.isNullOrEmpty(khoaHoc) && !"TẤT CẢ".equalsIgnoreCase(khoaHoc)) {
            sql.append("AND khoa_hoc = ? ");
            params.add(khoaHoc.trim());
        }

        sql.append("ORDER BY ma_lop ASC");

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
            System.err.println("Lỗi search LopHoc: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(LopHoc lh) {
        String sql = "INSERT INTO lop_hoc (ma_lop, ten_lop, si_so, khoa_hoc) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lh.getMaLop().trim().toUpperCase());
            ps.setString(2, lh.getTenLop().trim());
            ps.setInt(3, lh.getSiSo());
            ps.setString(4, lh.getKhoaHoc().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert LopHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean update(LopHoc lh) {
        String sql = "UPDATE lop_hoc SET ten_lop = ?, si_so = ?, khoa_hoc = ? WHERE ma_lop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lh.getTenLop().trim());
            ps.setInt(2, lh.getSiSo());
            ps.setString(3, lh.getKhoaHoc().trim());
            ps.setString(4, lh.getMaLop().trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update LopHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean isReferencedInSchedule(String maLop) {
        String sql = "SELECT COUNT(*) FROM thoi_khoa_bieu WHERE ma_lop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi isReferencedInSchedule LopHoc: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String maLop) {
        if (isReferencedInSchedule(maLop)) {
            return false; // Ràng buộc khóa ngoại
        }
        String sql = "DELETE FROM lop_hoc WHERE ma_lop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete LopHoc: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllAcademicBatches() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT khoa_hoc FROM lop_hoc ORDER BY khoa_hoc ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("khoa_hoc"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllAcademicBatches: " + e.getMessage());
        }
        return list;
    }

    private LopHoc mapResultSet(ResultSet rs) throws SQLException {
        return new LopHoc(
                rs.getString("ma_lop"),
                rs.getString("ten_lop"),
                rs.getInt("si_so"),
                rs.getString("khoa_hoc")
        );
    }
}
