package util;

import connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Tiện ích một chạm để chuẩn hóa bảng mã UTF-8 (utf8mb4)
 * và phục hồi tiếng Việt có dấu chuẩn cho tất cả các bảng trong MySQL.
 */
public class FixDatabaseEncoding {

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU CHUẨN HÓA BỘ MÃ UTF-8 & PHỤC HỒI TIẾNG VIỆT ===");
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                // 1. Đảm bảo connection và database dùng utf8mb4
                stmt.execute("SET NAMES utf8mb4;");
                stmt.execute("ALTER DATABASE `quanly_tkb_cnj56` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");

                String[] tables = {
                    "lop_hoc", "tai_khoan", "sinh_vien", "giang_vien", "mon_hoc",
                    "phong_hoc", "thoi_khoa_bieu", "chuong_trinh_dao_tao",
                    "yeu_cau_doi_lich", "lich_su_phe_duyet", "audit_log"
                };
                for (String t : tables) {
                    try {
                        stmt.execute("ALTER TABLE `" + t + "` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                    } catch (Exception e) {
                        System.err.println("Lưu ý khi convert table " + t + ": " + e.getMessage());
                    }
                }
            }

            // 2. Cập nhật tên lớp học chuẩn tiếng Việt cho 40 lớp
            System.out.println("1. Phục hồi 40 lớp học tiếng Việt chuẩn...");
            String sqlLop = "UPDATE lop_hoc SET ten_lop = ? WHERE ma_lop = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlLop)) {
                String[] cohorts = {"K21", "K22", "K23", "K24"};
                for (String k : cohorts) {
                    String kNum = k.substring(1);
                    for (int c = 1; c <= 10; c++) {
                        String maLop = "D" + kNum + "CNTT" + String.format("%02d", c);
                        String tenLop = "Đại học CNTT " + c + " - " + k;
                        ps.setString(1, tenLop);
                        ps.setString(2, maLop);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            // 3. Cập nhật 6 tài khoản chính thức chuẩn tiếng Việt
            System.out.println("2. Phục hồi họ tên 6 tài khoản chuẩn tiếng Việt...");
            String sqlTk = "UPDATE tai_khoan SET ho_ten = ? WHERE ten_dang_nhap = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTk)) {
                Object[][] accounts = {
                    {"admin", "Quản Trị Viên Hệ Thống"},
                    {"bangiamhieu", "GS.TS. Trần Văn Hiệu (Hiệu Trưởng)"},
                    {"truongkhoa", "PGS.TS. Lê Đình Khoa (Trưởng Khoa CNTT)"},
                    {"daotao", "ThS. Hoàng Minh Đào Tạo (Cán Bộ Đào Tạo)"},
                    {"giangvien", "TS. Nguyễn Văn An (Giảng Viên Bộ Môn)"},
                    {"sinhvien", "Đỗ Xuân Hùng (Sinh Viên K21)"}
                };
                for (Object[] acc : accounts) {
                    ps.setString(1, (String) acc[1]);
                    ps.setString(2, (String) acc[0]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4. Phục hồi dữ liệu yêu cầu đổi lịch
            System.out.println("3. Phục hồi lý do yêu cầu đổi lịch...");
            String sqlYc = "UPDATE yeu_cau_doi_lich SET ly_do = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlYc)) {
                Object[][] requests = {
                    {1, "Giảng viên đi công tác hội nghị đầu tuần, xin chuyển sang Thứ 4"},
                    {2, "Phòng LT202 bảo trì hệ thống điều hòa, xin đổi sang LT201"},
                    {3, "Lớp thực hành cần bổ sung thêm máy tính cấu hình đồ họa cao"}
                };
                for (Object[] r : requests) {
                    ps.setString(1, (String) r[1]);
                    ps.setInt(2, (Integer) r[0]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 5. Việt hóa toàn bộ hành động (hanh_dong) trong audit_log
            System.out.println("4. Việt hóa toàn bộ hành động nhật ký hệ thống...");
            String sqlAuditAct = "UPDATE audit_log SET hanh_dong = ? WHERE hanh_dong = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAuditAct)) {
                String[][] actionMaps = {
                    {"Khởi tạo hệ thống", "SYSTEM_INIT"},
                    {"Nạp khung CTĐT", "SEED_CURRICULUM"},
                    {"Nạp phòng học", "SEED_ROOMS"},
                    {"Nạp giảng viên", "SEED_LECTURERS"},
                    {"Nạp sinh viên", "SEED_STUDENTS"},
                    {"Đăng nhập", "LOGIN"},
                    {"Đăng xuất", "LOGOUT"},
                    {"Xếp lịch tự động", "AUTO_SCHEDULE"},
                    {"Thêm lịch TKB", "CREATE_SCHEDULE"},
                    {"Cập nhật TKB", "UPDATE_SCHEDULE"},
                    {"Xóa lịch TKB", "DELETE_SCHEDULE"},
                    {"Cập nhật sinh viên", "UPDATE_STUDENT"},
                    {"Cập nhật giảng viên", "UPDATE_LECTURER"},
                    {"Xuất file Excel", "EXPORT_EXCEL"},
                    {"Thêm phòng học", "CREATE_ROOM"},
                    {"Cập nhật phòng học", "UPDATE_ROOM"},
                    {"Xóa phòng học", "DELETE_ROOM"},
                    {"Đặt lại mật khẩu", "RESET_PASSWORD"},
                    {"Xử lý xung đột", "RESOLVE_CONFLICT"},
                    {"Cập nhật lớp học", "UPDATE_CLASS"},
                    {"Cập nhật phân quyền", "UPDATE_ROLE"},
                    {"Nhập sinh viên", "IMPORT_STUDENTS"},
                    {"Kiểm tra an toàn", "SECURITY_CHECK"},
                    {"Kiểm tra phòng học", "CHECK_ROOM_STATUS"},
                    {"Xuất nhật ký", "EXPORT_AUDIT_LOG"},
                    {"Đề xuất đổi lịch", "SUBMIT_RESCHEDULE"},
                    {"Duyệt cấp 1 (Khoa)", "APPROVE_LEVEL_1"},
                    {"Từ chối cấp 1", "REJECT_LEVEL_1"},
                    {"Từ chối cấp 2", "REJECT_LEVEL_2"},
                    {"Phê duyệt & Áp dụng TKB", "FINAL_APPROVE_AND_APPLY"}
                };
                for (String[] pair : actionMaps) {
                    ps.setString(1, pair[0]);
                    ps.setString(2, pair[1]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("=== CHUẨN HÓA BỘ MÃ UTF-8 THÀNH CÔNG 100%! ===");
        } catch (Exception e) {
            System.err.println("Lỗi chuẩn hóa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
