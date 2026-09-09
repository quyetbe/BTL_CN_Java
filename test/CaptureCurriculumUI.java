package test;

import util.UIUtil;
import view.panel.CurriculumPanel;
import view.panel.MonHocPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CaptureCurriculumUI {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            try {
                UIUtil.setSystemLookAndFeel();
                File outDir = new File("screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                service.AuthService.getInstance().login("admin", "admin123");

                // 1. Chụp CurriculumPanel với Khóa K21 (130 tín chỉ)
                JFrame frameCtdt = new JFrame("Khung CTĐT K21 - 130 Tín Chỉ");
                frameCtdt.setSize(1280, 800);
                frameCtdt.setLocationRelativeTo(null);
                CurriculumPanel pnlCtdt = new CurriculumPanel();
                frameCtdt.setContentPane(pnlCtdt);
                frameCtdt.setVisible(true);
                Thread.sleep(600);

                // Chọn Khóa K21
                JComboBox<String> cbKhoa = findComboBox(pnlCtdt, "K21");
                if (cbKhoa != null) {
                    cbKhoa.setSelectedItem("K21");
                }
                Thread.sleep(600);
                captureComponent(frameCtdt, "screenshots/hinh_ctdt_k21_130tc.png");

                // Chọn Khóa K24
                if (cbKhoa != null) {
                    cbKhoa.setSelectedItem("K24");
                }
                Thread.sleep(600);
                captureComponent(frameCtdt, "screenshots/hinh_ctdt_k24_130tc.png");
                frameCtdt.dispose();

                // 2. Chụp MonHocPanel (Danh mục môn học 50 môn)
                JFrame frameMh = new JFrame("Danh Mục Môn Học");
                frameMh.setSize(1280, 800);
                frameMh.setLocationRelativeTo(null);
                MonHocPanel pnlMh = new MonHocPanel();
                frameMh.setContentPane(pnlMh);
                frameMh.setVisible(true);
                Thread.sleep(600);
                captureComponent(frameMh, "screenshots/hinh_danhmuc_monhoc.png");
                frameMh.dispose();

                System.out.println("=== CHỤP ẢNH CTĐT & MÔN HỌC THÀNH CÔNG! ===");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static JComboBox<String> findComboBox(Container c, String itemToContain) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                for (int i = 0; i < cb.getItemCount(); i++) {
                    if (itemToContain.equals(cb.getItemAt(i))) {
                        return (JComboBox<String>) cb;
                    }
                }
            }
            if (comp instanceof Container) {
                JComboBox<String> res = findComboBox((Container) comp, itemToContain);
                if (res != null) return res;
            }
        }
        return null;
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
