package connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý cấu hình môi trường (.env / config) cho toàn hệ thống.
 * Hỗ trợ chạy linh hoạt trên localhost và các môi trường khác.
 */
public class AppConfig {

    private static final Map<String, String> ENV_VARS = new HashMap<>();

    static {
        loadEnv();
    }

    private static void loadEnv() {
        // Tìm file .env ở thư mục gốc dự án hoặc thư mục web
        File[] possibleFiles = new File[]{
            new File(".env"),
            new File("../.env"),
            new File("web/.env")
        };

        for (File f : possibleFiles) {
            if (f.exists() && f.isFile()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String val = line.substring(eqIdx + 1).trim();
                            ENV_VARS.put(key, val);
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static String get(String key, String defaultValue) {
        return ENV_VARS.getOrDefault(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getDbHost() { return get("DB_HOST", "127.0.0.1"); }
    public static int getDbPort() { return getInt("DB_PORT", 3306); }
    public static String getDbName() { return get("DB_NAME", "quanly_tkb_cnj56"); }
    public static String getDbUser() { return get("DB_USER", "root"); }
    public static String getDbPassword() { return get("DB_PASSWORD", ""); }
    public static int getWebPort() { return getInt("PORT", 8080); }
}
