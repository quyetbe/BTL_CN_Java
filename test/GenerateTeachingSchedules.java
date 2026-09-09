package test;

import connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class GenerateTeachingSchedules {

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TẠO THỜI KHÓA BIỂU GIẢNG DẠY CHO GIẢNG VIÊN ===");
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                generateSchedules(conn);
                conn.commit();
                System.out.println("=== THÀNH CÔNG! ĐÃ COMMIT VÀO MYSQL ===");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateSchedules(Connection conn) throws Exception {
        // Tạm thời tắt foreign key checks để truncate thoi_khoa_bieu
        try (Statement st = conn.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS = 0");
            st.execute("TRUNCATE TABLE thoi_khoa_bieu");
            st.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        String insertSql = "INSERT INTO thoi_khoa_bieu (id, ma_mon, ma_lop, ma_gv, ma_phong, thu_trong_tuan, tiet_bat_dau, so_tiet, tiet_ket_thuc, tuan_bat_dau, tuan_ket_thuc, hoc_ky, nam_hoc, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Danh sách phòng học
        String[] ltRooms = {"LT201", "LT202", "LT203", "LT204", "A101", "A102", "A201", "C101", "C102", "HT301"};
        String[] pmRooms = {"PM101", "PM102", "PM103", "PM104", "B201", "B202", "B301", "HT302", "HT303", "HT304"};

        int currentId = 1;
        int totalInserted = 0;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

            // =========================================================================
            // 1. HỌC KỲ 1 - NĂM HỌC 2025-2026 (HỌC KỲ HIỆN TẠI)
            // =========================================================================
            // Đảm bảo ID 1-6 là D24CNTT01 (GV0001, GV0002)
            // ID 7-12 là D24CNTT02 (GV0003, GV0004)
            // ID 13-18 là D23CNTT01 (GV0005, GV0006)
            // để khớp chuẩn 100% với yeu_cau_doi_lich hiện có!

            // 1.1. K24 (10 lớp: D24CNTT01 -> D24CNTT10) - T2, T4, T6
            // Môn: CS101 (Tuần 1-7 Sáng), MA101 (Tuần 10-16 Sáng)
            //      ENG101 (Tuần 1-7 Chiều), MA102 (Tuần 10-16 Chiều)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D24CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0001" : ((c == 1) ? "GV0003" : String.format("GV%04d", 10 + c));
                String gvMorningB2 = (c == 0) ? "GV0002" : ((c == 1) ? "GV0004" : String.format("GV%04d", 20 + c));
                String gvAfternoonB1 = String.format("GV%04d", 30 + c);
                String gvAfternoonB2 = String.format("GV%04d", 40 + c);

                // Morning Block 1 (Tuần 1-7)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS101", maLop, gvMorningB1, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Full sáng T2,4,6 (Tuần 1-7). Nghỉ tuần 8-9.");
                    totalInserted++;
                }
                // Morning Block 2 (Tuần 10-16)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "MA101", maLop, gvMorningB2, ltRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Full sáng T2,4,6 (Tuần 10-16). Sau nghỉ 2 tuần.");
                    totalInserted++;
                }
                // Afternoon Block 1 (Tuần 1-7)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "ENG101", maLop, gvAfternoonB1, pmRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T2,4,6 (Tuần 1-7). Ngoại ngữ thực hành.");
                    totalInserted++;
                }
                // Afternoon Block 2 (Tuần 10-16)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "MA102", maLop, gvAfternoonB2, pmRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T2,4,6 (Tuần 10-16). Đại số.");
                    totalInserted++;
                }
            }

            // 1.2. K23 (10 lớp: D23CNTT01 -> D23CNTT10) - T3, T5, T7
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D23CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0005" : String.format("GV%04d", 50 + c);
                String gvMorningB2 = (c == 0) ? "GV0006" : String.format("GV%04d", 60 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0007" : String.format("GV%04d", 70 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0008" : String.format("GV%04d", 80 + c);

                // Morning Block 1 (Tuần 1-7): MA201 (Toán rời rạc)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "MA201", maLop, gvMorningB1, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T3,5,7 (Tuần 1-7). Toán rời rạc.");
                    totalInserted++;
                }
                // Morning Block 2 (Tuần 10-16): CS204 (Kiến trúc máy tính)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS204", maLop, gvMorningB2, ltRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T3,5,7 (Tuần 10-16). Kiến trúc MT.");
                    totalInserted++;
                }
                // Afternoon Block 1 (Tuần 1-7): CS201 (CTDL & GT)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS201", maLop, gvAfternoonB1, pmRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T3,5,7 (Tuần 1-7). Thực hành CTDL.");
                    totalInserted++;
                }
                // Afternoon Block 2 (Tuần 10-16): CS202 (OOP Java)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS202", maLop, gvAfternoonB2, pmRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T3,5,7 (Tuần 10-16). Lập trình Java.");
                    totalInserted++;
                }
            }

            // 1.3. K22 (10 lớp: D22CNTT01 -> D22CNTT10) - T2, T4, T6 (Morning PM, Afternoon LT)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D22CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0009" : String.format("GV%04d", 90 + c);
                String gvMorningB2 = (c == 0) ? "GV0010" : String.format("GV%04d", 100 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0011" : String.format("GV%04d", 110 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0012" : String.format("GV%04d", 120 + c);

                // Morning Block 1: CS302 (Mạng MT)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS302", maLop, gvMorningB1, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T2,4,6 (Tuần 1-7). Mạng máy tính.");
                    totalInserted++;
                }
                // Morning Block 2: CS304 (Web)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS304", maLop, gvMorningB2, pmRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T2,4,6 (Tuần 10-16). Lập trình Web.");
                    totalInserted++;
                }
                // Afternoon Block 1: CS301 (Hệ điều hành & Linux)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS301", maLop, gvAfternoonB1, ltRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T2,4,6 (Tuần 1-7). Hệ điều hành.");
                    totalInserted++;
                }
                // Afternoon Block 2: CS305 (Hệ QT CSDL)
                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS305", maLop, gvAfternoonB2, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T2,4,6 (Tuần 10-16). Hệ QT CSDL.");
                    totalInserted++;
                }
            }

            // 1.4. K21 (10 lớp: D21CNTT01 -> D21CNTT10) - T3, T5, T7 (Morning PM, Afternoon LT)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D21CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0013" : String.format("GV%04d", 130 + c);
                String gvMorningB2 = (c == 0) ? "GV0014" : String.format("GV%04d", 140 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0015" : String.format("GV%04d", 150 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0016" : String.format("GV%04d", 160 + c);

                // Morning Block 1: CS401 (AI & Học máy)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS401", maLop, gvMorningB1, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T3,5,7 (Tuần 1-7). AI & ML.");
                    totalInserted++;
                }
                // Morning Block 2: CS404 (Big Data)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS404", maLop, gvMorningB2, pmRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T3,5,7 (Tuần 10-16). Big Data.");
                    totalInserted++;
                }
                // Afternoon Block 1: CS402 (An toàn bảo mật)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS402", maLop, gvAfternoonB1, ltRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T3,5,7 (Tuần 1-7). An toàn thông tin.");
                    totalInserted++;
                }
                // Afternoon Block 2: CS403 (Quản lý dự án phần mềm)
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS403", maLop, gvAfternoonB2, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T3,5,7 (Tuần 10-16). Quản lý dự án.");
                    totalInserted++;
                }
            }

            // =========================================================================
            // 2. HỌC KỲ 2 - NĂM HỌC 2024-2025
            // =========================================================================
            for (int c = 0; c < 10; c++) {
                String maLopK23 = String.format("D23CNTT%02d", c + 1);
                String maLopK22 = String.format("D22CNTT%02d", c + 1);
                String maLopK21 = String.format("D21CNTT%02d", c + 1);

                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gv1 = String.format("GV%04d", 161 + c);
                String gv2 = String.format("GV%04d", 171 + c);
                String gv3 = String.format("GV%04d", 181 + c);
                String gv4 = String.format("GV%04d", 191 + c);

                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS102", maLopK23, gv1, pmRoom, thu, 1, 5, 5, 1, 7, "HK2", "2024-2025", "Lập trình C/C++.");
                    addBatch(ps, currentId++, "CS203", maLopK22, gv2, pmRoom, thu, 7, 5, 11, 1, 7, "HK2", "2024-2025", "Cơ sở dữ liệu.");
                    totalInserted += 2;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS303", maLopK21, gv3, ltRoom, thu, 1, 5, 5, 10, 16, "HK2", "2024-2025", "Công nghệ phần mềm.");
                    addBatch(ps, currentId++, "CS309", maLopK21, gv4, ltRoom, thu, 7, 5, 11, 10, 16, "HK2", "2024-2025", "Java Spring Boot.");
                    totalInserted += 2;
                }
            }

            // =========================================================================
            // 3. HỌC KỲ 1 - NĂM HỌC 2024-2025
            // =========================================================================
            for (int c = 0; c < 10; c++) {
                String maLopK23 = String.format("D23CNTT%02d", c + 1);
                String maLopK22 = String.format("D22CNTT%02d", c + 1);
                String maLopK21 = String.format("D21CNTT%02d", c + 1);

                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gv5 = String.format("GV%04d", 201 + c);
                String gv6 = String.format("GV%04d", 211 + c);
                String gv7 = String.format("GV%04d", 221 + c);

                for (int thu : new int[]{2, 4, 6}) {
                    addBatch(ps, currentId++, "CS101", maLopK23, gv5, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2024-2025", "Tin học đại cương.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatch(ps, currentId++, "CS201", maLopK22, gv6, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2024-2025", "Cấu trúc dữ liệu.");
                    addBatch(ps, currentId++, "CS301", maLopK21, gv7, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2024-2025", "Hệ điều hành.");
                    totalInserted += 2;
                }
            }

            ps.executeBatch();
            System.out.println("-> Đã nạp thành công tổng cộng " + totalInserted + " tiết/buổi thời khóa biểu giảng dạy!");
        }
    }

    private static void addBatch(PreparedStatement ps, int id, String maMon, String maLop, String maGv, String maPhong,
                                 int thu, int tietBd, int soTiet, int tietKt, int tuanBd, int tuanKt,
                                 String hocKy, String namHoc, String ghiChu) throws Exception {
        ps.setInt(1, id);
        ps.setString(2, maMon);
        ps.setString(3, maLop);
        ps.setString(4, maGv);
        ps.setString(5, maPhong);
        ps.setInt(6, thu);
        ps.setInt(7, tietBd);
        ps.setInt(8, soTiet);
        ps.setInt(9, tietKt);
        ps.setInt(10, tuanBd);
        ps.setInt(11, tuanKt);
        ps.setString(12, hocKy);
        ps.setString(13, namHoc);
        ps.setString(14, ghiChu);
        ps.addBatch();
    }
}
