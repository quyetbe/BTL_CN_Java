package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích băm và xác thực mật khẩu bằng thuật toán SHA-256.
 * Đảm bảo không lưu mật khẩu dạng plaintext trong cơ sở dữ liệu.
 */
public class PasswordUtil {

    /**
     * Băm chuỗi mật khẩu thành chuỗi Hex 64 ký tự bằng SHA-256.
     *
     * @param plainPassword Mật khẩu thô
     * @return Chuỗi băm SHA-256 dạng Hex
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không tìm thấy thuật toán SHA-256", e);
        }
    }

    /**
     * Kiểm tra mật khẩu thô khớp với chuỗi băm trong CSDL.
     *
     * @param plainPassword Mật khẩu người dùng nhập
     * @param hashedPassword Mật khẩu đã băm trong CSDL
     * @return true nếu khớp, false nếu sai
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String inputHash = hashPassword(plainPassword);
        return inputHash.equalsIgnoreCase(hashedPassword);
    }
}
