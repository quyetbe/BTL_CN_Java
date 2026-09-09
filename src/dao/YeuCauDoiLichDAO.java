package dao;

import connection.DBConnection;
import model.LichSuDuyet;
import model.YeuCauDoiLich;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YeuCauDoiLichDAO {

    private static final String BASE_SELECT = 
            "SELECT y.*, gv.ho_ten AS ho_ten_gv, mh.ten_mon, lh.ten_lop, "
          + "       tkb.ma_phong AS ma_phong_cu, tkb.thu_trong_tuan AS thu_cu, tkb.tiet_bat_dau AS tiet_bat_dau_cu "
          + "FROM yeu_cau_doi_lich y "
          + "JOIN thoi_khoa_bieu tkb ON y.ma_tkb = tkb.id "
          + "JOIN giang_vien gv ON y.ma_gv = gv.ma_gv "
          + "JOIN mon_hoc mh ON tkb.ma_mon = mh.ma_mon "
          + "JOIN lop_hoc lh ON tkb.ma_lop = lh.ma_lop ";

    public List<YeuCauDoiLich> getAll(String statusFilter) {
        List<YeuCauDoiLich> list = new ArrayList<>();
        String sql = BASE_SELECT;
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql += " WHERE y.trang_thai = ? ";
        }
        sql += " ORDER BY y.ngay_tao DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                ps.setString(1, statusFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll YeuCauDoiLich: " + e.getMessage());
        }
        return list;
    }

    public List<YeuCauDoiLich> getAll() {
        return getAll("ALL");
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM yeu_cau_doi_lich WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete YeuCauDoiLich: " + e.getMessage());
            return false;
        }
    }

    public YeuCauDoiLich getById(int id) {
        String sql = BASE_SELECT + " WHERE y.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById YeuCauDoiLich: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(YeuCauDoiLich y) {
        String sql = "INSERT INTO yeu_cau_doi_lich (ma_tkb, ma_gv, ma_phong_moi, thu_moi, tiet_bat_dau_moi, so_tiet, tuan_bat_dau_moi, tuan_ket_thuc_moi, ly_do, trang_thai, cap_phe_duyet) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'CHO_KHOA_DUYET', 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, y.getMaTkb());
            ps.setString(2, y.getMaGv());
            ps.setString(3, y.getMaPhongMoi());
            ps.setInt(4, y.getThuMoi());
            ps.setInt(5, y.getTietBatDauMoi());
            ps.setInt(6, y.getSoTiet());
            ps.setInt(7, y.getTuanBatDauMoi());
            ps.setInt(8, y.getTuanKetThucMoi());
            ps.setString(9, y.getLyDo());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) y.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi insert YeuCauDoiLich: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatusAndLevel(int id, String status, int level) {
        String sql = "UPDATE yeu_cau_doi_lich SET trang_thai = ?, cap_phe_duyet = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, level);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateStatusAndLevel: " + e.getMessage());
            return false;
        }
    }

    public boolean addApprovalHistory(LichSuDuyet ls) {
        String sql = "INSERT INTO lich_su_phe_duyet (yeu_cau_id, nguoi_duyet_id, cap_duyet, hanh_dong, y_kien) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ls.getYeuCauId());
            ps.setInt(2, ls.getNguoiDuyetId());
            ps.setString(3, ls.getCapDuyet());
            ps.setString(4, ls.getHanhDong());
            ps.setString(5, ls.getyKien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi addApprovalHistory: " + e.getMessage());
            return false;
        }
    }

    public List<LichSuDuyet> getHistory(int yeuCauId) {
        List<LichSuDuyet> list = new ArrayList<>();
        String sql = "SELECT ls.*, tk.ho_ten AS ten_nguoi_duyet FROM lich_su_phe_duyet ls "
                   + "LEFT JOIN tai_khoan tk ON ls.nguoi_duyet_id = tk.id "
                   + "WHERE ls.yeuCau_id = ? ORDER BY ls.ngay_duyet ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, yeuCauId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichSuDuyet l = new LichSuDuyet(
                            rs.getInt("id"),
                            rs.getInt("yeu_cau_id"),
                            rs.getInt("nguoi_duyet_id"),
                            rs.getString("cap_duyet"),
                            rs.getString("hanh_dong"),
                            rs.getString("y_kien"),
                            rs.getTimestamp("ngay_duyet")
                    );
                    l.setTenNguoiDuyet(rs.getString("ten_nguoi_duyet"));
                    list.add(l);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getHistory: " + e.getMessage());
        }
        return list;
    }

    private YeuCauDoiLich mapResultSet(ResultSet rs) throws SQLException {
        YeuCauDoiLich y = new YeuCauDoiLich(
                rs.getInt("id"),
                rs.getInt("ma_tkb"),
                rs.getString("ma_gv"),
                rs.getString("ma_phong_moi"),
                rs.getInt("thu_moi"),
                rs.getInt("tiet_bat_dau_moi"),
                rs.getInt("so_tiet"),
                rs.getInt("tuan_bat_dau_moi"),
                rs.getInt("tuan_ket_thuc_moi"),
                rs.getString("ly_do"),
                rs.getString("trang_thai"),
                rs.getInt("cap_phe_duyet"),
                rs.getTimestamp("ngay_tao")
        );
        y.setHoTenGv(rs.getString("ho_ten_gv"));
        y.setTenMon(rs.getString("ten_mon"));
        y.setTenLop(rs.getString("ten_lop"));
        y.setMaPhongCu(rs.getString("ma_phong_cu"));
        y.setThuCu(rs.getInt("thu_cu"));
        y.setTietBatDauCu(rs.getInt("tiet_bat_dau_cu"));
        return y;
    }
}
