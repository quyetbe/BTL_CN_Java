import model.TaiKhoan;
import service.AuthService;
import util.UIUtil;
import view.LoginForm;
import view.MainForm;
import view.dialog.XepLichDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureAllScreenshots {

    private static void saveContentPane(JFrame frame, String filename) throws Exception {
        frame.validate();
        frame.repaint();
        Thread.sleep(300);

        Container cp = frame.getContentPane();
        int w = cp.getWidth();
        int h = cp.getHeight();
        if (w <= 0 || h <= 0) {
            w = frame.getWidth();
            h = frame.getHeight();
        }

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        cp.paint(g2);
        g2.dispose();

        File out = new File("screenshots/" + filename);
        ImageIO.write(img, "png", out);
        System.out.println("Saved: " + out.getName() + " (" + w + "x" + h + ", " + out.length() + " bytes)");
    }

    private static void saveDialogContent(JDialog dialog, String filename) throws Exception {
        dialog.validate();
        dialog.repaint();
        Thread.sleep(300);

        Container cp = dialog.getContentPane();
        int w = cp.getWidth();
        int h = cp.getHeight();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        cp.paint(g2);
        g2.dispose();

        File out = new File("screenshots/" + filename);
        ImageIO.write(img, "png", out);
        System.out.println("Saved Dialog: " + out.getName() + " (" + w + "x" + h + ", " + out.length() + " bytes)");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Bắt đầu chụp bộ 11 ảnh giao diện chuẩn HD...");
        UIUtil.setSystemLookAndFeel();
        new File("screenshots").mkdirs();

        // 1. Hình 3.1: LoginForm
        LoginForm loginForm = new LoginForm();
        loginForm.setCredentials("admin", "admin123");
        loginForm.pack();
        loginForm.setSize(420, 390);
        loginForm.setLocationRelativeTo(null);
        loginForm.setVisible(true);
        Thread.sleep(400);
        saveContentPane(loginForm, "hinh_3_1_login.png");
        loginForm.dispose();

        // Xác thực quyền admin
        AuthService.getInstance().login("admin", "admin123");

        // 2. MainForm
        MainForm mf = new MainForm();
        mf.setSize(1280, 760);
        mf.setLocationRelativeTo(null);
        mf.setVisible(true);
        Thread.sleep(600);

        // Hình 3.2: Dashboard (index 0)
        mf.selectTabByIndex(0);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_2_dashboard.png");

        // Hình 3.3: Lưới TKB Tuần (index 1)
        mf.selectTabByIndex(1);
        Thread.sleep(600);
        saveContentPane(mf, "hinh_3_3_grid.png");

        // Hình 3.4: Xếp Lịch TKB (index 2)
        mf.selectTabByIndex(2);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_4_thoikhoabieu.png");

        // Hình 3.5: Hộp thoại Xếp lịch XepLichDialog
        XepLichDialog dialog = new XepLichDialog(mf, null);
        dialog.setModal(false);
        dialog.pack();
        dialog.setSize(560, 640);
        dialog.setLocationRelativeTo(mf);
        dialog.setVisible(true);
        Thread.sleep(500);
        saveDialogContent(dialog, "hinh_3_5_xeplich_dialog.png");
        dialog.dispose();

        // Hình 3.6: Phòng Học (index 3)
        mf.selectTabByIndex(3);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_6_phonghoc.png");

        // Hình 3.7: Giảng Viên (index 4)
        mf.selectTabByIndex(4);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_7_giangvien.png");

        // Hình 3.8: Môn Học (index 5)
        mf.selectTabByIndex(5);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_8_monhoc.png");

        // Hình 3.9: Lớp Học (index 6)
        mf.selectTabByIndex(6);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_9_lophoc.png");

        // Hình 3.10: Thống Kê & Báo Cáo (index 7)
        mf.selectTabByIndex(7);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_10_thongke.png");

        // Hình 3.11: Quản Trị Tài Khoản (index 8)
        mf.selectTabByIndex(8);
        Thread.sleep(400);
        saveContentPane(mf, "hinh_3_11_taikhoan.png");

        mf.dispose();
        System.out.println("TOÀN BỘ 11 ẢNH GIAO DIỆN ĐÃ ĐƯỢC CHỤP THÀNH CÔNG!");
        System.exit(0);
    }
}
