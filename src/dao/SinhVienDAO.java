package dao;

import connection.DBConnection;
import model.SinhVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý truy vấn CSDL cho bảng sinh_vien.
 */
public class SinhVienDAO {

    public List<SinhVien> getAll() {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM sinh_vien ORDER BY ma_sv ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll SinhVien: " + e.getMessage());
        }
        return list;
    }

    public List<SinhVien> getByClass(String maLop) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM sinh_vien WHERE ma_lop = ? ORDER BY ma_sv ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByClass SinhVien: " + e.getMessage());
        }
        return list;
    }

    public SinhVien getById(String maSv) {
        String sql = "SELECT * FROM sinh_vien WHERE ma_sv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById SinhVien: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(SinhVien sv) {
        String sql = "INSERT INTO sinh_vien (ma_sv, ho_ten, email, so_dien_thoai, gioi_tinh, ngay_sinh, ma_lop, khoa_hoc, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getMaSv());
            ps.setString(2, sv.getHoTen());
            ps.setString(3, sv.getEmail());
            ps.setString(4, sv.getSoDienThoai());
            ps.setString(5, sv.getGioiTinh());
            ps.setDate(6, sv.getNgaySinh());
            ps.setString(7, sv.getMaLop());
            ps.setString(8, sv.getKhoaHoc());
            ps.setString(9, sv.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert SinhVien: " + e.getMessage());
            return false;
        }
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM sinh_vien";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi countTotal SinhVien: " + e.getMessage());
        }
        return 0;
    }

    public static class ThongKeKhoaHocRecord {
        public String khoaHoc;
        public int tongSv;
        public int namCount;
        public int nuCount;
        public int lopCount;
    }

    public List<ThongKeKhoaHocRecord> getThongKeKhoaHoc() {
        List<ThongKeKhoaHocRecord> list = new ArrayList<>();
        String sql = "SELECT khoa_hoc, COUNT(*) as tong_sv, "
                   + "       SUM(CASE WHEN gioi_tinh = 'Nam' THEN 1 ELSE 0 END) as nam_count, "
                   + "       SUM(CASE WHEN gioi_tinh = 'Nu' THEN 1 ELSE 0 END) as nu_count, "
                   + "       COUNT(DISTINCT ma_lop) as lop_count "
                   + "FROM sinh_vien "
                   + "GROUP BY khoa_hoc "
                   + "ORDER BY khoa_hoc ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeKhoaHocRecord r = new ThongKeKhoaHocRecord();
                r.khoaHoc = rs.getString("khoa_hoc");
                r.tongSv = rs.getInt("tong_sv");
                r.namCount = rs.getInt("nam_count");
                r.nuCount = rs.getInt("nu_count");
                r.lopCount = rs.getInt("lop_count");
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getThongKeKhoaHoc: " + e.getMessage());
        }
        return list;
    }

    public static class ThongKeLopRecord {
        public String maLop;
        public int tongSv;
        public int namCount;
        public int nuCount;
        public int dangHocCount;
    }

    public List<ThongKeLopRecord> getThongKeTheoLop() {
        List<ThongKeLopRecord> list = new ArrayList<>();
        String sql = "SELECT ma_lop, COUNT(*) as tong_sv, "
                   + "       SUM(CASE WHEN gioi_tinh = 'Nam' THEN 1 ELSE 0 END) as nam_count, "
                   + "       SUM(CASE WHEN gioi_tinh = 'Nu' THEN 1 ELSE 0 END) as nu_count, "
                   + "       SUM(CASE WHEN trang_thai = 'DANG_HOC' THEN 1 ELSE 0 END) as dang_hoc_count "
                   + "FROM sinh_vien "
                   + "GROUP BY ma_lop "
                   + "ORDER BY ma_lop ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeLopRecord r = new ThongKeLopRecord();
                r.maLop = rs.getString("ma_lop");
                r.tongSv = rs.getInt("tong_sv");
                r.namCount = rs.getInt("nam_count");
                r.nuCount = rs.getInt("nu_count");
                r.dangHocCount = rs.getInt("dang_hoc_count");
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getThongKeTheoLop: " + e.getMessage());
        }
        return list;
    }

    private SinhVien mapResultSet(ResultSet rs) throws SQLException {
        return new SinhVien(
                rs.getString("ma_sv"),
                rs.getString("ho_ten"),
                rs.getString("email"),
                rs.getString("so_dien_thoai"),
                rs.getString("gioi_tinh"),
                rs.getDate("ngay_sinh"),
                rs.getString("ma_lop"),
                rs.getString("khoa_hoc"),
                rs.getString("trang_thai"),
                rs.getTimestamp("ngay_tao")
        );
    }
}
