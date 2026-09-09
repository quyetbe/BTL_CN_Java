package dao;

import connection.DBConnection;
import model.ThoiKhoaBieu;
import util.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng thoi_khoa_bieu.
 * Chứa câu lệnh JOIN đầy đủ để lấy thông tin chi tiết của môn, lớp, giảng viên, phòng.
 */
public class ThoiKhoaBieuDAO {

    private static final String SELECT_BASE = 
            "SELECT tkb.id, tkb.ma_mon, tkb.ma_lop, tkb.ma_gv, tkb.ma_phong, "
          + "       tkb.thu_trong_tuan, tkb.tiet_bat_dau, tkb.so_tiet, tkb.tiet_ket_thuc, "
          + "       tkb.tuan_bat_dau, tkb.tuan_ket_thuc, tkb.hoc_ky, tkb.nam_hoc, tkb.ghi_chu, "
          + "       mh.ten_mon, mh.loai_mon, "
          + "       lh.ten_lop, lh.si_so, "
          + "       gv.ho_ten AS ho_ten_gv, "
          + "       ph.ten_phong, ph.suc_chua AS suc_chua_phong, ph.loai_phong "
          + "FROM thoi_khoa_bieu tkb "
          + "JOIN mon_hoc mh ON tkb.ma_mon = mh.ma_mon "
          + "JOIN lop_hoc lh ON tkb.ma_lop = lh.ma_lop "
          + "JOIN giang_vien gv ON tkb.ma_gv = gv.ma_gv "
          + "JOIN phong_hoc ph ON tkb.ma_phong = ph.ma_phong ";

    public List<ThoiKhoaBieu> getAll() {
        List<ThoiKhoaBieu> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY tkb.nam_hoc DESC, tkb.hoc_ky ASC, tkb.thu_trong_tuan ASC, tkb.tiet_bat_dau ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll ThoiKhoaBieu: " + e.getMessage());
        }
        return list;
    }

    public ThoiKhoaBieu getById(int id) {
        String sql = SELECT_BASE + "WHERE tkb.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById ThoiKhoaBieu: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lọc danh sách thời khóa biểu theo nhiều tiêu chí (tương thích ngược).
     */
    public List<ThoiKhoaBieu> getByFilter(String hocKy, String namHoc, Integer tuan,
                                          String maPhong, String maGv, String maLop, Integer thuTrongTuan) {
        return getByFilter(hocKy, namHoc, tuan, maPhong, maGv, maLop, thuTrongTuan, null);
    }

    /**
     * Lọc danh sách thời khóa biểu theo nhiều tiêu chí bao gồm Khóa học (K21, K22, K23, K24...).
     */
    public List<ThoiKhoaBieu> getByFilter(String hocKy, String namHoc, Integer tuan,
                                          String maPhong, String maGv, String maLop, Integer thuTrongTuan, String khoaHoc) {
        List<ThoiKhoaBieu> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!ValidationUtil.isNullOrEmpty(hocKy) && !"TẤT CẢ".equalsIgnoreCase(hocKy)) {
            sql.append("AND tkb.hoc_ky = ? ");
            params.add(hocKy.trim());
        }

        if (!ValidationUtil.isNullOrEmpty(namHoc) && !"TẤT CẢ".equalsIgnoreCase(namHoc)) {
            sql.append("AND tkb.nam_hoc = ? ");
            params.add(namHoc.trim());
        }

        if (tuan != null && tuan > 0) {
            sql.append("AND ? BETWEEN tkb.tuan_bat_dau AND tkb.tuan_ket_thuc ");
            params.add(tuan);
        }

        if (!ValidationUtil.isNullOrEmpty(maPhong) && !"TẤT CẢ".equalsIgnoreCase(maPhong)) {
            sql.append("AND tkb.ma_phong = ? ");
            params.add(maPhong.trim());
        }

        if (!ValidationUtil.isNullOrEmpty(maGv) && !"TẤT CẢ".equalsIgnoreCase(maGv)) {
            sql.append("AND tkb.ma_gv = ? ");
            params.add(maGv.trim());
        }

        if (!ValidationUtil.isNullOrEmpty(maLop) && !"TẤT CẢ".equalsIgnoreCase(maLop)) {
            sql.append("AND tkb.ma_lop = ? ");
            params.add(maLop.trim());
        }

        if (thuTrongTuan != null && thuTrongTuan >= 2 && thuTrongTuan <= 8) {
            sql.append("AND tkb.thu_trong_tuan = ? ");
            params.add(thuTrongTuan);
        }

        if (!ValidationUtil.isNullOrEmpty(khoaHoc) && !"TẤT CẢ".equalsIgnoreCase(khoaHoc) && !"TẤT CẢ KHÓA".equalsIgnoreCase(khoaHoc)) {
            sql.append("AND lh.khoa_hoc = ? ");
            params.add(khoaHoc.trim());
        }

        sql.append("ORDER BY tkb.thu_trong_tuan ASC, tkb.tiet_bat_dau ASC, tkb.ma_phong ASC");

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
            System.err.println("Lỗi getByFilter ThoiKhoaBieu: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách lịch học có khả năng xung đột (cùng kỳ, năm, thứ).
     */
    public List<ThoiKhoaBieu> getSchedulesForConflictCheck(String hocKy, String namHoc, int thuTrongTuan) {
        List<ThoiKhoaBieu> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE tkb.hoc_ky = ? AND tkb.nam_hoc = ? AND tkb.thu_trong_tuan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hocKy.trim());
            ps.setString(2, namHoc.trim());
            ps.setInt(3, thuTrongTuan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getSchedulesForConflictCheck: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(ThoiKhoaBieu tkb) {
        String sql = "INSERT INTO thoi_khoa_bieu (ma_mon, ma_lop, ma_gv, ma_phong, thu_trong_tuan, "
                   + "tiet_bat_dau, so_tiet, tiet_ket_thuc, tuan_bat_dau, tuan_ket_thuc, hoc_ky, nam_hoc, ghi_chu) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int tietKetThuc = tkb.getTietBatDau() + tkb.getSoTiet() - 1;
            ps.setString(1, tkb.getMaMon().trim());
            ps.setString(2, tkb.getMaLop().trim());
            ps.setString(3, tkb.getMaGv().trim());
            ps.setString(4, tkb.getMaPhong().trim());
            ps.setInt(5, tkb.getThuTrongTuan());
            ps.setInt(6, tkb.getTietBatDau());
            ps.setInt(7, tkb.getSoTiet());
            ps.setInt(8, tietKetThuc);
            ps.setInt(9, tkb.getTuanBatDau());
            ps.setInt(10, tkb.getTuanKetThuc());
            ps.setString(11, tkb.getHocKy().trim());
            ps.setString(12, tkb.getNamHoc().trim());
            ps.setString(13, tkb.getGhiChu() != null ? tkb.getGhiChu().trim() : "");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        tkb.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi insert ThoiKhoaBieu: " + e.getMessage());
        }
        return false;
    }

    public boolean update(ThoiKhoaBieu tkb) {
        String sql = "UPDATE thoi_khoa_bieu SET ma_mon = ?, ma_lop = ?, ma_gv = ?, ma_phong = ?, "
                   + "thu_trong_tuan = ?, tiet_bat_dau = ?, so_tiet = ?, tiet_ket_thuc = ?, "
                   + "tuan_bat_dau = ?, tuan_ket_thuc = ?, hoc_ky = ?, nam_hoc = ?, ghi_chu = ? "
                   + "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int tietKetThuc = tkb.getTietBatDau() + tkb.getSoTiet() - 1;
            ps.setString(1, tkb.getMaMon().trim());
            ps.setString(2, tkb.getMaLop().trim());
            ps.setString(3, tkb.getMaGv().trim());
            ps.setString(4, tkb.getMaPhong().trim());
            ps.setInt(5, tkb.getThuTrongTuan());
            ps.setInt(6, tkb.getTietBatDau());
            ps.setInt(7, tkb.getSoTiet());
            ps.setInt(8, tietKetThuc);
            ps.setInt(9, tkb.getTuanBatDau());
            ps.setInt(10, tkb.getTuanKetThuc());
            ps.setString(11, tkb.getHocKy().trim());
            ps.setString(12, tkb.getNamHoc().trim());
            ps.setString(13, tkb.getGhiChu() != null ? tkb.getGhiChu().trim() : "");
            ps.setInt(14, tkb.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update ThoiKhoaBieu: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM thoi_khoa_bieu WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete ThoiKhoaBieu: " + e.getMessage());
            return false;
        }
    }

    public List<String> getDistinctSemesters() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT hoc_ky FROM thoi_khoa_bieu ORDER BY hoc_ky ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("hoc_ky"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDistinctSemesters: " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.add("HK1");
            list.add("HK2");
            list.add("HK3");
        }
        return list;
    }

    public List<String> getDistinctAcademicYears() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT nam_hoc FROM thoi_khoa_bieu ORDER BY nam_hoc DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String y = rs.getString("nam_hoc");
                if (y != null && !y.trim().isEmpty() && !list.contains(y.trim())) {
                    list.add(y.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDistinctAcademicYears: " + e.getMessage());
        }
        String[] defaults = {"2025-2026", "2024-2025", "2023-2024", "2022-2023", "2021-2022"};
        for (String d : defaults) {
            if (!list.contains(d)) {
                list.add(d);
            }
        }
        return list;
    }

    public List<String> getDistinctKhoaHoc() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT khoa_hoc FROM lop_hoc WHERE khoa_hoc IS NOT NULL AND khoa_hoc != '' ORDER BY khoa_hoc ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String k = rs.getString("khoa_hoc");
                if (k != null && !k.trim().isEmpty() && !list.contains(k.trim())) {
                    list.add(k.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDistinctKhoaHoc: " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.add("K21");
            list.add("K22");
            list.add("K23");
            list.add("K24");
        }
        return list;
    }

    private ThoiKhoaBieu mapResultSet(ResultSet rs) throws SQLException {
        ThoiKhoaBieu tkb = new ThoiKhoaBieu(
                rs.getInt("id"),
                rs.getString("ma_mon"),
                rs.getString("ma_lop"),
                rs.getString("ma_gv"),
                rs.getString("ma_phong"),
                rs.getInt("thu_trong_tuan"),
                rs.getInt("tiet_bat_dau"),
                rs.getInt("so_tiet"),
                rs.getInt("tiet_ket_thuc"),
                rs.getInt("tuan_bat_dau"),
                rs.getInt("tuan_ket_thuc"),
                rs.getString("hoc_ky"),
                rs.getString("nam_hoc"),
                rs.getString("ghi_chu")
        );
        tkb.setTenMon(rs.getString("ten_mon"));
        tkb.setLoaiMon(rs.getString("loai_mon"));
        tkb.setTenLop(rs.getString("ten_lop"));
        tkb.setSiSoLop(rs.getInt("si_so"));
        tkb.setHoTenGv(rs.getString("ho_ten_gv"));
        tkb.setTenPhong(rs.getString("ten_phong"));
        tkb.setSucChuaPhong(rs.getInt("suc_chua_phong"));
        tkb.setLoaiPhong(rs.getString("loai_phong"));
        return tkb;
    }
}
