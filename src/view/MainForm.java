package view;

import model.TaiKhoan;
import service.AuthService;
import util.UIUtil;
import view.panel.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Chính của Hệ thống (Main Window).
 * Thiết kế Sidebar điều hướng hiện đại, Header thông tin phiên làm việc,
 * và CardLayout quản lý các phân hệ chức năng.
 */
public class MainForm extends JFrame {

    private static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    private static final String CARD_GRID = "CARD_GRID";
    private static final String CARD_SCHEDULE = "CARD_SCHEDULE";
    private static final String CARD_ROOMS = "CARD_ROOMS";
    private static final String CARD_TEACHERS = "CARD_TEACHERS";
    private static final String CARD_SUBJECTS = "CARD_SUBJECTS";
    private static final String CARD_CLASSES = "CARD_CLASSES";
    private static final String CARD_STATS = "CARD_STATS";
    private static final String CARD_USERS = "CARD_USERS";

    private CardLayout cardLayout;
    private JPanel pnlContent;

    private DashboardPanel pnlDashboard;
    private TimetableGridPanel pnlGrid;
    private ThoiKhoaBieuPanel pnlSchedule;
    private PhongHocPanel pnlRooms;
    private GiangVienPanel pnlTeachers;
    private MonHocPanel pnlSubjects;
    private LopHocPanel pnlClasses;
    private ThongKePanel pnlStats;
    private TaiKhoanPanel pnlUsers;

    private final List<SidebarButton> sidebarButtons = new ArrayList<>();

    public MainForm() {
        super("Hệ Thống Quản Lý Thời Khóa Biểu & Tài Nguyên Phòng Học - Đề Tài CNJ56");
        UIUtil.setSystemLookAndFeel();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Header Bar
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Left Sidebar Menu
        add(createSidebarPanel(), BorderLayout.WEST);

        // 3. Central Content Panel (CardLayout)
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(UIUtil.BG_LIGHT);

        pnlDashboard = new DashboardPanel(
                () -> showCard(CARD_SCHEDULE),
                () -> showCard(CARD_GRID),
                () -> showCard(CARD_ROOMS),
                () -> showCard(CARD_STATS)
        );
        pnlGrid = new TimetableGridPanel();
        pnlSchedule = new ThoiKhoaBieuPanel();
        pnlRooms = new PhongHocPanel();
        pnlTeachers = new GiangVienPanel();
        pnlSubjects = new MonHocPanel();
        pnlClasses = new LopHocPanel();
        pnlStats = new ThongKePanel();

        pnlContent.add(pnlDashboard, CARD_DASHBOARD);
        pnlContent.add(pnlGrid, CARD_GRID);
        pnlContent.add(pnlSchedule, CARD_SCHEDULE);
        pnlContent.add(pnlRooms, CARD_ROOMS);
        pnlContent.add(pnlTeachers, CARD_TEACHERS);
        pnlContent.add(pnlSubjects, CARD_SUBJECTS);
        pnlContent.add(pnlClasses, CARD_CLASSES);
        pnlContent.add(pnlStats, CARD_STATS);

        if (AuthService.getInstance().isAdmin()) {
            pnlUsers = new TaiKhoanPanel();
            pnlContent.add(pnlUsers, CARD_USERS);
        }

        add(pnlContent, BorderLayout.CENTER);

        // Hiển thị Dashboard mặc định
        if (!sidebarButtons.isEmpty()) {
            setActiveButton(sidebarButtons.get(0));
        }
        showCard(CARD_DASHBOARD);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtil.HEADER_BG);
        header.setPreferredSize(new Dimension(getWidth(), 52));
        header.setBorder(new EmptyBorder(0, 16, 0, 16));

        // Left App Branding
        JPanel leftBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        leftBrand.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ THỜI KHÓA BIỂU & PHÒNG HỌC");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblBadge = new JLabel(" CNJ56 ");
        lblBadge.setFont(UIUtil.FONT_BOLD);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(UIUtil.PRIMARY);
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        leftBrand.add(lblTitle);
        leftBrand.add(lblBadge);
        header.add(leftBrand, BorderLayout.WEST);

        // Right User Profile & Logout
        JPanel rightProfile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        rightProfile.setOpaque(false);

        TaiKhoan user = AuthService.getInstance().getCurrentUser();
        String roleText = (user != null && user.isAdmin()) ? "ADMIN" : "NHÂN VIÊN";
        String nameText = (user != null) ? user.getHoTen() : "User";

        JLabel lblUser = new JLabel("Người dùng: " + nameText + " [" + roleText + "]");
        lblUser.setFont(UIUtil.FONT_BOLD);
        lblUser.setForeground(new Color(226, 232, 240));

        JButton btnLogout = UIUtil.createDangerButton("Đăng Xuất");
        btnLogout.addActionListener(e -> performLogout());

        rightProfile.add(lblUser);
        rightProfile.add(btnLogout);
        header.add(rightProfile, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtil.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setBorder(new EmptyBorder(12, 10, 12, 10));

        sidebarButtons.clear();

        addSidebarButton(sidebar, "Trang Chủ", CARD_DASHBOARD);
        addSidebarButton(sidebar, "Lưới TKB Tuần", CARD_GRID);
        addSidebarButton(sidebar, "Xếp Lịch TKB", CARD_SCHEDULE);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarSeparator("DANH MỤC"));
        addSidebarButton(sidebar, "Phòng Học", CARD_ROOMS);
        addSidebarButton(sidebar, "Giảng Viên", CARD_TEACHERS);
        addSidebarButton(sidebar, "Môn Học", CARD_SUBJECTS);
        addSidebarButton(sidebar, "Lớp Học", CARD_CLASSES);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarSeparator("TIỆN ÍCH & HỆ THỐNG"));
        addSidebarButton(sidebar, "Thống Kê & Báo Cáo", CARD_STATS);

        if (AuthService.getInstance().isAdmin()) {
            addSidebarButton(sidebar, "Quản Trị Tài Khoản", CARD_USERS);
        }

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JLabel createSidebarSeparator(String title) {
        JLabel lbl = new JLabel(" " + title);
        lbl.setFont(UIUtil.FONT_SMALL);
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setBorder(new EmptyBorder(6, 4, 4, 4));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Nút bấm Sidebar tùy biến giao diện phẳng không phụ thuộc Windows Button UI.
     */
    private static class SidebarButton extends JButton {
        private boolean active = false;

        public SidebarButton(String text) {
            super(text);
            setFont(UIUtil.FONT_BOLD);
            setForeground(new Color(226, 232, 240));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setForeground(Color.WHITE);
            } else {
                setForeground(new Color(203, 213, 225));
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            if (active) {
                g2.setColor(UIUtil.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (getModel().isRollover()) {
                g2.setColor(UIUtil.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void addSidebarButton(JPanel parent, String text, String cardName) {
        SidebarButton btn = new SidebarButton(text);
        btn.addActionListener(e -> {
            setActiveButton(btn);
            showCard(cardName);
        });

        sidebarButtons.add(btn);
        parent.add(btn);
        parent.add(Box.createVerticalStrut(4));
    }

    private void setActiveButton(SidebarButton btn) {
        for (SidebarButton b : sidebarButtons) {
            b.setActive(false);
        }
        btn.setActive(true);
    }

    public void selectTabByIndex(int index) {
        if (index >= 0 && index < sidebarButtons.size()) {
            SidebarButton btn = sidebarButtons.get(index);
            setActiveButton(btn);
            for (java.awt.event.ActionListener al : btn.getActionListeners()) {
                al.actionPerformed(new java.awt.event.ActionEvent(btn, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }

    public void showCard(String cardName) {
        cardLayout.show(pnlContent, cardName);

        // Tự động làm mới dữ liệu khi chuyển tab
        if (CARD_DASHBOARD.equals(cardName) && pnlDashboard != null) {
            pnlDashboard.refreshData();
        } else if (CARD_GRID.equals(cardName) && pnlGrid != null) {
            pnlGrid.renderGrid();
        } else if (CARD_SCHEDULE.equals(cardName) && pnlSchedule != null) {
            pnlSchedule.loadDropdownFilters();
            pnlSchedule.loadData();
        } else if (CARD_ROOMS.equals(cardName) && pnlRooms != null) {
            pnlRooms.loadData();
        } else if (CARD_TEACHERS.equals(cardName) && pnlTeachers != null) {
            pnlTeachers.loadData();
        } else if (CARD_SUBJECTS.equals(cardName) && pnlSubjects != null) {
            pnlSubjects.loadData();
        } else if (CARD_CLASSES.equals(cardName) && pnlClasses != null) {
            pnlClasses.loadData();
        } else if (CARD_USERS.equals(cardName) && pnlUsers != null) {
            pnlUsers.loadData();
        }
    }

    private void performLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            dispose();
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        }
    }
}
