package test;

import util.UIUtil;
import view.MainForm;
import view.panel.DashboardPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureDashboardUI {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            try {
                UIUtil.setSystemLookAndFeel();
                File outDir = new File("screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                service.AuthService.getInstance().login("admin", "admin123");

                MainForm mainForm = new MainForm();
                mainForm.setSize(1400, 850);
                mainForm.setLocationRelativeTo(null);
                mainForm.setVisible(true);

                Thread.sleep(800);

                captureComponent(mainForm, "screenshots/hinh_trangchu_moi.png");
                mainForm.dispose();

                System.out.println("=== CHỤP ẢNH TRANG CHỦ MỚI THÀNH CÔNG! ===");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureComponent(Component comp, String outputPath) {
        try {
            BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            comp.paint(g2);
            g2.dispose();
            ImageIO.write(img, "png", new File(outputPath));
            System.out.println("Đã lưu ảnh: " + outputPath);
        } catch (Exception e) {
            System.err.println("Lỗi chụp component: " + e.getMessage());
        }
    }
}
