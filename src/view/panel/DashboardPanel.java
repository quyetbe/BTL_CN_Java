package view.panel;

import service.AuthService;
import service.ThongKeService;
import util.UIUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Trang tổng quan Dashboard hiển thị thống kê tổng thể và lối tắt nhanh.
 */
public class DashboardPanel extends JPanel {

    private final ThongKeService thongKeService;
    private final Runnable navToSchedule;
    private final Runnable navToGrid;
    private final Runnable navToRooms;
    private final Runnable navToStats;

    private JLabel lblTotalRooms;
    private JLabel lblTotalLecturers;
    private JLabel lblTotalSubjects;
    private JLabel lblTotalClasses;
    private JLabel lblTotalSchedules;

    public DashboardPanel(Runnable navToSchedule, Runnable navToGrid, Runnable navToRooms, Runnable navToStats) {
        this.thongKeService = new ThongKeService();
        this.navToSchedule = navToSchedule;
        this.navToGrid = navToGrid;
        this.navToRooms = navToRooms;
        this.navToStats = navToStats;

        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header Greeting
        JPanel pnlGreeting = new JPanel(new BorderLayout(0, 4));
        pnlGreeting.setOpaque(false);

        String userGreeting = "Xin chào, " + AuthService.getInstance().getCurrentUserName() + " (" + AuthService.getInstance().getCurrentUserRole() + ")!";
        JLabel lblGreeting = new JLabel(userGreeting);
        lblGreeting.setFont(UIUtil.FONT_TITLE);
        lblGreeting.setForeground(UIUtil.TEXT_DARK);

        JLabel lblSub = new JLabel("Hệ thống Quản lý Thời khóa biểu & Tài nguyên Phòng học - CNJ56");
        lblSub.setFont(UIUtil.FONT_REGULAR);
        lblSub.setForeground(UIUtil.TEXT_MUTED);

        pnlGreeting.add(lblGreeting, BorderLayout.NORTH);
        pnlGreeting.add(lblSub, BorderLayout.SOUTH);
        add(pnlGreeting, BorderLayout.NORTH);

        // Center: Metric Cards & Shortcuts
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);

        // Metric Cards Grid
        JPanel pnlCards = new JPanel(new GridLayout(1, 5, 12, 0));
        pnlCards.setOpaque(false);

        lblTotalRooms = new JLabel("0", SwingConstants.CENTER);
        lblTotalLecturers = new JLabel("0", SwingConstants.CENTER);
        lblTotalSubjects = new JLabel("0", SwingConstants.CENTER);
        lblTotalClasses = new JLabel("0", SwingConstants.CENTER);
        lblTotalSchedules = new JLabel("0", SwingConstants.CENTER);

        pnlCards.add(createMetricCard("PHÒNG HỌC", lblTotalRooms, new Color(37, 99, 235)));
        pnlCards.add(createMetricCard("GIẢNG VIÊN", lblTotalLecturers, new Color(13, 148, 136)));
        pnlCards.add(createMetricCard("MÔN HỌC", lblTotalSubjects, new Color(217, 119, 6)));
        pnlCards.add(createMetricCard("LỚP HỌC", lblTotalClasses, new Color(124, 58, 237)));
        pnlCards.add(createMetricCard("LỊCH ĐÃ XẾP", lblTotalSchedules, new Color(22, 163, 74)));

        pnlCenter.add(pnlCards, BorderLayout.NORTH);

        // Quick Actions & Features
        JPanel pnlActions = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlActions.setOpaque(false);

        // Box 1: Lối tắt thao tác nhanh
        JPanel boxShortcuts = new JPanel(new GridLayout(4, 1, 10, 10));
        boxShortcuts.setBackground(Color.WHITE);
        boxShortcuts.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(16, 18, 16, 18)));

        JLabel lblBox1Title = new JLabel("LỐI TẮT THAO TÁC NHANH");
        lblBox1Title.setFont(UIUtil.FONT_SUBTITLE);
        lblBox1Title.setForeground(UIUtil.PRIMARY_DARK);
        boxShortcuts.add(lblBox1Title);

        JButton btnGoGrid = UIUtil.createPrimaryButton("Xem Lưới Thời Khóa Biểu Tuần");
        btnGoGrid.addActionListener(e -> { if (navToGrid != null) navToGrid.run(); });
        boxShortcuts.add(btnGoGrid);

        JButton btnGoSchedule = UIUtil.createSecondaryButton("Xếp Lịch / Quản Lý Thời Khóa Biểu");
        btnGoSchedule.addActionListener(e -> { if (navToSchedule != null) navToSchedule.run(); });
        boxShortcuts.add(btnGoSchedule);

        JButton btnGoStats = UIUtil.createSecondaryButton("Tra Cứu Phòng Trống & Thống Kê");
        btnGoStats.addActionListener(e -> { if (navToStats != null) navToStats.run(); });
        boxShortcuts.add(btnGoStats);

        pnlActions.add(boxShortcuts);

        // Box 2: Thông tin hệ thống
        JPanel boxInfo = new JPanel(new BorderLayout(0, 10));
        boxInfo.setBackground(Color.WHITE);
        boxInfo.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(16, 18, 16, 18)));

        JLabel lblBox2Title = new JLabel("THÔNG TIN HỆ THỐNG");
        lblBox2Title.setFont(UIUtil.FONT_SUBTITLE);
        lblBox2Title.setForeground(UIUtil.TEXT_DARK);
        boxInfo.add(lblBox2Title, BorderLayout.NORTH);

        JTextArea txtSysInfo = new JTextArea();
        txtSysInfo.setFont(UIUtil.FONT_REGULAR);
        txtSysInfo.setEditable(false);
        txtSysInfo.setOpaque(false);
        txtSysInfo.setLineWrap(true);
        txtSysInfo.setWrapStyleWord(true);
        txtSysInfo.setText(
                "• Đề tài: CNJ56 - Quản lý thời khóa biểu & tài nguyên phòng học\n"
              + "• Công nghệ: Java Swing, JDBC thuần, MySQL 8.0, XAMPP\n"
              + "• Thuật toán: Tự động phát hiện và ngăn chặn xung đột lịch\n"
              + "  (Trùng phòng, Giảng viên, Lớp học, Sức chứa, Loại phòng)\n"
              + "• Bảo mật: Mật khẩu băm SHA-256, 100% PreparedStatement chống SQLi\n"
              + "• Trạng thái CSDL: Kết nối ổn định tới localhost:3306"
        );
        boxInfo.add(txtSysInfo, BorderLayout.CENTER);

        pnlActions.add(boxInfo);

        pnlCenter.add(pnlActions, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel lblValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(UIUtil.FONT_BOLD);
        lblTitle.setForeground(UIUtil.TEXT_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(accentColor);
        card.add(lblValue, BorderLayout.CENTER);

        JPanel pnlBar = new JPanel();
        pnlBar.setPreferredSize(new Dimension(card.getWidth(), 3));
        pnlBar.setBackground(accentColor);
        card.add(pnlBar, BorderLayout.SOUTH);

        return card;
    }

    public void refreshData() {
        Map<String, Integer> map = thongKeService.getDashboardSummary();
        lblTotalRooms.setText(String.valueOf(map.getOrDefault("totalRooms", 0)));
        lblTotalLecturers.setText(String.valueOf(map.getOrDefault("totalLecturers", 0)));
        lblTotalSubjects.setText(String.valueOf(map.getOrDefault("totalSubjects", 0)));
        lblTotalClasses.setText(String.valueOf(map.getOrDefault("totalClasses", 0)));
        lblTotalSchedules.setText(String.valueOf(map.getOrDefault("totalSchedules", 0)));
    }
}
