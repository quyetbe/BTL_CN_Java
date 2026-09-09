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
    private static final String CARD_PROPOSE = "CARD_PROPOSE";
    private static final String CARD_WORKFLOW = "CARD_WORKFLOW";
    private static final String CARD_CURRICULUM = "CARD_CURRICULUM";
    private static final String CARD_ROOMS = "CARD_ROOMS";
    private static final String CARD_TEACHERS = "CARD_TEACHERS";
    private static final String CARD_STUDENTS = "CARD_STUDENTS";
    private static final String CARD_SUBJECTS = "CARD_SUBJECTS";
    private static final String CARD_CLASSES = "CARD_CLASSES";
    private static final String CARD_STATS = "CARD_STATS";
    private static final String CARD_USERS = "CARD_USERS";
    private static final String CARD_AUDIT = "CARD_AUDIT";

    private CardLayout cardLayout;
    private JPanel pnlContent;

    private DashboardPanel pnlDashboard;
    private TimetableGridPanel pnlGrid;
    private ThoiKhoaBieuPanel pnlSchedule;
    private DeXuatDoiLichPanel pnlDeXuatDoiLich;
    private DoiLichPanel pnlDoiLich;
    private CurriculumPanel pnlCurriculum;
    private PhongHocPanel pnlRooms;
    private GiangVienPanel pnlTeachers;
    private SinhVienPanel pnlStudents;
    private MonHocPanel pnlSubjects;
    private LopHocPanel pnlClasses;
    private ThongKePanel pnlStats;
    private TaiKhoanPanel pnlUsers;
    private AuditLogPanel pnlAudit;

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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
                () -> showCard(CARD_STATS),
                () -> showCard(CARD_PROPOSE),
                () -> showCard(CARD_WORKFLOW),
                () -> showCard(CARD_CURRICULUM)
        );
        pnlGrid = new TimetableGridPanel();
        pnlSchedule = new ThoiKhoaBieuPanel();
        pnlDeXuatDoiLich = new DeXuatDoiLichPanel();
        pnlDoiLich = new DoiLichPanel();
        pnlCurriculum = new CurriculumPanel();
        pnlRooms = new PhongHocPanel();
        pnlTeachers = new GiangVienPanel();
        pnlStudents = new SinhVienPanel();
        pnlSubjects = new MonHocPanel();
        pnlClasses = new LopHocPanel();
        pnlStats = new ThongKePanel();
        pnlAudit = new AuditLogPanel();

        pnlContent.add(pnlDashboard, CARD_DASHBOARD);
        pnlContent.add(pnlGrid, CARD_GRID);
        pnlContent.add(pnlSchedule, CARD_SCHEDULE);
        pnlContent.add(pnlDeXuatDoiLich, CARD_PROPOSE);
        pnlContent.add(pnlDoiLich, CARD_WORKFLOW);
        pnlContent.add(pnlCurriculum, CARD_CURRICULUM);
        pnlContent.add(pnlRooms, CARD_ROOMS);
        pnlContent.add(pnlTeachers, CARD_TEACHERS);
        pnlContent.add(pnlStudents, CARD_STUDENTS);
        pnlContent.add(pnlSubjects, CARD_SUBJECTS);
        pnlContent.add(pnlClasses, CARD_CLASSES);
        pnlContent.add(pnlStats, CARD_STATS);
        pnlContent.add(pnlAudit, CARD_AUDIT);

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
        String roleText = AuthService.getInstance().getRoleBadgeText();
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
        AuthService auth = AuthService.getInstance();

        // 1. Nhóm Chính
        if (auth.canAccessDashboard()) {
            addSidebarButton(sidebar, "Trang Chủ", CARD_DASHBOARD);
        }
        if (auth.canAccessTimetableGrid()) {
            addSidebarButton(sidebar, "Lưới TKB Tuần", CARD_GRID);
        }
        if (auth.canAccessSchedule()) {
            addSidebarButton(sidebar, "Xếp Lịch TKB", CARD_SCHEDULE);
        }

        // 2. Nhóm Quy Trình & Đào Tạo
        boolean hasWorkflowGroup = auth.canProposeSchedule() || auth.canApproveSchedule() || auth.canAccessCurriculum();
        if (hasWorkflowGroup) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(createSidebarSeparator("QUY TRÌNH & ĐÀO TẠO"));
            if (auth.canProposeSchedule()) {
                addSidebarButton(sidebar, "Đề Xuất Đổi Lịch", CARD_PROPOSE);
            }
            if (auth.canApproveSchedule()) {
                addSidebarButton(sidebar, "Duyệt Đổi Lịch", CARD_WORKFLOW);
            }
            if (auth.canAccessCurriculum()) {
                addSidebarButton(sidebar, "Khung CTĐT 4 Năm", CARD_CURRICULUM);
            }
        }

        // 3. Nhóm Danh Mục
        boolean hasMasterGroup = auth.canAccessRooms() || auth.canAccessTeachers() || auth.canAccessStudents()
                || auth.canAccessSubjects() || auth.canAccessClasses();
        if (hasMasterGroup) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(createSidebarSeparator("DANH MỤC"));
            if (auth.canAccessRooms()) {
                addSidebarButton(sidebar, "Phòng Học", CARD_ROOMS);
            }
            if (auth.canAccessTeachers()) {
                addSidebarButton(sidebar, "Giảng Viên", CARD_TEACHERS);
            }
            if (auth.canAccessStudents()) {
                addSidebarButton(sidebar, "Sinh Viên", CARD_STUDENTS);
            }
            if (auth.canAccessSubjects()) {
                addSidebarButton(sidebar, "Môn Học", CARD_SUBJECTS);
            }
            if (auth.canAccessClasses()) {
                addSidebarButton(sidebar, "Lớp Học", CARD_CLASSES);
            }
        }

        // 4. Nhóm Tiện Ích & Hệ Thống
        boolean hasSystemGroup = auth.canAccessStatistics() || auth.canAccessAuditLog() || auth.canManageUsers();
        if (hasSystemGroup) {
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(createSidebarSeparator("TIỆN ÍCH & HỆ THỐNG"));
            if (auth.canAccessStatistics()) {
                addSidebarButton(sidebar, "Thống Kê & Báo Cáo", CARD_STATS);
            }
            if (auth.canAccessAuditLog()) {
                addSidebarButton(sidebar, "Nhật Ký Hệ Thống", CARD_AUDIT);
            }
            if (auth.canManageUsers()) {
                addSidebarButton(sidebar, "Quản Trị Tài Khoản", CARD_USERS);
            }
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

    public int getSidebarButtonCount() {
        return sidebarButtons.size();
    }

    public List<String> getSidebarButtonLabels() {
        List<String> labels = new ArrayList<>();
        for (SidebarButton b : sidebarButtons) {
            labels.add(b.getText());
        }
        return labels;
    }

    public void showCard(String cardName) {
        AuthService auth = AuthService.getInstance();
        if (CARD_SCHEDULE.equals(cardName) && !auth.canAccessSchedule()) cardName = CARD_DASHBOARD;
        else if (CARD_PROPOSE.equals(cardName) && !auth.canProposeSchedule()) cardName = CARD_DASHBOARD;
        else if (CARD_WORKFLOW.equals(cardName) && !auth.canApproveSchedule()) cardName = CARD_DASHBOARD;
        else if (CARD_ROOMS.equals(cardName) && !auth.canAccessRooms()) cardName = CARD_DASHBOARD;
        else if (CARD_TEACHERS.equals(cardName) && !auth.canAccessTeachers()) cardName = CARD_DASHBOARD;
        else if (CARD_STUDENTS.equals(cardName) && !auth.canAccessStudents()) cardName = CARD_DASHBOARD;
        else if (CARD_SUBJECTS.equals(cardName) && !auth.canAccessSubjects()) cardName = CARD_DASHBOARD;
        else if (CARD_CLASSES.equals(cardName) && !auth.canAccessClasses()) cardName = CARD_DASHBOARD;
        else if (CARD_STATS.equals(cardName) && !auth.canAccessStatistics()) cardName = CARD_DASHBOARD;
        else if (CARD_AUDIT.equals(cardName) && !auth.canAccessAuditLog()) cardName = CARD_DASHBOARD;
        else if (CARD_USERS.equals(cardName) && !auth.canManageUsers()) cardName = CARD_DASHBOARD;

        cardLayout.show(pnlContent, cardName);

        // Tự động làm mới dữ liệu khi chuyển tab
        if (CARD_DASHBOARD.equals(cardName) && pnlDashboard != null) {
            pnlDashboard.refreshData();
        } else if (CARD_GRID.equals(cardName) && pnlGrid != null) {
            pnlGrid.renderGrid();
        } else if (CARD_SCHEDULE.equals(cardName) && pnlSchedule != null) {
            pnlSchedule.loadDropdownFilters();
            pnlSchedule.loadData();
        } else if (CARD_PROPOSE.equals(cardName) && pnlDeXuatDoiLich != null) {
            pnlDeXuatDoiLich.loadData();
        } else if (CARD_WORKFLOW.equals(cardName) && pnlDoiLich != null) {
            pnlDoiLich.loadData();
        } else if (CARD_CURRICULUM.equals(cardName) && pnlCurriculum != null) {
            pnlCurriculum.loadData();
        } else if (CARD_ROOMS.equals(cardName) && pnlRooms != null) {
            pnlRooms.loadData();
        } else if (CARD_TEACHERS.equals(cardName) && pnlTeachers != null) {
            pnlTeachers.loadData();
        } else if (CARD_STUDENTS.equals(cardName) && pnlStudents != null) {
            pnlStudents.loadData();
        } else if (CARD_SUBJECTS.equals(cardName) && pnlSubjects != null) {
            pnlSubjects.loadData();
        } else if (CARD_CLASSES.equals(cardName) && pnlClasses != null) {
            pnlClasses.loadData();
        } else if (CARD_STATS.equals(cardName) && pnlStats != null) {
            pnlStats.loadAllData();
        } else if (CARD_USERS.equals(cardName) && pnlUsers != null) {
            pnlUsers.loadData();
        } else if (CARD_AUDIT.equals(cardName) && pnlAudit != null) {
            pnlAudit.loadData();
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
