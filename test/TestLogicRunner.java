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

        System.out.println("==================================================");
        System.out.println("KẾT QUẢ: " + passed + " PASSED, " + failed + " FAILED.");
        System.out.println("==================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
