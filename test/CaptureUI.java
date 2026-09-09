package test;

import connection.AppConfig;
import util.UIUtil;
import view.MainForm;
import view.panel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CaptureUI {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            try {
                UIUtil.setSystemLookAndFeel();
                File outDir = new File("screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                // 1. MainForm - Sinh Viên (Chỉ 3 nút: Trang Chủ, Lưới TKB, Khung CTĐT)
                service.AuthService.getInstance().login("sinhvien", "123456");
                MainForm mfSv = new MainForm();
                mfSv.setVisible(true);
                Thread.sleep(800);
                captureFrame(mfSv, "screenshots/hinh_rbac_sinhvien.png");
                mfSv.dispose();

                // 2. MainForm - Giảng Viên (5 nút: Trang Chủ, Lưới TKB, Đề Xuất, Khung CTĐT, Thống Kê)
                service.AuthService.getInstance().login("giangvien", "123456");
                MainForm mfGv = new MainForm();
                mfGv.setVisible(true);
                Thread.sleep(800);
                captureFrame(mfGv, "screenshots/hinh_rbac_giangvien.png");
                mfGv.dispose();

                // 3. MainForm - Ban Giám Hiệu (11 nút: Duyệt Cấp 2, Giám sát phòng/GV/SV, Thống Kê, Nhật Ký)
                service.AuthService.getInstance().login("bangiamhieu", "123456");
                MainForm mfBgh = new MainForm();
                mfBgh.setVisible(true);
                Thread.sleep(800);
                captureFrame(mfBgh, "screenshots/hinh_rbac_bangiamhieu.png");
                mfBgh.dispose();

                // 4. MainForm - Admin (Đầy đủ 14 nút điều hướng)
                service.AuthService.getInstance().login("admin", "admin123");
                MainForm mfAdmin = new MainForm();
                mfAdmin.setVisible(true);
                Thread.sleep(800);
                captureFrame(mfAdmin, "screenshots/hinh_rbac_admin.png");
                mfAdmin.dispose();

                System.out.println("=== CHỤP ẢNH MÀN HÌNH HOÀN TẤT! ===");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static JTabbedPane findTabbedPane(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTabbedPane) return (JTabbedPane) comp;
            if (comp instanceof Container) {
                JTabbedPane tp = findTabbedPane((Container) comp);
                if (tp != null) return tp;
            }
        }
        return null;
    }

    private static void capturePanel(JPanel panel, String filename, int width, int height) throws Exception {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.getContentPane().add(panel);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Thread.sleep(900);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        frame.paint(g2d);
        g2d.dispose();

        ImageIO.write(img, "png", new File(filename));
        System.out.println("Đã lưu ảnh: " + filename);
        frame.dispose();
    }

    private static void captureFrame(JFrame frame, String filename) throws Exception {
        Thread.sleep(800);
        int w = Math.max(frame.getWidth(), 1200);
        int h = Math.max(frame.getHeight(), 750);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        frame.paint(g2d);
        g2d.dispose();

        ImageIO.write(img, "png", new File(filename));
        System.out.println("Đã lưu ảnh: " + filename);
        frame.dispose();
    }
}
