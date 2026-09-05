package dao;

import connection.DBConnection;
import model.PhongHoc;
import util.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng phong_hoc (phòng học & tài nguyên).
 */
public class PhongHocDAO {

    public List<PhongHoc> getAll() {
        List<PhongHoc> list = new ArrayList<>();
        String sql = "SELECT ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai "
                   + "FROM phong_hoc ORDER BY toa_nha ASC, ma_phong ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll PhongHoc: " + e.getMessage());
        }
        return list;
    }

    public List<PhongHoc> getAvailableRooms() {
        List<PhongHoc> list = new ArrayList<>();
        String sql = "SELECT ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai "
                   + "FROM phong_hoc WHERE trang_thai = 'DANG_SU_DUNG' ORDER BY toa_nha ASC, ma_phong ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAvailableRooms: " + e.getMessage());
        }
        return list;
    }

    public PhongHoc getById(String maPhong) {
        String sql = "SELECT ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai "
                   + "FROM phong_hoc WHERE ma_phong = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById PhongHoc: " + e.getMessage());
        }
        return null;
    }

    public List<PhongHoc> search(String keyword, String toaNha, String loaiPhong, String trangThai) {
        List<PhongHoc> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai FROM phong_hoc WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!ValidationUtil.isNullOrEmpty(keyword)) {
            sql.append("AND (ma_phong LIKE ? OR ten_phong LIKE ? OR trang_thiet_bi LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (!ValidationUtil.isNullOrEmpty(toaNha) && !"TẤT CẢ".equalsIgnoreCase(toaNha)) {
            sql.append("AND toa_nha = ? ");
            params.add(toaNha.trim());
        }

        if (!ValidationUtil.isNullOrEmpty(loaiPhong) && !"TẤT CẢ".equalsIgnoreCase(loaiPhong)) {
            sql.append("AND loai_phong = ? ");
            params.add(loaiPhong.trim());
        }

        if (!ValidationUtil.isNullOrEmpty(trangThai) && !"TẤT CẢ".equalsIgnoreCase(trangThai)) {
            sql.append("AND trang_thai = ? ");
            params.add(trangThai.trim());
        }

        sql.append("ORDER BY toa_nha ASC, ma_phong ASC");

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
            System.err.println("Lỗi search PhongHoc: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(PhongHoc p) {
        String sql = "INSERT INTO phong_hoc (ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhong().trim().toUpperCase());
            ps.setString(2, p.getTenPhong().trim());
            ps.setString(3, p.getToaNha().trim());
            ps.setInt(4, p.getSucChua());
            ps.setString(5, p.getLoaiPhong());
            ps.setString(6, p.getTrangThietBi() != null ? p.getTrangThietBi().trim() : "");
            ps.setString(7, p.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert PhongHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean update(PhongHoc p) {
        String sql = "UPDATE phong_hoc SET ten_phong = ?, toa_nha = ?, suc_chua = ?, loai_phong = ?, "
                   + "trang_thiet_bi = ?, trang_thai = ? WHERE ma_phong = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhong().trim());
            ps.setString(2, p.getToaNha().trim());
            ps.setInt(3, p.getSucChua());
            ps.setString(4, p.getLoaiPhong());
            ps.setString(5, p.getTrangThietBi() != null ? p.getTrangThietBi().trim() : "");
            ps.setString(6, p.getTrangThai());
            ps.setString(7, p.getMaPhong().trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update PhongHoc: " + e.getMessage());
            return false;
        }
    }

    public boolean isReferencedInSchedule(String maPhong) {
        String sql = "SELECT COUNT(*) FROM thoi_khoa_bieu WHERE ma_phong = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi isReferencedInSchedule PhongHoc: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String maPhong) {
        if (isReferencedInSchedule(maPhong)) {
            return false; // Ràng buộc khóa ngoại
        }
        String sql = "DELETE FROM phong_hoc WHERE ma_phong = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete PhongHoc: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllBuildings() {
        List<String> buildings = new ArrayList<>();
        String sql = "SELECT DISTINCT toa_nha FROM phong_hoc ORDER BY toa_nha ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                buildings.add(rs.getString("toa_nha"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllBuildings: " + e.getMessage());
        }
        return buildings;
    }

    private PhongHoc mapResultSet(ResultSet rs) throws SQLException {
        return new PhongHoc(
                rs.getString("ma_phong"),
                rs.getString("ten_phong"),
                rs.getString("toa_nha"),
                rs.getInt("suc_chua"),
                rs.getString("loai_phong"),
                rs.getString("trang_thiet_bi"),
                rs.getString("trang_thai")
        );
    }
}
