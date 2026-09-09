package test;

import util.UIUtil;
import view.panel.MonHocPanel;
import view.panel.PaginationBar;
import view.panel.ThongKePanel;
import view.panel.TimetableGridPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureVerificationUI {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            try {
                UIUtil.setSystemLookAndFeel();
                File outDir = new File("screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                service.AuthService.getInstance().login("admin", "admin123");

                // 1. Chụp MonHocPanel Trang 3 (STT 41 - 50, Nút "< Trước" BẬT)
                JFrame frameMh = new JFrame("Danh Mục Môn Học - Kiểm Tra Phân Trang");
                frameMh.setSize(1280, 800);
                frameMh.setLocationRelativeTo(null);
                MonHocPanel pnlMh = new MonHocPanel();
                frameMh.setContentPane(pnlMh);
                frameMh.setVisible(true);
                Thread.sleep(600);

                // Tìm PaginationBar và bấm Cuối >> hoặc Tiếp >
                PaginationBar pBar = findPaginationBar(pnlMh);
                if (pBar != null) {
                    JButton btnLast = findButton(pBar, "Cuối >>");
                    if (btnLast != null) {
                        btnLast.doClick();
                    }
                }
                Thread.sleep(600);
                captureComponent(frameMh, "screenshots/hinh_phantrang_fix_trang3.png");

                // Thử bấm nút "< Trước" để lùi về Trang 2
                if (pBar != null) {
                    JButton btnPrev = findButton(pBar, "< Trước");
                    if (btnPrev != null && btnPrev.isEnabled()) {
                        btnPrev.doClick();
                    }
                }
                Thread.sleep(600);
                captureComponent(frameMh, "screenshots/hinh_phantrang_fix_trang2.png");
                frameMh.dispose();

                // 2. Chụp TimetableGridPanel - Xem theo Giảng Viên
                JFrame frameTkb = new JFrame("Lưới Thời Khóa Biểu - Xem Theo Giảng Viên");
                frameTkb.setSize(1280, 800);
                frameTkb.setLocationRelativeTo(null);
                TimetableGridPanel pnlTkb = new TimetableGridPanel();
                frameTkb.setContentPane(pnlTkb);
                frameTkb.setVisible(true);
                Thread.sleep(600);

                JComboBox<String> cbViewMode = findComboBoxContaining(pnlTkb, "Xem theo Giảng viên");
                if (cbViewMode != null) {
                    cbViewMode.setSelectedItem("Xem theo Giảng viên");
                }
                Thread.sleep(600);

                // Chọn Giảng viên GV0001
                JComboBox<String> cbTarget = findComboBoxStartingWith(pnlTkb, "GV0001");
                if (cbTarget != null) {
                    for (int i = 0; i < cbTarget.getItemCount(); i++) {
                        String item = cbTarget.getItemAt(i);
                        if (item != null && item.startsWith("GV0001")) {
                            cbTarget.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                Thread.sleep(600);

                JButton btnApply = findButton(pnlTkb, "Xem Lịch");
                if (btnApply != null) {
                    btnApply.doClick();
                }
                Thread.sleep(600);
                captureComponent(frameTkb, "screenshots/hinh_tkb_giangvien.png");
                frameTkb.dispose();

                // 3. Chụp ThongKePanel - Tab Thống Kê Giảng Viên
                JFrame frameTk = new JFrame("Thống Kê Tải Giảng Dạy Của Giảng Viên");
                frameTk.setSize(1280, 800);
                frameTk.setLocationRelativeTo(null);
                ThongKePanel pnlTk = new ThongKePanel();
                frameTk.setContentPane(pnlTk);
                frameTk.setVisible(true);
                Thread.sleep(600);

                JTabbedPane tabbedPane = findTabbedPane(pnlTk);
                if (tabbedPane != null) {
                    tabbedPane.setSelectedIndex(2); // Tab 3: Thống kê Giảng viên
                }
                Thread.sleep(600);
                captureComponent(frameTk, "screenshots/hinh_thongke_giangvien.png");
                frameTk.dispose();

                System.out.println("=== CHỤP ẢNH MINH CHỨNG THÀNH CÔNG! ===");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static PaginationBar findPaginationBar(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof PaginationBar) return (PaginationBar) comp;
            if (comp instanceof Container) {
                PaginationBar res = findPaginationBar((Container) comp);
                if (res != null) return res;
            }
        }
        return null;
    }

    private static JButton findButton(Container c, String text) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                if (text.equals(b.getText())) return b;
            }
            if (comp instanceof Container) {
                JButton res = findButton((Container) comp, text);
                if (res != null) return res;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<String> findComboBoxContaining(Container c, String itemToContain) {
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
                JComboBox<String> res = findComboBoxContaining((Container) comp, itemToContain);
                if (res != null) return res;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<String> findComboBoxStartingWith(Container c, String prefix) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                for (int i = 0; i < cb.getItemCount(); i++) {
                    Object item = cb.getItemAt(i);
                    if (item != null && item.toString().startsWith(prefix)) {
                        return (JComboBox<String>) cb;
                    }
                }
            }
            if (comp instanceof Container) {
                JComboBox<String> res = findComboBoxStartingWith((Container) comp, prefix);
                if (res != null) return res;
            }
        }
        return null;
    }

    private static JTabbedPane findTabbedPane(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTabbedPane) return (JTabbedPane) comp;
            if (comp instanceof Container) {
                JTabbedPane res = findTabbedPane((Container) comp);
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
