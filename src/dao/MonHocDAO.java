package dao;

import connection.DBConnection;
import model.MonHoc;
import util.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng mon_hoc.
 */
public class MonHocDAO {

    public List<MonHoc> getAll() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT ma_mon, ten_mon, so_tin_chi, loai_mon FROM mon_hoc ORDER BY ma_mon ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll MonHoc: " + e.getMessage());
        }
        return list;
    }

    public MonHoc getById(String maMon) {
        String sql = "SELECT ma_mon, ten_mon, so_tin_chi, loai_mon FROM mon_hoc WHERE ma_mon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById MonHoc: " + e.getMessage());
        }
        return null;
    }

    public List<MonHoc> search(String keyword, String loaiMon) {
        List<MonHoc> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ma_mon, ten_mon, so_tin_chi, loai_mon FROM mon_hoc WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!ValidationUtil.isNullOrEmpty(keyword)) {
            sql.append("AND (ma_mon LIKE ? OR ten_mon LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        if (!ValidationUtil.isNullOrEmpty(loaiMon) && !"TẤT CẢ".equalsIgnoreCase(loaiMon)) {
            sql.append("AND loai_mon = ? ");
            params.add(loaiMon.trim());
        }

        sql.append("ORDER BY ma_mon ASC");

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
            System.err.println("Lỗi search MonHoc: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(MonHoc mh) {
        String sql = "INSERT INTO mon_hoc (ma_mon, ten_mon, so_tin_chi, loai_mon) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mh.getMaMon().trim().toUpperCase());
            ps.setString(2, mh.getTenMon().trim());
            ps.setInt(3, mh.getSoTinChi());
            ps.setString(4, mh.getLoaiMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert MonHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean update(MonHoc mh) {
        String sql = "UPDATE mon_hoc SET ten_mon = ?, so_tin_chi = ?, loai_mon = ? WHERE ma_mon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mh.getTenMon().trim());
            ps.setInt(2, mh.getSoTinChi());
            ps.setString(3, mh.getLoaiMon());
            ps.setString(4, mh.getMaMon().trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update MonHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean isReferencedInSchedule(String maMon) {
        String sql = "SELECT COUNT(*) FROM thoi_khoa_bieu WHERE ma_mon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi isReferencedInSchedule MonHoc: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String maMon) {
        if (isReferencedInSchedule(maMon)) {
            return false; // Ràng buộc khóa ngoại
        }
        String sql = "DELETE FROM mon_hoc WHERE ma_mon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete MonHoc: " + e.getMessage());
            return false;
        }
    }

    private MonHoc mapResultSet(ResultSet rs) throws SQLException {
        return new MonHoc(
                rs.getString("ma_mon"),
                rs.getString("ten_mon"),
                rs.getInt("so_tin_chi"),
                rs.getString("loai_mon")
        );
    }
}
