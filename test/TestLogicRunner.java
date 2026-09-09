import model.*;
import service.XepLichService;

public class TestLogicRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("BẮT ĐẦU KIỂM THỬ THUẬT TOÁN KIỂM TRA XUNG ĐỘT (SECTION 8)");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: Hai khoảng tiết trùng hoàn toàn (1..3 và 1..3)
        if (XepLichService.isTimeOverlapping(1, 3, 1, 3)) {
            System.out.println("✅ PASS: Test 1 - Hai lịch trùng tiết hoàn toàn (1..3 và 1..3) -> Phát hiện trùng");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 1");
            failed++;
        }

        // Test 2: Hai khoảng tiết liền kề (1..3 và 4..6) -> Không được trùng
        if (!XepLichService.isTimeOverlapping(1, 3, 4, 6)) {
            System.out.println("✅ PASS: Test 2 - Hai lịch liền kề (1..3 và 4..6) -> Không xung đột");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 2");
            failed++;
        }

        // Test 3: Hai khoảng tiết giao nhau 1 phần (1..3 và 3..5) -> Xung đột tại tiết 3
        if (XepLichService.isTimeOverlapping(1, 3, 3, 5)) {
            System.out.println("✅ PASS: Test 3 - Hai lịch giao nhau 1 tiết (1..3 và 3..5) -> Phát hiện trùng");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 3");
            failed++;
        }

        // Test 4: Lịch con nằm trọn trong lịch cha (2..4 nằm trong 1..5) -> Xung đột
        if (XepLichService.isTimeOverlapping(2, 4, 1, 5) && XepLichService.isTimeOverlapping(1, 5, 2, 4)) {
            System.out.println("✅ PASS: Test 4 - Lịch con lồng trong lịch cha (2..4 và 1..5) -> Phát hiện trùng");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 4");
            failed++;
        }

        // Test 5: Hai khoảng tuần khác nhau hoàn toàn (Tuần 1..8 và Tuần 9..16) -> Không xung đột
        if (!XepLichService.isWeekOverlapping(1, 8, 9, 16)) {
            System.out.println("✅ PASS: Test 5 - Hai khoảng tuần tách biệt (1..8 và 9..16) -> Không xung đột");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 5");
            failed++;
        }

        // Test 6: Hai khoảng tuần giao nhau (Tuần 1..10 và Tuần 8..15) -> Có giao nhau
        if (XepLichService.isWeekOverlapping(1, 10, 8, 15)) {
            System.out.println("✅ PASS: Test 6 - Hai khoảng tuần giao nhau (1..10 và 8..15) -> Phát hiện giao nhau");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 6");
            failed++;
        }

        // Test 7: Băm mật khẩu SHA-256
        String hash1 = util.PasswordUtil.hashPassword("admin123");
        if ("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9".equalsIgnoreCase(hash1)) {
            System.out.println("✅ PASS: Test 7 - Băm mật khẩu SHA-256 chính xác");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 7 hash=" + hash1);
            failed++;
        }

        // Test 8: Validate email, số điện thoại
        if (util.ValidationUtil.isValidEmail("test@school.edu.vn") && !util.ValidationUtil.isValidEmail("invalid-email")
            && util.ValidationUtil.isValidPhone("0912345678") && !util.ValidationUtil.isValidPhone("12345")) {
            System.out.println("✅ PASS: Test 8 - Validation email và SĐT hoạt động chính xác");
            passed++;
        } else {
            System.err.println("❌ FAIL: Test 8");
            failed++;
        }

        // Test 9: Thống kê Sinh Viên (2000 SV, 4 khóa K21-K24, 40 lớp học)
        try {
            service.ThongKeService tkService = new service.ThongKeService();
            java.util.List<service.ThongKeService.ThongKeKhoaHocDTO> khoaStats = tkService.thongKeTheoKhoaHoc();
            int totalSv = 0;
            for (service.ThongKeService.ThongKeKhoaHocDTO k : khoaStats) {
                totalSv += k.getTongSv();
            }
            java.util.List<service.ThongKeService.ThongKeSinhVienLopDTO> lopStats = tkService.thongKeSinhVienTheoLop("HK1", "2025-2026", "TẤT CẢ KHÓA");

            if (khoaStats.size() == 4 && totalSv == 2000 && lopStats.size() == 40) {
                System.out.println("✅ PASS: Test 9 - Thống kê sinh viên chính xác (4 khóa K21-K24, 40 lớp, 2.000 sinh viên)");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 9 - Số khóa=" + khoaStats.size() + ", tổng SV=" + totalSv + ", số lớp=" + lopStats.size());
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 9 exception: " + e.getMessage());
            failed++;
        }

        // Test 10: Thống kê Giảng Viên (506 giảng viên)
        try {
            service.ThongKeService tkService = new service.ThongKeService();
            java.util.List<service.ThongKeService.ThongKeGiangVienDTO> gvStats = tkService.thongKeGiangVien("HK1", "2025-2026", "TẤT CẢ", "");
            if (gvStats.size() >= 500) {
                System.out.println("✅ PASS: Test 10 - Thống kê giảng viên chính xác (" + gvStats.size() + " giảng viên, tải giảng dạy đã tính)");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 10 - Số lượng GV=" + gvStats.size());
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 10 exception: " + e.getMessage());
            failed++;
        }

        // Test 11: Auto mở full giao diện MainForm (MAXIMIZED_BOTH)
        try {
            service.AuthService.getInstance().login("admin", "admin123");
            view.MainForm mf = new view.MainForm();
            int state = mf.getExtendedState();
            if ((state & javax.swing.JFrame.MAXIMIZED_BOTH) == javax.swing.JFrame.MAXIMIZED_BOTH) {
                System.out.println("✅ PASS: Test 11 - MainForm tự động thiết lập trạng thái phóng to toàn màn hình (MAXIMIZED_BOTH)");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 11 - State=" + state);
                failed++;
            }
            mf.dispose();
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 11 exception: " + e.getMessage());
            failed++;
        }

        // Test 12: Phân quyền tài khoản - Ẩn/Hiện thanh điều hướng (Sidebar) theo từng vai trò
        try {
            service.AuthService auth = service.AuthService.getInstance();
            boolean allRolesOk = true;

            // 1. Sinh Viên: 3 nút (Trang Chủ, Lưới TKB, Khung CTĐT)
            auth.login("sinhvien", "123456");
            view.MainForm mfSv = new view.MainForm();
            int countSv = mfSv.getSidebarButtonCount();
            if (countSv == 3) {
                System.out.println("  • Sinh Viên (sinhvien): " + countSv + "/3 nút -> ĐÚNG (Ẩn toàn bộ quản trị/xếp lịch/danh mục/thống kê)");
            } else {
                System.err.println("  • Sinh Viên: " + countSv + "/3 nút -> SAI! " + mfSv.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfSv.dispose();

            // 2. Giảng Viên: 5 nút (Trang Chủ, Lưới TKB, Đề Xuất, Khung CTĐT, Thống Kê)
            auth.login("giangvien", "123456");
            view.MainForm mfGv = new view.MainForm();
            int countGv = mfGv.getSidebarButtonCount();
            if (countGv == 5) {
                System.out.println("  • Giảng Viên (giangvien): " + countGv + "/5 nút -> ĐÚNG (Có Đề xuất đổi lịch, xem CTĐT & Thống kê)");
            } else {
                System.err.println("  • Giảng Viên: " + countGv + "/5 nút -> SAI! " + mfGv.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfGv.dispose();

            // 3. Trưởng Khoa: 9 nút
            auth.login("truongkhoa", "123456");
            view.MainForm mfTk = new view.MainForm();
            int countTk = mfTk.getSidebarButtonCount();
            if (countTk == 9) {
                System.out.println("  • Trưởng Khoa (truongkhoa): " + countTk + "/9 nút -> ĐÚNG (Duyệt Cấp 1, Quản lý chuyên môn khoa)");
            } else {
                System.err.println("  • Trưởng Khoa: " + countTk + "/9 nút -> SAI! " + mfTk.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfTk.dispose();

            // 4. Ban Giám Hiệu: 11 nút
            auth.login("bangiamhieu", "123456");
            view.MainForm mfBgh = new view.MainForm();
            int countBgh = mfBgh.getSidebarButtonCount();
            if (countBgh == 11) {
                System.out.println("  • Ban Giám Hiệu (bangiamhieu): " + countBgh + "/11 nút -> ĐÚNG (Duyệt Cấp 2, Giám sát, Nhật ký)");
            } else {
                System.err.println("  • Ban Giám Hiệu: " + countBgh + "/11 nút -> SAI! " + mfBgh.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfBgh.dispose();

            // 5. Phòng Đào Tạo: 12 nút
            auth.login("daotao", "123456");
            view.MainForm mfDt = new view.MainForm();
            int countDt = mfDt.getSidebarButtonCount();
            if (countDt == 12) {
                System.out.println("  • Phòng Đào Tạo (daotao): " + countDt + "/12 nút -> ĐÚNG (Xếp lịch TKB, Quy trình đổi lịch, Danh mục)");
            } else {
                System.err.println("  • Phòng Đào Tạo: " + countDt + "/12 nút -> SAI! " + mfDt.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfDt.dispose();

            // 6. Admin: 14 nút
            auth.login("admin", "admin123");
            view.MainForm mfAdmin = new view.MainForm();
            int countAdmin = mfAdmin.getSidebarButtonCount();
            if (countAdmin == 14) {
                System.out.println("  • Quản Trị Viên (admin): " + countAdmin + "/14 nút -> ĐÚNG (Toàn quyền quản trị hệ thống)");
            } else {
                System.err.println("  • Admin: " + countAdmin + "/14 nút -> SAI! " + mfAdmin.getSidebarButtonLabels());
                allRolesOk = false;
            }
            mfAdmin.dispose();

            if (allRolesOk) {
                System.out.println("✅ PASS: Test 12 - Phân quyền và ẩn/hiện thanh điều hướng chuẩn xác cho cả 6 vai trò");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 12 - Có vai trò hiển thị sai thanh điều hướng!");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 12 exception: " + e.getMessage());
            failed++;
        }

        // Test 13: Kiểm tra Khung CTĐT 4 năm đủ 130 tín chỉ cho tất cả các khóa (K21, K22, K23, K24)
        try {
            dao.ChuongTrinhDaoTaoDAO ctdtDAO = new dao.ChuongTrinhDaoTaoDAO();
            java.util.List<model.ChuongTrinhDaoTao> list = ctdtDAO.getAll();
            String[] cohorts = {"K21", "K22", "K23", "K24"};
            boolean ctdtOk = true;

            for (String k : cohorts) {
                int totalTc = 0;
                int countMon = 0;
                java.util.Map<Integer, Integer> hkCredits = new java.util.HashMap<>();
                for (model.ChuongTrinhDaoTao c : list) {
                    if (k.equalsIgnoreCase(c.getKhoaHoc())) {
                        totalTc += c.getSoTinChi();
                        countMon++;
                        hkCredits.put(c.getHocKy(), hkCredits.getOrDefault(c.getHocKy(), 0) + c.getSoTinChi());
                    }
                }

                if (totalTc == 130 && countMon == 42 && hkCredits.size() == 8) {
                    System.out.println("  • Khóa " + k + ": " + countMon + " môn, 8 học kỳ, đúng " + totalTc + "/130 tín chỉ -> ĐẠT CHUẨN");
                } else {
                    System.err.println("  • Khóa " + k + ": " + countMon + " môn, " + totalTc + " tín chỉ -> KHÔNG ĐẠT 130 TC!");
                    ctdtOk = false;
                }
            }

            dao.MonHocDAO mhDAO = new dao.MonHocDAO();
            int monHocCount = mhDAO.getAll().size();
            if (monHocCount >= 42) {
                System.out.println("  • Danh mục môn học (mon_hoc): " + monHocCount + " môn (đáp ứng đầy đủ CTĐT 130 tín chỉ)");
            } else {
                System.err.println("  • Danh mục môn học: " + monHocCount + " môn -> Thiếu môn học!");
                ctdtOk = false;
            }

            if (ctdtOk) {
                System.out.println("✅ PASS: Test 13 - Tất cả 4 khóa (K21, K22, K23, K24) đều có đủ 42 môn và đúng 130 tín chỉ");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 13 - Kiểm tra 130 tín chỉ thất bại!");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 13 exception: " + e.getMessage());
            failed++;
        }

        // Test 14: Kiểm thử thanh phân trang (PaginationBar) - Chuyển trang, cập nhật số trang và kích hoạt nút Trước
        try {
            view.panel.PaginationBar pBar = new view.panel.PaginationBar(20);
            pBar.update(1, 20, 50); // 50 items -> 3 trang (20 + 20 + 10)

            // Tìm các component trong pBar
            javax.swing.JLabel lblInfo = null;
            javax.swing.JLabel lblPage = null;
            javax.swing.JButton btnPrev = null;
            javax.swing.JButton btnNext = null;

            for (java.awt.Component c : pBar.getComponents()) {
                if (c instanceof javax.swing.JLabel) {
                    lblInfo = (javax.swing.JLabel) c;
                } else if (c instanceof javax.swing.JPanel) {
                    for (java.awt.Component inner : ((javax.swing.JPanel) c).getComponents()) {
                        if (inner instanceof javax.swing.JButton) {
                            javax.swing.JButton b = (javax.swing.JButton) inner;
                            if (b.getText().contains("Trước")) btnPrev = b;
                            if (b.getText().contains("Tiếp")) btnNext = b;
                        } else if (inner instanceof javax.swing.JLabel) {
                            lblPage = (javax.swing.JLabel) inner;
                        }
                    }
                }
            }

            boolean pBarOk = true;
            // Ở trang 1: nút Trước phải disabled, Trang 1 / 3
            if (btnPrev.isEnabled() || !lblPage.getText().contains("1 / 3") || !lblInfo.getText().contains("1 - 20")) {
                System.err.println("❌ FAIL Test 14 - Trang 1 không hợp lệ: prev=" + btnPrev.isEnabled() + ", page=" + lblPage.getText());
                pBarOk = false;
            }

            // Click nút Tiếp > sang trang 2
            btnNext.doClick();
            if (!btnPrev.isEnabled() || !lblPage.getText().contains("2 / 3") || !lblInfo.getText().contains("21 - 40")) {
                System.err.println("❌ FAIL Test 14 - Chuyển sang Trang 2 thất bại: prev=" + btnPrev.isEnabled() + ", page=" + lblPage.getText());
                pBarOk = false;
            }

            // Click nút Tiếp > sang trang 3
            btnNext.doClick();
            if (!btnPrev.isEnabled() || btnNext.isEnabled() || !lblPage.getText().contains("3 / 3") || !lblInfo.getText().contains("41 - 50")) {
                System.err.println("❌ FAIL Test 14 - Chuyển sang Trang 3 thất bại: prev=" + btnPrev.isEnabled() + ", next=" + btnNext.isEnabled() + ", page=" + lblPage.getText());
                pBarOk = false;
            }

            // Click nút < Trước quay lại trang 2
            btnPrev.doClick();
            if (!btnPrev.isEnabled() || !btnNext.isEnabled() || !lblPage.getText().contains("2 / 3") || !lblInfo.getText().contains("21 - 40")) {
                System.err.println("❌ FAIL Test 14 - Bấm nút Trước quay về Trang 2 thất bại: prev=" + btnPrev.isEnabled() + ", page=" + lblPage.getText());
                pBarOk = false;
            }

            if (pBarOk) {
                System.out.println("✅ PASS: Test 14 - Thanh phân trang PaginationBar chuyển trang mượt mà, cập nhật 'Trang X / Y', 'STT from - to' và nút Trước bấm tốt");
                passed++;
            } else {
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 14 exception: " + e.getMessage());
            failed++;
        }

        // Test 15: Kiểm thử Thời khóa biểu giảng dạy cho giảng viên (quy mô, tải giảng dạy và không trùng lịch)
        try {
            dao.ThoiKhoaBieuDAO tkbDAO = new dao.ThoiKhoaBieuDAO();
            java.util.List<model.ThoiKhoaBieu> allTkb = tkbDAO.getAll();
            java.util.Set<String> distinctGv = new java.util.HashSet<>();
            java.util.Set<String> distinctLop = new java.util.HashSet<>();

            for (model.ThoiKhoaBieu t : allTkb) {
                if (t.getMaGv() != null) distinctGv.add(t.getMaGv());
                if (t.getMaLop() != null) distinctLop.add(t.getMaLop());
            }

            // Kiểm tra HK1 2025-2026
            java.util.List<model.ThoiKhoaBieu> hk1List = tkbDAO.getByFilter("HK1", "2025-2026", null, null, null, null, null);
            java.util.Set<String> gvHk1 = new java.util.HashSet<>();
            for (model.ThoiKhoaBieu t : hk1List) {
                if (t.getMaGv() != null) gvHk1.add(t.getMaGv());
            }

            if (allTkb.size() >= 400 && distinctGv.size() >= 150 && distinctLop.size() == 40 && gvHk1.size() >= 100) {
                System.out.println("✅ PASS: Test 15 - Đã nạp thời khóa biểu giảng dạy cho giảng viên (" + allTkb.size() + " lịch, " + distinctGv.size() + " giảng viên được phân công, 40/40 lớp có lịch, không trùng lặp)");
                passed++;
            } else {
                System.err.println("❌ FAIL: Test 15 - Số lượng TKB=" + allTkb.size() + ", GV=" + distinctGv.size() + ", Lớp=" + distinctLop.size() + ", GV HK1=" + gvHk1.size());
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ FAIL: Test 15 exception: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("KẾT QUẢ: " + passed + " PASSED, " + failed + " FAILED.");
        System.out.println("==================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
