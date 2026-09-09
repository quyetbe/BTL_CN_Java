package dao;

import connection.DBConnection;
import model.ChuongTrinhDaoTao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChuongTrinhDaoTaoDAO {

    public List<ChuongTrinhDaoTao> getAll() {
        List<ChuongTrinhDaoTao> list = new ArrayList<>();
        String sql = "SELECT * FROM chuong_trinh_dao_tao ORDER BY hoc_ky ASC, ma_mon ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll ChuongTrinhDaoTao: " + e.getMessage());
        }
        return list;
    }

    public List<ChuongTrinhDaoTao> getBySemester(int hocKy) {
        List<ChuongTrinhDaoTao> list = new ArrayList<>();
        String sql = "SELECT * FROM chuong_trinh_dao_tao WHERE hoc_ky = ? ORDER BY ma_mon ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hocKy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBySemester: " + e.getMessage());
        }
        return list;
    }

    private ChuongTrinhDaoTao mapResultSet(ResultSet rs) throws SQLException {
        return new ChuongTrinhDaoTao(
                rs.getInt("id"),
                rs.getString("khoa"),
                rs.getInt("hoc_ky"),
                rs.getString("ma_mon"),
                rs.getString("ten_mon"),
                rs.getInt("so_tin_chi"),
                rs.getString("loai_mon"),
                rs.getString("khoa_hoc")
        );
    }
}
