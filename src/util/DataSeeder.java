package util;

import connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * DataSeeder: Tự động khởi tạo và nạp dữ liệu quy mô lớn (Mock Data)
 * - 4 Khóa sinh viên (K21 -> K24), 8 lớp học
 * - 12 Phòng học (Lý thuyết, Thực hành, Hội trường)
 * - Khung chương trình đào tạo 8 học kỳ (42 học phần, 130 tín chỉ)
 * - 500 Giảng viên (GV0001 -> GV0500)
 * - 2.000 Sinh viên (SV210001 -> SV240500)
 * - Tài khoản phân quyền: TRUONG_KHOA, BAN_GIAM_HIEU, PHONG_DAO_TAO
 * - Lịch học mẫu (Thời khóa biểu)
 * Tối ưu bằng JDBC executeBatch() và Transaction commit.
 */
public class DataSeeder {

    private static final String DEFAULT_PW_HASH = PasswordUtil.hashPassword("123456");

    private static final String[] HO = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý"};
    private static final String[] DEM = {"Văn", "Thị", "Đức", "Minh", "Quang", "Hải", "Tuấn", "Thành", "Ngọc", "Hữu", "Xuân", "Thanh", "Mạnh", "Hồng", "Đình"};
    private static final String[] TEN = {"An", "Bình", "Cường", "Dũng", "Em", "Phương", "Giang", "Hương", "Huy", "Hùng", "Khánh", "Linh", "Long", "Nam", "Nhung", "Phúc", "Quân", "Quỳnh", "Sơn", "Tâm", "Thảo", "Thắng", "Trang", "Trung", "Tú", "Việt", "Vinh", "Yến"};

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU CHẠY SEEDER DỮ LIỆU ĐÀO TẠO QUY MÔ LỚN ===");
        long start = System.currentTimeMillis();
        try {
            seedAll();
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("=== SEED THÀNH CÔNG TRONG " + elapsed + " ms! ===");
        } catch (Exception e) {
            System.err.println("Lỗi khi chạy Seeder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void seedAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                System.out.println("1. Nạp Danh mục Phòng học...");
                seedRooms(conn);

                System.out.println("2. Nạp Danh mục Khóa & Lớp học...");
                seedClasses(conn);

                System.out.println("3. Nạp Khung CTĐT 8 Học kỳ & Môn học...");
                seedCurriculumAndSubjects(conn);

                System.out.println("4. Nạp Tài khoản Quản trị & Ban Giám Hiệu & Khoa...");
                seedManagementAccounts(conn);

                System.out.println("5. Nạp 500 Giảng viên (executeBatch)...");
                seedLecturers(conn, 500);

                System.out.println("6. Nạp 2.000 Sinh viên (executeBatch)...");
                seedStudents(conn, 2000);

                System.out.println("7. Nạp Lịch học mẫu (Thời khóa biểu)...");
                seedTimetables(conn);

                conn.commit();
                System.out.println("-> Hoàn tất commit toàn bộ dữ liệu vào MySQL!");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static void seedRooms(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM phong_hoc";
        try (PreparedStatement ps = conn.prepareStatement(checkSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) >= 12) {
                System.out.println("   Đã có đủ phòng học, bỏ qua.");
                return;
            }
        }

        String sql = "INSERT IGNORE INTO phong_hoc (ma_phong, ten_phong, toa_nha, suc_chua, loai_phong, trang_thiet_bi, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 4 Phòng máy thực hành
            for (int i = 1; i <= 4; i++) {
                ps.setString(1, "PM10" + i);
                ps.setString(2, "Phòng Máy 10" + i);
                ps.setString(3, "Tòa A");
                ps.setInt(4, 45);
                ps.setString(5, "THUC_HANH");
                ps.setString(6, "45 Máy PC Core i7, Máy chiếu, Điều hòa, LAN GigE");
                ps.setString(7, "DANG_SU_DUNG");
                ps.addBatch();
            }
            // 4 Giảng đường lý thuyết
            for (int i = 1; i <= 4; i++) {
                ps.setString(1, "LT20" + i);
                ps.setString(2, "Giảng Đường LT20" + i);
                ps.setString(3, "Tòa B");
                ps.setInt(4, 90);
                ps.setString(5, "LY_THUYET");
                ps.setString(6, "Máy chiếu Laser, Âm thanh mic không dây, 4 Điều hòa");
                ps.setString(7, "DANG_SU_DUNG");
                ps.addBatch();
            }
            // 4 Hội trường lớn
            for (int i = 1; i <= 4; i++) {
                ps.setString(1, "HT30" + i);
                ps.setString(2, "Hội Trường Lớn HT30" + i);
                ps.setString(3, "Tòa C");
                ps.setInt(4, 250);
                ps.setString(5, "HOI_TRUONG");
                ps.setString(6, "Màn hình LED P2.5, Âm thanh vòm, Hệ thống chiếu sáng sân khấu");
                ps.setString(7, "DANG_SU_DUNG");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedClasses(Connection conn) throws SQLException {
        String sql = "INSERT INTO lop_hoc (ma_lop, ten_lop, si_so, khoa_hoc) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE si_so = VALUES(si_so), ten_lop = VALUES(ten_lop)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String[] cohorts = {"K21", "K22", "K23", "K24"};
            for (String k : cohorts) {
                String kNum = k.substring(1);
                for (int c = 1; c <= 10; c++) {
                    String maLop = "D" + kNum + "CNTT" + String.format("%02d", c);
                    String tenLop = "Đại học CNTT " + c + " - " + k;
                    ps.setString(1, maLop);
                    ps.setString(2, tenLop);
                    ps.setInt(3, 50);
                    ps.setString(4, k);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private static void seedCurriculumAndSubjects(Connection conn) throws SQLException {
        // 42 môn học thuộc 8 học kỳ (4 năm học - CHUẨN ĐÚNG 130 TÍN CHỈ CHO MỖI KHÓA)
        Object[][] subjects = {
            // HK1: 16 TC
            {"CS101", "Tin Học Đại Cương", 3, "LY_THUYET", 1, "Cơ bản"},
            {"MA101", "Giải Tích 1", 3, "LY_THUYET", 1, "Cơ bản"},
            {"MA102", "Đại Số Tuyến Tính", 3, "LY_THUYET", 1, "Cơ bản"},
            {"POL101", "Triết Học Mác - Lênin", 3, "LY_THUYET", 1, "Đại cương"},
            {"ENG101", "Tiếng Anh Cơ Bản 1", 3, "LY_THUYET", 1, "Ngoại ngữ"},
            {"PE101", "Giáo Dục Thể Chất 1", 1, "THUC_HANH", 1, "Thể chất"},

            // HK2: 17 TC
            {"CS102", "Kỹ Thuật Lập Trình C/C++", 4, "THUC_HANH", 2, "Cơ sở ngành"},
            {"MA103", "Giải Tích 2", 3, "LY_THUYET", 2, "Cơ bản"},
            {"PH101", "Vật Lý Đại Cương", 3, "LY_THUYET", 2, "Cơ bản"},
            {"POL102", "Kinh Tế Chính Trị Mác - Lênin", 2, "LY_THUYET", 2, "Đại cương"},
            {"ENG102", "Tiếng Anh Cơ Bản 2", 3, "LY_THUYET", 2, "Ngoại ngữ"},
            {"PE102", "Giáo Dục Thể Chất 2", 1, "THUC_HANH", 2, "Thể chất"},
            {"LAW101", "Pháp Luật Đại Cương", 1, "LY_THUYET", 2, "Đại cương"},

            // HK3: 17 TC
            {"CS201", "Cấu Trúc Dữ Liệu & Giải Thuật", 4, "THUC_HANH", 3, "Cơ sở ngành"},
            {"CS202", "Lập Trình Hướng Đối Tượng Java", 4, "THUC_HANH", 3, "Chuyên ngành"},
            {"MA201", "Toán Rời Rạc", 3, "LY_THUYET", 3, "Cơ sở ngành"},
            {"CS204", "Kiến Trúc Máy Tính & Hợp Ngữ", 3, "LY_THUYET", 3, "Cơ sở ngành"},
            {"POL103", "Chủ Nghĩa Xã Hội Khoa Học", 2, "LY_THUYET", 3, "Đại cương"},
            {"PE103", "Giáo Dục Thể Chất 3", 1, "THUC_HANH", 3, "Thể chất"},

            // HK4: 17 TC
            {"CS203", "Cơ Sở Dữ Liệu & SQL", 4, "THUC_HANH", 4, "Chuyên ngành"},
            {"CS301", "Hệ Điều Hành & Linux", 3, "LY_THUYET", 4, "Chuyên ngành"},
            {"MA202", "Xác Suất Thống Kê", 3, "LY_THUYET", 4, "Cơ bản"},
            {"CS205", "Thiết Kế Web Cơ Bản", 3, "THUC_HANH", 4, "Chuyên ngành"},
            {"POL104", "Lịch Sử Đảng Cộng Sản Việt Nam", 2, "LY_THUYET", 4, "Đại cương"},
            {"POL105", "Tư Tưởng Hồ Chí Minh", 2, "LY_THUYET", 4, "Đại cương"},

            // HK5: 17 TC
            {"CS302", "Mạng Máy Tính & Viễn Thông", 3, "THUC_HANH", 5, "Chuyên ngành"},
            {"CS304", "Phát Triển Ứng Dụng Web", 4, "THUC_HANH", 5, "Chuyên ngành"},
            {"CS305", "Hệ Quản Trị Cơ Sở Dữ Liệu", 3, "THUC_HANH", 5, "Chuyên ngành"},
            {"CS306", "Phân Tích & Thiết Kế Hệ Thống", 3, "LY_THUYET", 5, "Chuyên ngành"},
            {"CS307", "Lập Trình Ứng Dụng Di Động", 4, "THUC_HANH", 5, "Chuyên ngành"},

            // HK6: 17 TC
            {"CS303", "Công Nghệ Phần Mềm Hiện Đại", 3, "LY_THUYET", 6, "Chuyên ngành"},
            {"CS308", "Kiểm Thử Phần Mềm & QA", 3, "THUC_HANH", 6, "Chuyên ngành"},
            {"CS401", "Trí Tuệ Nhân Tạo & Học Máy", 3, "THUC_HANH", 6, "Chuyên ngành nâng cao"},
            {"CS309", "Lập Trình Java Nâng Cao & Spring Boot", 4, "THUC_HANH", 6, "Chuyên ngành nâng cao"},
            {"CS310", "Điện Toán Đám Mây & DevOps", 4, "THUC_HANH", 6, "Chuyên ngành nâng cao"},

            // HK7: 17 TC
            {"CS402", "An Toàn & Bảo Mật Hệ Thống", 3, "LY_THUYET", 7, "Chuyên ngành nâng cao"},
            {"CS403", "Quản Lý Dự Án Phần Mềm", 3, "LY_THUYET", 7, "Bổ trợ nghề nghiệp"},
            {"CS404", "Xử Lý Dữ Liệu Lớn (Big Data)", 3, "THUC_HANH", 7, "Chuyên ngành nâng cao"},
            {"CS405", "Internet Vạn Vật (IoT) & Ứng Dụng", 3, "THUC_HANH", 7, "Chuyên ngành nâng cao"},
            {"CS406", "Thực Tập Doanh Nghiệp", 5, "THUC_HANH", 7, "Thực tập"},

            // HK8: 12 TC
            {"CS407", "Chuyên Đề Công Nghệ Mới", 2, "LY_THUYET", 8, "Bổ trợ nghề nghiệp"},
            {"CS499", "Đồ Án Tốt Nghiệp Kỹ Sư", 10, "THUC_HANH", 8, "Tốt nghiệp"}
        };

        String insertMonHoc = "INSERT INTO mon_hoc (ma_mon, ten_mon, so_tin_chi, loai_mon) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE ten_mon = VALUES(ten_mon), so_tin_chi = VALUES(so_tin_chi), loai_mon = VALUES(loai_mon)";
        try (PreparedStatement ps = conn.prepareStatement(insertMonHoc)) {
            for (Object[] s : subjects) {
                ps.setString(1, (String) s[0]);
                ps.setString(2, (String) s[1]);
                ps.setInt(3, (Integer) s[2]);
                ps.setString(4, (String) s[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Làm mới chuong_trinh_dao_tao để chuẩn hóa đúng 130 tín chỉ cho cả 4 khóa K21, K22, K23, K24
        try (PreparedStatement psClear = conn.prepareStatement("DELETE FROM chuong_trinh_dao_tao")) {
            psClear.executeUpdate();
        }

        String insertCTDT = "INSERT INTO chuong_trinh_dao_tao (khoa, hoc_ky, ma_mon, ten_mon, so_tin_chi, loai_mon, khoa_hoc) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertCTDT)) {
            String[] cohorts = {"K21", "K22", "K23", "K24"};
            for (String k : cohorts) {
                for (Object[] s : subjects) {
                    ps.setString(1, "Công Nghệ Thông Tin");
                    ps.setInt(2, (Integer) s[4]);
                    ps.setString(3, (String) s[0]);
                    ps.setString(4, (String) s[1]);
                    ps.setInt(5, (Integer) s[2]);
                    ps.setString(6, (String) s[3]);
                    ps.setString(7, k);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private static void seedManagementAccounts(Connection conn) throws SQLException {
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, trang_thai) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE ho_ten = VALUES(ho_ten), vai_tro = VALUES(vai_tro), mat_khau = VALUES(mat_khau)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1. Admin
            ps.setString(1, "admin");
            ps.setString(2, PasswordUtil.hashPassword("admin123"));
            ps.setString(3, "Quản Trị Viên Hệ Thống");
            ps.setString(4, "admin@university.edu.vn");
            ps.setString(5, "ADMIN");
            ps.addBatch();

            // 2. Ban Giám Hiệu
            ps.setString(1, "bangiamhieu");
            ps.setString(2, DEFAULT_PW_HASH);
            ps.setString(3, "GS.TS. Trần Văn Hiệu (Hiệu Trưởng)");
            ps.setString(4, "bgh@university.edu.vn");
            ps.setString(5, "BAN_GIAM_HIEU");
            ps.addBatch();

            // 3. Trưởng khoa
            ps.setString(1, "truongkhoa");
            ps.setString(2, DEFAULT_PW_HASH);
            ps.setString(3, "PGS.TS. Lê Đình Khoa (Trưởng Khoa CNTT)");
            ps.setString(4, "truongkhoa.cntt@university.edu.vn");
            ps.setString(5, "TRUONG_KHOA");
            ps.addBatch();

            // 4. Phòng Đào Tạo
            ps.setString(1, "daotao");
            ps.setString(2, DEFAULT_PW_HASH);
            ps.setString(3, "ThS. Hoàng Minh Đào Tạo (Cán Bộ Đào Tạo)");
            ps.setString(4, "phongdaotao@university.edu.vn");
            ps.setString(5, "PHONG_DAO_TAO");
            ps.addBatch();

            // 5. Giảng Viên
            ps.setString(1, "giangvien");
            ps.setString(2, DEFAULT_PW_HASH);
            ps.setString(3, "TS. Nguyễn Văn An (Giảng Viên Bộ Môn)");
            ps.setString(4, "annv@university.edu.vn");
            ps.setString(5, "GIANG_VIEN");
            ps.addBatch();

            // 6. Sinh Viên
            ps.setString(1, "sinhvien");
            ps.setString(2, DEFAULT_PW_HASH);
            ps.setString(3, "Đỗ Xuân Hùng (Sinh Viên K21)");
            ps.setString(4, "sv210001@student.university.edu.vn");
            ps.setString(5, "SINH_VIEN");
            ps.addBatch();

            ps.executeBatch();
        }
    }

    private static void seedLecturers(Connection conn, int count) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM giang_vien");
             ResultSet rs = check.executeQuery()) {
            if (rs.next() && rs.getInt(1) >= count) {
                System.out.println("   Đã có đủ " + rs.getInt(1) + " giảng viên trong bảng giang_vien, bỏ qua.");
                return;
            }
        }

        String insertGv = "INSERT IGNORE INTO giang_vien (ma_gv, ho_ten, khoa_bo_mon, email, so_dien_thoai) VALUES (?, ?, ?, ?, ?)";
        Random rnd = new Random(42);

        try (PreparedStatement psGv = conn.prepareStatement(insertGv)) {
            for (int i = 1; i <= count; i++) {
                String maGv = String.format("GV%04d", i);
                String hoTen = HO[rnd.nextInt(HO.length)] + " " + DEM[rnd.nextInt(DEM.length)] + " " + TEN[rnd.nextInt(TEN.length)];
                String email = "gv" + String.format("%04d", i) + "@university.edu.vn";
                String sdt = "098" + String.format("%07d", 1000000 + i);

                psGv.setString(1, maGv);
                psGv.setString(2, hoTen);
                psGv.setString(3, "Khoa Công Nghệ Thông Tin");
                psGv.setString(4, email);
                psGv.setString(5, sdt);
                psGv.addBatch();

                if (i % 250 == 0) {
                    psGv.executeBatch();
                }
            }
            psGv.executeBatch();
        }
    }

    private static void seedStudents(Connection conn, int totalStudents) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM sinh_vien");
             ResultSet rs = check.executeQuery()) {
            if (rs.next() && rs.getInt(1) >= totalStudents) {
                System.out.println("   Đã có đủ " + rs.getInt(1) + " hồ sơ sinh viên trong bảng sinh_vien, bỏ qua.");
                return;
            }
        }

        String insertSv = "INSERT IGNORE INTO sinh_vien (ma_sv, ho_ten, email, so_dien_thoai, gioi_tinh, ngay_sinh, ma_lop, khoa_hoc, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'DANG_HOC')";
        Random rnd = new Random(101);

        String[] cohorts = {"21", "22", "23", "24"};
        int perCohort = totalStudents / cohorts.length;

        try (PreparedStatement psSv = conn.prepareStatement(insertSv)) {
            int counter = 0;
            for (String k : cohorts) {
                for (int i = 1; i <= perCohort; i++) {
                    String maSv = "SV" + k + String.format("%04d", i);
                    String hoTen = HO[rnd.nextInt(HO.length)] + " " + DEM[rnd.nextInt(DEM.length)] + " " + TEN[rnd.nextInt(TEN.length)];
                    String email = maSv.toLowerCase() + "@student.university.edu.vn";
                    String sdt = "038" + String.format("%07d", 1000000 + counter);
                    String gioiTinh = hoTen.contains("Thị") ? "Nu" : "Nam";
                    String ngaySinh = "200" + (3 + (Integer.parseInt(k) - 21)) + "-09-15";
                    
                    // Phân bổ chính xác 50 sinh viên vào mỗi lớp trong 10 lớp
                    int classNum = ((i - 1) / 50) + 1;
                    String maLop = "D" + k + "CNTT" + String.format("%02d", classNum);

                    psSv.setString(1, maSv);
                    psSv.setString(2, hoTen);
                    psSv.setString(3, email);
                    psSv.setString(4, sdt);
                    psSv.setString(5, gioiTinh);
                    psSv.setDate(6, java.sql.Date.valueOf(ngaySinh));
                    psSv.setString(7, maLop);
                    psSv.setString(8, "K" + k);
                    psSv.addBatch();

                    counter++;

                    if (counter % 500 == 0) {
                        psSv.executeBatch();
                    }
                }
            }
            psSv.executeBatch();
        }
    }

    private static void seedTimetables(Connection conn) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM thoi_khoa_bieu");
             ResultSet rs = check.executeQuery()) {
            if (rs.next() && rs.getInt(1) >= 200) {
                System.out.println("   Đã có sẵn đầy đủ thời khóa biểu giảng dạy (" + rs.getInt(1) + " bản ghi), bỏ qua.");
                return;
            }
        }

        try (java.sql.Statement st = conn.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS = 0");
            st.execute("TRUNCATE TABLE thoi_khoa_bieu");
            st.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        String insertSql = "INSERT INTO thoi_khoa_bieu (id, ma_mon, ma_lop, ma_gv, ma_phong, thu_trong_tuan, tiet_bat_dau, so_tiet, tiet_ket_thuc, tuan_bat_dau, tuan_ket_thuc, hoc_ky, nam_hoc, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[] ltRooms = {"LT201", "LT202", "LT203", "LT204", "A101", "A102", "A201", "C101", "C102", "HT301"};
        String[] pmRooms = {"PM101", "PM102", "PM103", "PM104", "B201", "B202", "B301", "HT302", "HT303", "HT304"};

        int currentId = 1;
        int totalInserted = 0;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

            // 1. HK1 2025-2026: K24 (10 lớp)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D24CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0001" : ((c == 1) ? "GV0003" : String.format("GV%04d", 10 + c));
                String gvMorningB2 = (c == 0) ? "GV0002" : ((c == 1) ? "GV0004" : String.format("GV%04d", 20 + c));
                String gvAfternoonB1 = String.format("GV%04d", 30 + c);
                String gvAfternoonB2 = String.format("GV%04d", 40 + c);

                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "CS101", maLop, gvMorningB1, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Full sáng T2,4,6 (Tuần 1-7). Nghỉ tuần 8-9.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "MA101", maLop, gvMorningB2, ltRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Full sáng T2,4,6 (Tuần 10-16). Sau nghỉ 2 tuần.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "ENG101", maLop, gvAfternoonB1, pmRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T2,4,6 (Tuần 1-7). Ngoại ngữ.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "MA102", maLop, gvAfternoonB2, pmRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T2,4,6 (Tuần 10-16). Đại số tuyến tính.");
                    totalInserted++;
                }
            }

            // 2. HK1 2025-2026: K23 (10 lớp)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D23CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0005" : String.format("GV%04d", 50 + c);
                String gvMorningB2 = (c == 0) ? "GV0006" : String.format("GV%04d", 60 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0007" : String.format("GV%04d", 70 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0008" : String.format("GV%04d", 80 + c);

                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "MA201", maLop, gvMorningB1, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T3,5,7 (Tuần 1-7). Toán rời rạc.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS204", maLop, gvMorningB2, ltRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T3,5,7 (Tuần 10-16). Kiến trúc máy tính.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS201", maLop, gvAfternoonB1, pmRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T3,5,7 (Tuần 1-7). Thực hành CTDL & GT.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS202", maLop, gvAfternoonB2, pmRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T3,5,7 (Tuần 10-16). Lập trình OOP Java.");
                    totalInserted++;
                }
            }

            // 3. HK1 2025-2026: K22 (10 lớp)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D22CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0009" : String.format("GV%04d", 90 + c);
                String gvMorningB2 = (c == 0) ? "GV0010" : String.format("GV%04d", 100 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0011" : String.format("GV%04d", 110 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0012" : String.format("GV%04d", 120 + c);

                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "CS302", maLop, gvMorningB1, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T2,4,6 (Tuần 1-7). Mạng máy tính.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "CS304", maLop, gvMorningB2, pmRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T2,4,6 (Tuần 10-16). Lập trình Web.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "CS301", maLop, gvAfternoonB1, ltRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T2,4,6 (Tuần 1-7). Hệ điều hành.");
                    totalInserted++;
                }
                for (int thu : new int[]{2, 4, 6}) {
                    addBatchTkb(ps, currentId++, "CS305", maLop, gvAfternoonB2, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T2,4,6 (Tuần 10-16). Hệ QT CSDL.");
                    totalInserted++;
                }
            }

            // 4. HK1 2025-2026: K21 (10 lớp)
            for (int c = 0; c < 10; c++) {
                String maLop = String.format("D21CNTT%02d", c + 1);
                String ltRoom = ltRooms[c % ltRooms.length];
                String pmRoom = pmRooms[c % pmRooms.length];

                String gvMorningB1 = (c == 0) ? "GV0013" : String.format("GV%04d", 130 + c);
                String gvMorningB2 = (c == 0) ? "GV0014" : String.format("GV%04d", 140 + c);
                String gvAfternoonB1 = (c == 1) ? "GV0015" : String.format("GV%04d", 150 + c);
                String gvAfternoonB2 = (c == 1) ? "GV0016" : String.format("GV%04d", 160 + c);

                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS401", maLop, gvMorningB1, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2025-2026", "Block 1: Sáng T3,5,7 (Tuần 1-7). AI & Machine Learning.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS404", maLop, gvMorningB2, pmRoom, thu, 1, 5, 5, 10, 16, "HK1", "2025-2026", "Block 2: Sáng T3,5,7 (Tuần 10-16). Big Data.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS402", maLop, gvAfternoonB1, ltRoom, thu, 7, 5, 11, 1, 7, "HK1", "2025-2026", "Block 1: Chiều T3,5,7 (Tuần 1-7). An toàn hệ thống.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS403", maLop, gvAfternoonB2, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2025-2026", "Block 2: Chiều T3,5,7 (Tuần 10-16). Quản lý dự án PM.");
                    totalInserted++;
                }
            }

            // 5. HK2 2024-2025: Bổ sung cho GV0161 -> GV0200
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
                    addBatchTkb(ps, currentId++, "CS102", maLopK23, gv1, pmRoom, thu, 1, 5, 5, 1, 7, "HK2", "2024-2025", "Lập trình C/C++.");
                    addBatchTkb(ps, currentId++, "CS203", maLopK22, gv2, pmRoom, thu, 7, 5, 11, 1, 7, "HK2", "2024-2025", "Cơ sở dữ liệu.");
                    totalInserted += 2;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS303", maLopK21, gv3, ltRoom, thu, 1, 5, 5, 10, 16, "HK2", "2024-2025", "Công nghệ phần mềm.");
                    addBatchTkb(ps, currentId++, "CS309", maLopK21, gv4, ltRoom, thu, 7, 5, 11, 10, 16, "HK2", "2024-2025", "Java Spring Boot.");
                    totalInserted += 2;
                }
            }

            // 6. HK1 2024-2025: Bổ sung cho GV0201 -> GV0230
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
                    addBatchTkb(ps, currentId++, "CS101", maLopK23, gv5, ltRoom, thu, 1, 5, 5, 1, 7, "HK1", "2024-2025", "Tin học đại cương.");
                    totalInserted++;
                }
                for (int thu : new int[]{3, 5, 7}) {
                    addBatchTkb(ps, currentId++, "CS201", maLopK22, gv6, pmRoom, thu, 1, 5, 5, 1, 7, "HK1", "2024-2025", "Cấu trúc dữ liệu.");
                    addBatchTkb(ps, currentId++, "CS301", maLopK21, gv7, ltRoom, thu, 7, 5, 11, 10, 16, "HK1", "2024-2025", "Hệ điều hành.");
                    totalInserted += 2;
                }
            }

            ps.executeBatch();
            System.out.println("   Đã nạp thành công " + totalInserted + " lịch giảng dạy cho giảng viên (40 lớp, không trùng lặp)!");
        }

        // Cập nhật ma_tkb trong yeu_cau_doi_lich để khớp với các giảng viên đề xuất
        try (java.sql.Statement st = conn.createStatement()) {
            st.executeUpdate("UPDATE yeu_cau_doi_lich SET ma_tkb = 1 WHERE id = 1");
            st.executeUpdate("UPDATE yeu_cau_doi_lich SET ma_tkb = 13 WHERE id = 2");
            st.executeUpdate("UPDATE yeu_cau_doi_lich SET ma_tkb = 121 WHERE id = 3");
        }
    }

    private static void addBatchTkb(PreparedStatement ps, int id, String maMon, String maLop, String maGv, String maPhong,
                                    int thu, int tietBd, int soTiet, int tietKt, int tuanBd, int tuanKt,
                                    String hocKy, String namHoc, String ghiChu) throws SQLException {
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
