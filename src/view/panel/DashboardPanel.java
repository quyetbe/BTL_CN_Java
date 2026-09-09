package view.panel;

import service.AuthService;
import service.ThongKeService;
import util.UIUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Trang tổng quan Dashboard hiện đại, chuyên nghiệp:
 * - Header chào mừng tích hợp vai trò người dùng và ngày tháng thực tế.
 * - 6 Thẻ KPI số liệu tổng thể (Sinh viên, Giảng viên, Lớp học, Môn học, Phòng học, Lịch TKB).
 * - Cột trái: Lối tắt thao tác nhanh trực quan với thanh chỉ màu và chú thích.
 * - Cột phải: Tổng quan tiến độ đào tạo 4 khóa (K21 - K24) và 3 chỉ số vận hành cốt lõi.
 * - Phía dưới: Thông tin hệ thống và kiến trúc phần mềm được bố cục nằm ngang gọn gàng 4 cột.
 */
public class DashboardPanel extends JPanel {

    private final ThongKeService thongKeService;
    private final Runnable navToSchedule;
    private final Runnable navToGrid;
    private final Runnable navToRooms;
    private final Runnable navToStats;
    private final Runnable navToPropose;
    private final Runnable navToWorkflow;
    private final Runnable navToCurriculum;

    private JLabel lblTotalStudents;
    private JLabel lblTotalRooms;
    private JLabel lblTotalLecturers;
    private JLabel lblTotalSubjects;
    private JLabel lblTotalClasses;
    private JLabel lblTotalSchedules;

    public DashboardPanel(Runnable navToSchedule, Runnable navToGrid, Runnable navToRooms, Runnable navToStats) {
        this(navToSchedule, navToGrid, navToRooms, navToStats, null, null, null);
    }

    public DashboardPanel(Runnable navToSchedule, Runnable navToGrid, Runnable navToRooms, Runnable navToStats,
                          Runnable navToPropose, Runnable navToWorkflow, Runnable navToCurriculum) {
        this.thongKeService = new ThongKeService();
        this.navToSchedule = navToSchedule;
        this.navToGrid = navToGrid;
        this.navToRooms = navToRooms;
        this.navToStats = navToStats;
        this.navToPropose = navToPropose;
        this.navToWorkflow = navToWorkflow;
        this.navToCurriculum = navToCurriculum;

        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Container chính bọc trong JScrollPane cuộn mượt khi thu nhỏ cửa sổ
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setOpaque(false);

        // 1. Header Greeting & Quick Actions
        pnlMain.add(createHeaderPanel());
        pnlMain.add(Box.createVerticalStrut(14));

        // 2. Metric Cards Grid (6 KPI)
        pnlMain.add(createMetricCardsPanel());
        pnlMain.add(Box.createVerticalStrut(16));

        // 3. Middle Section: Quick Actions & Training Overview
        pnlMain.add(createMiddleSectionPanel());
        pnlMain.add(Box.createVerticalStrut(16));

        // 4. Bottom Section: Thông tin hệ thống (ĐƯỢC CHUYỂN XUỐNG DƯỚI)
        pnlMain.add(createSystemInfoBottomPanel());

        JScrollPane scrollPane = new JScrollPane(pnlMain);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Khối 1: Header chào mừng, hiển thị vai trò và ngày giờ hệ thống
     */
    private JPanel createHeaderPanel() {
        JPanel pnlGreeting = new JPanel(new BorderLayout(12, 0));
        pnlGreeting.setOpaque(false);

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlLeft.setOpaque(false);

        String userName = AuthService.getInstance().getCurrentUserName();
        String userRole = AuthService.getInstance().getCurrentUserRole();

        JLabel lblGreeting = new JLabel("Xin chào, " + userName);
        lblGreeting.setFont(UIUtil.FONT_TITLE);
        lblGreeting.setForeground(UIUtil.TEXT_DARK);
        pnlLeft.add(lblGreeting);

        // Badge hiển thị vai trò
        JLabel lblRoleBadge = new JLabel("  " + userRole.toUpperCase() + "  ");
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRoleBadge.setForeground(Color.WHITE);
        lblRoleBadge.setOpaque(true);
        lblRoleBadge.setBackground(UIUtil.PRIMARY);
        lblRoleBadge.setBorder(new EmptyBorder(3, 8, 3, 8));
        pnlLeft.add(lblRoleBadge);

        JLabel lblSub = new JLabel("Hệ thống Quản lý Thời khóa biểu & Tài nguyên Phòng học - CNJ56");
        lblSub.setFont(UIUtil.FONT_REGULAR);
        lblSub.setForeground(UIUtil.TEXT_MUTED);

        JPanel pnlLeftCol = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlLeftCol.setOpaque(false);
        pnlLeftCol.add(pnlLeft);
        pnlLeftCol.add(lblSub);
        pnlGreeting.add(pnlLeftCol, BorderLayout.WEST);

        // Bên phải: Ngày tháng & Nút làm mới
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        pnlRight.setOpaque(false);

        LocalDate now = LocalDate.now();
        String dateStr = "Ngày: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblDate = new JLabel(dateStr);
        lblDate.setFont(UIUtil.FONT_BOLD);
        lblDate.setForeground(UIUtil.TEXT_MUTED);
        pnlRight.add(lblDate);

        JButton btnRefresh = UIUtil.createSecondaryButton("Làm Mới Số Liệu");
        btnRefresh.setPreferredSize(new Dimension(130, 30));
        btnRefresh.addActionListener(e -> refreshData());
        pnlRight.add(btnRefresh);

        pnlGreeting.add(pnlRight, BorderLayout.EAST);
        return pnlGreeting;
    }

    /**
     * Khối 2: Lưới 6 thẻ KPI tổng thể
     */
    private JPanel createMetricCardsPanel() {
        JPanel pnlCards = new JPanel(new GridLayout(1, 6, 12, 0));
        pnlCards.setOpaque(false);
        pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
        lblTotalLecturers = new JLabel("0", SwingConstants.CENTER);
        lblTotalClasses = new JLabel("0", SwingConstants.CENTER);
        lblTotalSubjects = new JLabel("0", SwingConstants.CENTER);
        lblTotalRooms = new JLabel("0", SwingConstants.CENTER);
        lblTotalSchedules = new JLabel("0", SwingConstants.CENTER);

        pnlCards.add(createMetricCard("SINH VIÊN", lblTotalStudents, "4 khóa K21-K24", new Color(16, 185, 129)));
        pnlCards.add(createMetricCard("GIẢNG VIÊN", lblTotalLecturers, "Cơ hữu & thỉnh giảng", new Color(13, 148, 136)));
        pnlCards.add(createMetricCard("LỚP HỌC", lblTotalClasses, "10 lớp / khóa", new Color(124, 58, 237)));
        pnlCards.add(createMetricCard("MÔN HỌC", lblTotalSubjects, "130 tín chỉ chuẩn", new Color(217, 119, 6)));
        pnlCards.add(createMetricCard("PHÒNG HỌC", lblTotalRooms, "10 LT + 10 PM", new Color(37, 99, 235)));
        pnlCards.add(createMetricCard("LỊCH ĐÃ XẾP", lblTotalSchedules, "0 xung đột lịch", new Color(22, 163, 74)));

        return pnlCards;
    }

    private JPanel createMetricCard(String title, JLabel lblValue, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UIUtil.TEXT_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(accentColor);
        card.add(lblValue, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout(0, 4));
        pnlBottom.setOpaque(false);

        JLabel lblSub = new JLabel(subtitle, SwingConstants.LEFT);
        lblSub.setFont(UIUtil.FONT_SMALL);
        lblSub.setForeground(new Color(148, 163, 184));
        pnlBottom.add(lblSub, BorderLayout.NORTH);

        JPanel pnlBar = new JPanel();
        pnlBar.setPreferredSize(new Dimension(card.getWidth(), 3));
        pnlBar.setBackground(accentColor);
        pnlBottom.add(pnlBar, BorderLayout.SOUTH);

        card.add(pnlBottom, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Khối 3: Cột Trái (Lối tắt nhanh) & Cột Phải (Tiến độ đào tạo K21-K24 & Chỉ số vận hành)
     */
    private JPanel createMiddleSectionPanel() {
        JPanel pnlMiddle = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlMiddle.setOpaque(false);

        // ================= CỘT TRÁI: LỐI TẮT THAO TÁC NHANH =================
        JPanel boxShortcuts = new JPanel(new BorderLayout(0, 10));
        boxShortcuts.setBackground(Color.WHITE);
        boxShortcuts.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(14, 18, 14, 18)));

        JPanel pnlBox1Header = new JPanel(new BorderLayout());
        pnlBox1Header.setOpaque(false);
        JLabel lblBox1Title = new JLabel("LỐI TẮT THAO TÁC NHANH");
        lblBox1Title.setFont(UIUtil.FONT_SUBTITLE);
        lblBox1Title.setForeground(UIUtil.PRIMARY_DARK);
        pnlBox1Header.add(lblBox1Title, BorderLayout.WEST);

        JLabel lblBox1Hint = new JLabel("Nhấp để điều hướng tức thì");
        lblBox1Hint.setFont(UIUtil.FONT_SMALL);
        lblBox1Hint.setForeground(UIUtil.TEXT_MUTED);
        pnlBox1Header.add(lblBox1Hint, BorderLayout.EAST);

        boxShortcuts.add(pnlBox1Header, BorderLayout.NORTH);

        JPanel pnlButtons = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlButtons.setOpaque(false);

        // 1. Xem Lưới TKB Tuần
        JButton btnGrid = createActionCardButton(
                "Xem Lưới Thời Khóa Biểu Tuần",
                "Tra cứu TKB trực quan theo Tuần, Lớp học, Giảng viên hoặc Phòng học",
                UIUtil.PRIMARY
        );
        btnGrid.addActionListener(e -> { if (navToGrid != null) navToGrid.run(); });
        pnlButtons.add(btnGrid);

        AuthService auth = AuthService.getInstance();

        // 2. Nút theo phân quyền xếp lịch / đề xuất / duyệt đổi lịch
        if (auth.canAccessSchedule()) {
            JButton btnSchedule = createActionCardButton(
                    "Xếp Lịch & Quản Lý Thời Khóa Biểu",
                    "Phát hiện và ngăn chặn xung đột lịch tự động (Trùng phòng, GV, Lớp)",
                    new Color(124, 58, 237)
            );
            btnSchedule.addActionListener(e -> { if (navToSchedule != null) navToSchedule.run(); });
            pnlButtons.add(btnSchedule);
        } else if (auth.canProposeSchedule()) {
            JButton btnPropose = createActionCardButton(
                    "Đề Xuất Đổi Lịch Giảng Dạy",
                    "Gửi yêu cầu đổi lịch, dời phòng hoặc đổi ca dạy trực tuyến",
                    new Color(217, 119, 6)
            );
            btnPropose.addActionListener(e -> { if (navToPropose != null) navToPropose.run(); });
            pnlButtons.add(btnPropose);
        } else if (auth.canApproveSchedule()) {
            JButton btnApprove = createActionCardButton(
                    "Quy Trình Duyệt Đổi Lịch (2 Cấp)",
                    "Duyệt yêu cầu đổi lịch cấp Khoa (Cấp 1) và Ban Giám Hiệu (Cấp 2)",
                    new Color(22, 163, 74)
            );
            btnApprove.addActionListener(e -> { if (navToWorkflow != null) navToWorkflow.run(); });
            pnlButtons.add(btnApprove);
        }

        // 3. Thống kê hoặc CTĐT
        if (auth.canAccessStatistics()) {
            JButton btnStats = createActionCardButton(
                    "Tra Cứu Phòng Trống & Báo Cáo Thống Kê",
                    "Xem tỷ lệ lấp đầy phòng học, tìm phòng trống, thống kê tải giảng dạy",
                    new Color(13, 148, 136)
            );
            btnStats.addActionListener(e -> { if (navToStats != null) navToStats.run(); });
            pnlButtons.add(btnStats);
        }

        // 4. CTĐT 130 tín chỉ
        if (auth.canAccessCurriculum()) {
            JButton btnCurriculum = createActionCardButton(
                    "Khung Chương Trình Đào Tạo 130 Tín Chỉ",
                    "Xem lộ trình đào tạo chuẩn 4 năm (8 học kỳ) cho các khóa K21 - K24",
                    new Color(30, 64, 175)
            );
            btnCurriculum.addActionListener(e -> { if (navToCurriculum != null) navToCurriculum.run(); });
            pnlButtons.add(btnCurriculum);
        }

        // 5. Phòng học
        if (auth.canAccessRooms()) {
            JButton btnRooms = createActionCardButton(
                    "Quản Lý Danh Mục Phòng Học & Thiết Bị",
                    "Quản lý sức chứa, trạng thái thiết bị 20 phòng lý thuyết và thực hành",
                    new Color(75, 85, 99)
            );
            btnRooms.addActionListener(e -> { if (navToRooms != null) navToRooms.run(); });
            pnlButtons.add(btnRooms);
        }

        boxShortcuts.add(pnlButtons, BorderLayout.CENTER);
        pnlMiddle.add(boxShortcuts);

        // ================= CỘT PHẢI: TIẾN ĐỘ ĐÀO TẠO & VẬN HÀNH =================
        JPanel boxOverview = new JPanel(new BorderLayout(0, 10));
        boxOverview.setBackground(Color.WHITE);
        boxOverview.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(14, 18, 14, 18)));

        JPanel pnlBox2Header = new JPanel(new BorderLayout());
        pnlBox2Header.setOpaque(false);
        JLabel lblBox2Title = new JLabel("TIẾN ĐỘ ĐÀO TẠO & CHỈ SỐ VẬN HÀNH");
        lblBox2Title.setFont(UIUtil.FONT_SUBTITLE);
        lblBox2Title.setForeground(UIUtil.PRIMARY_DARK);
        pnlBox2Header.add(lblBox2Title, BorderLayout.WEST);

        JLabel lblBox2Hint = new JLabel("Quy chuẩn 4 năm - 130 Tín chỉ");
        lblBox2Hint.setFont(UIUtil.FONT_SMALL);
        lblBox2Hint.setForeground(UIUtil.TEXT_MUTED);
        pnlBox2Header.add(lblBox2Hint, BorderLayout.EAST);

        boxOverview.add(pnlBox2Header, BorderLayout.NORTH);

        JPanel pnlOverviewBody = new JPanel();
        pnlOverviewBody.setLayout(new BoxLayout(pnlOverviewBody, BoxLayout.Y_AXIS));
        pnlOverviewBody.setOpaque(false);

        // Lưới 4 thẻ khóa học K21 -> K24
        JPanel pnlKhoaGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlKhoaGrid.setOpaque(false);
        pnlKhoaGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        pnlKhoaGrid.add(createKhoaCard("Khóa K21 - Năm 4", "10 lớp • 500 sinh viên", "HK7 - HK8 • 130/130 Tín chỉ", new Color(248, 250, 252)));
        pnlKhoaGrid.add(createKhoaCard("Khóa K22 - Năm 3", "10 lớp • 500 sinh viên", "HK5 - HK6 • 130/130 Tín chỉ", new Color(248, 250, 252)));
        pnlKhoaGrid.add(createKhoaCard("Khóa K23 - Năm 2", "10 lớp • 500 sinh viên", "HK3 - HK4 • 130/130 Tín chỉ", new Color(248, 250, 252)));
        pnlKhoaGrid.add(createKhoaCard("Khóa K24 - Năm 1", "10 lớp • 500 sinh viên", "HK1 - HK2 • 130/130 Tín chỉ", new Color(248, 250, 252)));

        pnlOverviewBody.add(pnlKhoaGrid);
        pnlOverviewBody.add(Box.createVerticalStrut(10));

        // 3 Chỉ số vận hành cốt lõi
        pnlOverviewBody.add(createOperationalIndicator("Kiểm Soát Xung Đột Lịch Học", "100% An toàn • 0 trùng phòng, 0 trùng GV, 0 trùng lớp", new Color(240, 253, 244), new Color(22, 101, 52)));
        pnlOverviewBody.add(Box.createVerticalStrut(8));
        pnlOverviewBody.add(createOperationalIndicator("Tải Giảng Dạy Giảng Viên", "15.5 tiết / tuần / giảng viên • 155 GV có giờ dạy trong HK1", new Color(240, 249, 255), new Color(3, 105, 161)));
        pnlOverviewBody.add(Box.createVerticalStrut(8));
        pnlOverviewBody.add(createOperationalIndicator("Hiệu Suất Tài Nguyên Phòng", "Khả dụng 20/20 phòng • 10 phòng Lý thuyết & 10 Thực hành", new Color(254, 252, 232), new Color(161, 98, 7)));

        boxOverview.add(pnlOverviewBody, BorderLayout.CENTER);
        pnlMiddle.add(boxOverview);

        return pnlMiddle;
    }

    private JPanel createKhoaCard(String title, String line1, String line2, Color bg) {
        JPanel pnl = new JPanel(new GridLayout(3, 1, 0, 3));
        pnl.setBackground(bg);
        pnl.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtil.FONT_BOLD);
        lblTitle.setForeground(UIUtil.TEXT_DARK);

        JLabel lblL1 = new JLabel(line1);
        lblL1.setFont(UIUtil.FONT_SMALL);
        lblL1.setForeground(UIUtil.TEXT_MUTED);

        JLabel lblL2 = new JLabel(line2);
        lblL2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblL2.setForeground(UIUtil.PRIMARY);

        pnl.add(lblTitle);
        pnl.add(lblL1);
        pnl.add(lblL2);
        return pnl;
    }

    private JPanel createOperationalIndicator(String title, String desc, Color bg, Color textCol) {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 2));
        pnl.setBackground(bg);
        pnl.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(7, 12, 7, 12)
        ));
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtil.FONT_BOLD);
        lblTitle.setForeground(textCol);
        pnl.add(lblTitle);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(UIUtil.FONT_SMALL);
        lblDesc.setForeground(textCol.darker());
        pnl.add(lblDesc);

        return pnl;
    }

    private JButton createActionCardButton(String title, String subtitle, Color accentColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setBackground(new Color(248, 250, 252));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(6, 12, 6, 14)
        ));

        // Accent indicator bar on the left
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(4, 36));
        bar.setBackground(accentColor);
        btn.add(bar, BorderLayout.WEST);

        // Center content with title and subtitle
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 1));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtil.FONT_BOLD);
        lblTitle.setForeground(UIUtil.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UIUtil.FONT_SMALL);
        lblSub.setForeground(UIUtil.TEXT_MUTED);

        pnlText.add(lblTitle);
        pnlText.add(lblSub);
        btn.add(pnlText, BorderLayout.CENTER);

        // Right arrow icon
        JLabel lblArrow = new JLabel(">");
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblArrow.setForeground(accentColor);
        btn.add(lblArrow, BorderLayout.EAST);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(239, 246, 255));
                btn.setBorder(new CompoundBorder(
                        new LineBorder(accentColor, 1),
                        new EmptyBorder(6, 12, 6, 14)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
                btn.setBorder(new CompoundBorder(
                        new LineBorder(UIUtil.BORDER_COLOR, 1),
                        new EmptyBorder(6, 12, 6, 14)
                ));
            }
        });

        return btn;
    }

    /**
     * Khối 4: THÔNG TIN HỆ THỐNG ĐƯỢC ĐẶT XUỐNG DƯỚI CÙNG (NGANG BẰNG CÁC CỘT TRỰC QUAN)
     */
    private JPanel createSystemInfoBottomPanel() {
        JPanel pnlBottomWrapper = new JPanel(new BorderLayout(0, 10));
        pnlBottomWrapper.setBackground(Color.WHITE);
        pnlBottomWrapper.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("THÔNG TIN HỆ THỐNG & KIẾN TRÚC VẬN HÀNH (CNJ56)");
        lblTitle.setFont(UIUtil.FONT_SUBTITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JLabel lblTag = new JLabel("Phiên bản v2.0 • Production");
        lblTag.setFont(UIUtil.FONT_SMALL);
        lblTag.setForeground(UIUtil.TEXT_MUTED);
        pnlHeader.add(lblTag, BorderLayout.EAST);

        pnlBottomWrapper.add(pnlHeader, BorderLayout.NORTH);

        // 4 Cột thông tin nằm ngang dàn đều
        JPanel pnlCols = new JPanel(new GridLayout(1, 4, 12, 0));
        pnlCols.setOpaque(false);

        pnlCols.add(createInfoColumn(
                "ĐỀ TÀI & NỀN TẢNG",
                "Quản lý TKB & Tài nguyên",
                "• Giao diện: Java Swing Flat UI\n" +
                "• Kết nối: JDBC thuần kết nối CSDL\n" +
                "• Nền tảng: Java 17+, NetBeans/IDEA",
                new Color(37, 99, 235)
        ));

        pnlCols.add(createInfoColumn(
                "THUẬT TOÁN XẾP LỊCH",
                "Kiểm soát xung đột tức thì",
                "• Phát hiện trùng Phòng, Lớp, GV\n" +
                "• Kiểm tra sức chứa & loại phòng\n" +
                "• Bảo đảm 0 xung đột trên 690 lịch",
                new Color(22, 163, 74)
        ));

        pnlCols.add(createInfoColumn(
                "AN TOÀN & BẢO MẬT",
                "Bảo vệ dữ liệu & phân quyền",
                "• Mật khẩu băm chuẩn SHA-256\n" +
                "• 100% PreparedStatement chống SQLi\n" +
                "• Phân quyền chuẩn xác 6 vai trò",
                new Color(124, 58, 237)
        ));

        pnlCols.add(createInfoColumn(
                "TRẠNG THÁI MÁY CHỦ CSDL",
                "MySQL Server 8.0 (XAMPP)",
                "• Trạng thái: Đang kết nối ổn định\n" +
                "• Máy chủ: localhost:3306\n" +
                "• Dữ liệu: 2.000 SV, 506 GV, 40 Lớp",
                new Color(13, 148, 136)
        ));

        pnlBottomWrapper.add(pnlCols, BorderLayout.CENTER);
        return pnlBottomWrapper;
    }

    private JPanel createInfoColumn(String badge, String title, String details, Color accentColor) {
        JPanel col = new JPanel(new BorderLayout(0, 4));
        col.setBackground(new Color(248, 250, 252));
        col.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JPanel pnlTop = new JPanel(new BorderLayout(0, 2));
        pnlTop.setOpaque(false);

        JLabel lblBadge = new JLabel(badge);
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(accentColor);
        pnlTop.add(lblBadge, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtil.FONT_BOLD);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.SOUTH);

        col.add(pnlTop, BorderLayout.NORTH);

        JTextArea txt = new JTextArea(details);
        txt.setFont(UIUtil.FONT_SMALL);
        txt.setForeground(UIUtil.TEXT_MUTED);
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        col.add(txt, BorderLayout.CENTER);

        return col;
    }

    public void refreshData() {
        Map<String, Integer> map = thongKeService.getDashboardSummary();
        lblTotalStudents.setText(String.format("%,d", map.getOrDefault("totalStudents", 0)));
        lblTotalLecturers.setText(String.format("%,d", map.getOrDefault("totalLecturers", 0)));
        lblTotalClasses.setText(String.format("%,d", map.getOrDefault("totalClasses", 0)));
        lblTotalSubjects.setText(String.format("%,d", map.getOrDefault("totalSubjects", 0)));
        lblTotalRooms.setText(String.format("%,d", map.getOrDefault("totalRooms", 0)));
        lblTotalSchedules.setText(String.format("%,d", map.getOrDefault("totalSchedules", 0)));
    }
}
