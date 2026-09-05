package util;

import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra tính hợp lệ của dữ liệu người dùng nhập (Validation).
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[3|5|7|8|9])[0-9]{8}$"
    );

    /**
     * Kiểm tra chuỗi rỗng hoặc chỉ chứa khoảng trắng.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra định dạng Email hợp lệ.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Kiểm tra số điện thoại di động Việt Nam (10 số, bắt đầu bằng 03, 05, 07, 08, 09).
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Kiểm tra có phải là số nguyên dương (> 0).
     */
    public static boolean isPositiveInteger(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse số nguyên dương, trả về -1 nếu không hợp lệ.
     */
    public static int parsePositiveInt(String str) {
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0 ? val : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của tiết học (tiết bắt đầu 1..12, số tiết 1..6, kết thúc <= 12).
     */
    public static boolean isValidPeriod(int start, int count) {
        if (start < 1 || start > 12) return false;
        if (count < 1 || count > 6) return false;
        return (start + count - 1) <= 12;
    }

    /**
     * Kiểm tra tính hợp lệ của khoảng tuần học (1..52).
     */
    public static boolean isValidWeekRange(int startWeek, int endWeek) {
        if (startWeek < 1 || startWeek > 52) return false;
        if (endWeek < 1 || endWeek > 52) return false;
        return startWeek <= endWeek;
    }
}
