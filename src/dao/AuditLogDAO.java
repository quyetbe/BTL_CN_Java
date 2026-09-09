package dao;

import connection.DBConnection;
import model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public boolean log(Integer userId, String tenDangNhap, String hanhDong, String doiTuong, Integer doiTuongId, String duLieuCu, String duLieuMoi, String ipAddress) {
        String sql = "INSERT INTO audit_log (user_id, ten_dang_nhap, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_cu, du_lieu_moi, ip_address) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, tenDangNhap);
            ps.setString(3, hanhDong);
            ps.setString(4, doiTuong);
            if (doiTuongId != null) ps.setInt(5, doiTuongId); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, duLieuCu);
            ps.setString(7, duLieuMoi);
            ps.setString(8, ipAddress != null ? ipAddress : "127.0.0.1");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi ghi Audit Log: " + e.getMessage());
            return false;
        }
    }

    public List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY ngay_tao DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AuditLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("ten_dang_nhap"),
                            rs.getString("hanh_dong"),
                            rs.getString("doi_tuong"),
                            rs.getInt("doi_tuong_id"),
                            rs.getString("du_lieu_cu"),
                            rs.getString("du_lieu_moi"),
                            rs.getString("ip_address"),
                            rs.getTimestamp("ngay_tao")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getRecentLogs: " + e.getMessage());
        }
        return list;
    }

    public List<AuditLog> getAll() {
        return search(null, null);
    }

    public List<AuditLog> search(String keyword, String action) {
        List<AuditLog> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM audit_log WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ten_dang_nhap LIKE ? OR doi_tuong LIKE ? OR hanh_dong LIKE ? OR du_lieu_cu LIKE ? OR du_lieu_moi LIKE ? OR ip_address LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            for (int i = 0; i < 6; i++) {
                params.add(kw);
            }
        }

        if (action != null && !action.trim().isEmpty() && !"TẤT CẢ".equalsIgnoreCase(action) && !"TẤT CẢ HÀNH ĐỘNG".equalsIgnoreCase(action)) {
            String actTrim = action.trim();
            String mapped = mapActionCode(actTrim);
            if (mapped != null && !mapped.equalsIgnoreCase(actTrim)) {
                sql.append("AND (hanh_dong = ? OR hanh_dong = ?) ");
                params.add(actTrim);
                params.add(mapped);
            } else {
                sql.append("AND hanh_dong = ? ");
                params.add(actTrim);
            }
        }

        sql.append("ORDER BY ngay_tao DESC, id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AuditLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("ten_dang_nhap"),
                            rs.getString("hanh_dong"),
                            rs.getString("doi_tuong"),
                            rs.getInt("doi_tuong_id"),
                            rs.getString("du_lieu_cu"),
                            rs.getString("du_lieu_moi"),
                            rs.getString("ip_address"),
                            rs.getTimestamp("ngay_tao")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi search AuditLog: " + e.getMessage());
        }
        return list;
    }

    public List<String> getDistinctActions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT hanh_dong FROM audit_log WHERE hanh_dong IS NOT NULL ORDER BY hanh_dong ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String act = rs.getString("hanh_dong");
                if (act != null && !act.trim().isEmpty()) {
                    list.add(act.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDistinctActions: " + e.getMessage());
        }
        return list;
    }

    private String mapActionCode(String act) {
        if (act == null) return null;
        switch (act.trim()) {
            case "Đăng nhập": return "LOGIN";
            case "LOGIN": return "Đăng nhập";
            case "Đăng xuất": return "LOGOUT";
            case "LOGOUT": return "Đăng xuất";
            case "Xếp lịch tự động": return "AUTO_SCHEDULE";
            case "AUTO_SCHEDULE": return "Xếp lịch tự động";
            case "Thêm lịch TKB": return "CREATE_SCHEDULE";
            case "CREATE_SCHEDULE": return "Thêm lịch TKB";
            case "Cập nhật TKB": return "UPDATE_SCHEDULE";
            case "UPDATE_SCHEDULE": return "Cập nhật TKB";
            case "Xóa lịch TKB": return "DELETE_SCHEDULE";
            case "DELETE_SCHEDULE": return "Xóa lịch TKB";
            case "Cập nhật sinh viên": return "UPDATE_STUDENT";
            case "UPDATE_STUDENT": return "Cập nhật sinh viên";
            case "Cập nhật giảng viên": return "UPDATE_LECTURER";
            case "UPDATE_LECTURER": return "Cập nhật giảng viên";
            case "Xuất file Excel": return "EXPORT_EXCEL";
            case "EXPORT_EXCEL": return "Xuất file Excel";
            case "Thêm phòng học": return "CREATE_ROOM";
            case "CREATE_ROOM": return "Thêm phòng học";
            case "Cập nhật phòng học": return "UPDATE_ROOM";
            case "UPDATE_ROOM": return "Cập nhật phòng học";
            case "Xóa phòng học": return "DELETE_ROOM";
            case "DELETE_ROOM": return "Xóa phòng học";
            case "Đặt lại mật khẩu": return "RESET_PASSWORD";
            case "RESET_PASSWORD": return "Đặt lại mật khẩu";
            case "Xử lý xung đột": return "RESOLVE_CONFLICT";
            case "RESOLVE_CONFLICT": return "Xử lý xung đột";
            case "Cập nhật lớp học": return "UPDATE_CLASS";
            case "UPDATE_CLASS": return "Cập nhật lớp học";
            case "Cập nhật phân quyền": return "UPDATE_ROLE";
            case "UPDATE_ROLE": return "Cập nhật phân quyền";
            case "Nhập sinh viên": return "IMPORT_STUDENTS";
            case "IMPORT_STUDENTS": return "Nhập sinh viên";
            case "Kiểm tra an toàn": return "SECURITY_CHECK";
            case "SECURITY_CHECK": return "Kiểm tra an toàn";
            case "Kiểm tra phòng học": return "CHECK_ROOM_STATUS";
            case "CHECK_ROOM_STATUS": return "Kiểm tra phòng học";
            case "Xuất nhật ký": return "EXPORT_AUDIT_LOG";
            case "EXPORT_AUDIT_LOG": return "Xuất nhật ký";
            case "Đề xuất đổi lịch": return "SUBMIT_RESCHEDULE";
            case "SUBMIT_RESCHEDULE": return "Đề xuất đổi lịch";
            case "Duyệt cấp 1 (Khoa)": return "APPROVE_LEVEL_1";
            case "APPROVE_LEVEL_1": return "Duyệt cấp 1 (Khoa)";
            case "Từ chối cấp 1": return "REJECT_LEVEL_1";
            case "REJECT_LEVEL_1": return "Từ chối cấp 1";
            case "Từ chối cấp 2": return "REJECT_LEVEL_2";
            case "REJECT_LEVEL_2": return "Từ chối cấp 2";
            case "Phê duyệt & Áp dụng TKB": return "FINAL_APPROVE_AND_APPLY";
            case "FINAL_APPROVE_AND_APPLY": return "Phê duyệt & Áp dụng TKB";
            default: return act;
        }
    }
}
