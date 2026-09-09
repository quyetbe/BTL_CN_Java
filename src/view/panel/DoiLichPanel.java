package view.panel;

import controller.RescheduleController;
import dao.YeuCauDoiLichDAO;
import model.LichSuDuyet;
import model.TaiKhoan;
import model.YeuCauDoiLich;
import service.AuthService;
import util.UIUtil;
import view.dialog.DoiLichDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Phân hệ Quản lý & Phê duyệt Đổi Lịch Giảng Dạy Đa Cấp (Workflow Approval).
 * Cấp 1: Trưởng Khoa / Bộ Môn (CHO_KHOA_DUYET -> CHO_BGH_DUYET)
 * Cấp 2: Ban Giám Hiệu / Đào Tạo (CHO_BGH_DUYET -> DA_PHE_DUYET & Atomic TKB Update)
 */
public class DoiLichPanel extends JPanel {

    private final RescheduleController rescheduleController;
    private final YeuCauDoiLichDAO yeuCauDAO;

    private JComboBox<String> cbFilterStatus;
    private JTable tblRequests;
    private DefaultTableModel tableModel;
    private List<YeuCauDoiLich> requestList;

    private JButton btnApproveLevel1;
    private JButton btnApproveLevel2;
    private JButton btnReject;
    private JButton btnViewHistory;

    public DoiLichPanel() {
        this.rescheduleController = new RescheduleController();
        this.yeuCauDAO = new YeuCauDoiLichDAO();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // 1. Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("QUY TRÌNH PHÊ DUYỆT ĐỔI LỊCH GIẢNG DẠY (2 CẤP)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Filter toolbar
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlToolbar.setBackground(Color.WHITE);
        pnlToolbar.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlToolbar.add(new JLabel("Lọc theo trạng thái:"));
        cbFilterStatus = new JComboBox<>(new String[]{
            "Tất cả hồ sơ",
            "Chờ Khoa duyệt (Cấp 1)",
            "Chờ BGH duyệt (Cấp 2)",
            "Đã phê duyệt",
            "Đã từ chối"
        });
        cbFilterStatus.addActionListener(e -> filterData());
        pnlToolbar.add(cbFilterStatus);

        JButton btnNew = UIUtil.createPrimaryButton("+ Tạo Đề Xuất Đổi Lịch");
        btnNew.addActionListener(e -> openNewRequestDialog());
        pnlToolbar.add(btnNew);

        btnApproveLevel1 = UIUtil.createSecondaryButton("Khoa Duyệt (Cấp 1)");
        btnApproveLevel1.addActionListener(e -> handleApproveLevel1());
        pnlToolbar.add(btnApproveLevel1);

        btnApproveLevel2 = UIUtil.createSuccessButton("BGH Duyệt Cuối (Cấp 2)");
        btnApproveLevel2.addActionListener(e -> handleApproveLevel2());
        pnlToolbar.add(btnApproveLevel2);

        btnReject = UIUtil.createDangerButton("Từ Chối");
        btnReject.addActionListener(e -> handleReject());
        pnlToolbar.add(btnReject);

        btnViewHistory = UIUtil.createSecondaryButton("Lịch Sử Duyệt");
        btnViewHistory.addActionListener(e -> showHistoryModal());
        pnlToolbar.add(btnViewHistory);

        JButton btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnRefresh.addActionListener(e -> loadData());
        pnlToolbar.add(btnRefresh);

        pnlTop.add(pnlToolbar, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table
        String[] headers = {
            "Mã ĐX", "Giảng Viên", "Môn Học", "Lớp Học",
            "Lịch Hiện Tại", "Lịch Đề Xuất Mới", "Phòng Cũ -> Mới", "Tuần", "Trạng Thái", "Ngày Gửi"
        };

        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRequests = new JTable(tableModel);
        UIUtil.formatTable(tblRequests);
        tblRequests.setRowHeight(36);
        tblRequests.setFont(UIUtil.FONT_REGULAR);
        tblRequests.setGridColor(UIUtil.BORDER_COLOR);
        tblRequests.setShowGrid(true);
        tblRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblRequests.setDefaultRenderer(Object.class, new RequestCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tblRequests);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Note
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBottom.setOpaque(false);
        JLabel lblNote = new JLabel("Lưu ý: BGH phê duyệt (Cấp 2) sẽ tự động cập nhật Thời khóa biểu ngay lập tức bằng Atomic Transaction.");
        lblNote.setFont(UIUtil.FONT_SMALL);
        lblNote.setForeground(UIUtil.TEXT_MUTED);
        pnlBottom.add(lblNote);
        add(pnlBottom, BorderLayout.SOUTH);

        applyPermissions();
    }

    private void applyPermissions() {
        AuthService auth = AuthService.getInstance();
        if (auth.isTruongKhoa()) {
            btnApproveLevel1.setEnabled(true);
            btnApproveLevel2.setEnabled(false);
            btnApproveLevel2.setToolTipText("Chỉ Ban Giám Hiệu mới có thẩm quyền phê duyệt Cấp 2");
        } else if (auth.isBanGiamHieu()) {
            btnApproveLevel1.setEnabled(false);
            btnApproveLevel1.setToolTipText("Chỉ Trưởng Khoa mới có thẩm quyền phê duyệt Cấp 1");
            btnApproveLevel2.setEnabled(true);
        } else if (auth.isAdmin() || auth.isPhongDaoTao()) {
            btnApproveLevel1.setEnabled(true);
            btnApproveLevel2.setEnabled(true);
        }
    }

    public void loadData() {
        applyPermissions();
        requestList = rescheduleController.getAllRequests();
        filterData();
    }

    private void filterData() {
        if (requestList == null) return;
        tableModel.setRowCount(0);

        int selectedFilter = cbFilterStatus.getSelectedIndex();
        String targetStatus = null;
        if (selectedFilter == 1) targetStatus = "CHO_KHOA_DUYET";
        else if (selectedFilter == 2) targetStatus = "CHO_BGH_DUYET";
        else if (selectedFilter == 3) targetStatus = "DA_PHE_DUYET";
        else if (selectedFilter == 4) targetStatus = "TU_CHOI";

        for (YeuCauDoiLich r : requestList) {
            if (targetStatus != null && !targetStatus.equalsIgnoreCase(r.getTrangThai())) {
                continue;
            }

            String lichCu = String.format("Thứ %d (T%d-T%d)", r.getThuCu() > 0 ? r.getThuCu() : 2, r.getTietBatDauCu(), r.getTietBatDauCu() + r.getSoTiet() - 1);
            String lichMoi = String.format("Thứ %d (T%d-T%d)", r.getThuMoi(), r.getTietBatDauMoi(), r.getTietBatDauMoi() + r.getSoTiet() - 1);
            String phong = (r.getMaPhongCu() != null ? r.getMaPhongCu() : "---") + " ➔ " + r.getMaPhongMoi();
            String tuan = r.getTuanBatDauMoi() + " - " + r.getTuanKetThucMoi();

            tableModel.addRow(new Object[]{
                "#" + r.getId(),
                r.getHoTenGv() != null ? r.getHoTenGv() : r.getMaGv(),
                r.getTenMon() != null ? r.getTenMon() : "Môn học #" + r.getMaTkb(),
                r.getTenLop() != null ? r.getTenLop() : "---",
                lichCu,
                lichMoi,
                phong,
                tuan,
                r.getTrangThai(),
                r.getNgayTao() != null ? r.getNgayTao().toString().substring(0, 16) : ""
            });
        }
    }

    private YeuCauDoiLich getSelectedRequest() {
        int row = tblRequests.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ đổi lịch từ danh sách!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String idStr = (String) tableModel.getValueAt(row, 0);
        int reqId = Integer.parseInt(idStr.replace("#", ""));
        for (YeuCauDoiLich r : requestList) {
            if (r.getId() == reqId) return r;
        }
        return null;
    }

    private void openNewRequestDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        DoiLichDialog dialog = new DoiLichDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadData();
        }
    }

    private void handleApproveLevel1() {
        if (!AuthService.getInstance().isAdmin() && !AuthService.getInstance().isTruongKhoa() && !AuthService.getInstance().isPhongDaoTao()) {
            JOptionPane.showMessageDialog(this, "Bạn không có thẩm quyền duyệt Cấp 1 (Yêu cầu vai trò Trưởng Khoa hoặc Admin)!", "Không đủ thẩm quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        YeuCauDoiLich req = getSelectedRequest();
        if (req == null) return;

        if (!"CHO_KHOA_DUYET".equalsIgnoreCase(req.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể duyệt Cấp 1 đối với các hồ sơ đang ở trạng thái 'CHO_KHOA_DUYET'!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = JOptionPane.showInputDialog(this, "Nhập ý kiến phê duyệt của Khoa:", "Duyệt Cấp 1 (Khoa)", JOptionPane.QUESTION_MESSAGE);
        if (comment == null) return;

        TaiKhoan user = AuthService.getInstance().getCurrentUser();
        int userId = user != null ? user.getId() : 1;
        String userName = user != null ? user.getHoTen() : "Trưởng Khoa";

        boolean ok = rescheduleController.reviewLevel1(req.getId(), userId, userName, true, comment.isEmpty() ? "Khoa đồng ý đề xuất." : comment);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Hồ sơ #" + req.getId() + " đã được Khoa duyệt thành công và chuyển lên BGH (Cấp 2)!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Duyệt Cấp 1 thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApproveLevel2() {
        if (!AuthService.getInstance().isAdmin() && !AuthService.getInstance().isBanGiamHieu()) {
            JOptionPane.showMessageDialog(this, "Bạn không có thẩm quyền duyệt Cấp 2 (Yêu cầu vai trò Ban Giám Hiệu hoặc Admin)!", "Không đủ thẩm quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        YeuCauDoiLich req = getSelectedRequest();
        if (req == null) return;

        if (!"CHO_BGH_DUYET".equalsIgnoreCase(req.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể phê duyệt Cấp 2 đối với các hồ sơ đã qua Cấp 1 (trạng thái 'CHO_BGH_DUYET')!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "XÁC NHẬN PHÊ DUYỆT CUỐI:\n"
                + "Thao tác này sẽ áp dụng ngay thay đổi phòng học & tiết dạy vào Thời khóa biểu chính thức.\n"
                + "Bạn có chắc chắn muốn phê duyệt không?",
                "Phê duyệt Ban Giám Hiệu",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        String comment = JOptionPane.showInputDialog(this, "Nhập ý kiến chỉ đạo của Ban Giám Hiệu:", "Ý kiến BGH", JOptionPane.QUESTION_MESSAGE);
        if (comment == null) comment = "BGH phê chuẩn điều chỉnh thời khóa biểu.";

        TaiKhoan user = AuthService.getInstance().getCurrentUser();
        int userId = user != null ? user.getId() : 1;
        String userName = user != null ? user.getHoTen() : "Ban Giám Hiệu";

        boolean ok = rescheduleController.reviewLevel2(req.getId(), userId, userName, true, comment);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Phê duyệt Cấp 2 thành công! Thời khóa biểu đã được cập nhật tự động.", "Hoàn tất phê duyệt", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Phê duyệt thất bại hoặc có lỗi cập nhật DB!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReject() {
        YeuCauDoiLich req = getSelectedRequest();
        if (req == null) return;

        if ("DA_PHE_DUYET".equalsIgnoreCase(req.getTrangThai()) || "TU_CHOI".equalsIgnoreCase(req.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Hồ sơ này đã kết thúc xử lý (" + req.getTrangThai() + "), không thể thay đổi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Nhập lý do từ chối hồ sơ #" + req.getId() + ":", "Từ chối yêu cầu", JOptionPane.WARNING_MESSAGE);
        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần cung cấp lý do từ chối!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan user = AuthService.getInstance().getCurrentUser();
        int userId = user != null ? user.getId() : 1;
        String userName = user != null ? user.getHoTen() : "Quản trị viên";

        boolean ok;
        if ("CHO_KHOA_DUYET".equalsIgnoreCase(req.getTrangThai())) {
            ok = rescheduleController.reviewLevel1(req.getId(), userId, userName, false, reason);
        } else {
            ok = rescheduleController.reviewLevel2(req.getId(), userId, userName, false, reason);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã từ chối hồ sơ #" + req.getId() + ".", "Đã xử lý", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác từ chối thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistoryModal() {
        YeuCauDoiLich req = getSelectedRequest();
        if (req == null) return;

        List<LichSuDuyet> history = yeuCauDAO.getHistory(req.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("=== LỊCH SỬ PHÊ DUYỆT HỒ SƠ ĐỔI LỊCH #").append(req.getId()).append(" ===\n\n");
        sb.append("• Lý do đổi lịch của GV: ").append(req.getLyDo()).append("\n");
        sb.append("• Trạng thái hiện thời: ").append(req.getTrangThai()).append("\n\n");

        if (history.isEmpty()) {
            sb.append("(Chưa có bước duyệt nào được ghi nhận)\n");
        } else {
            for (int i = 0; i < history.size(); i++) {
                LichSuDuyet ls = history.get(i);
                sb.append(String.format("[%d] Cấp duyệt: %s | Quyết định: %s\n", i + 1, ls.getCapDuyet(), ls.getQuyetDinh()));
                sb.append("    Ý kiến: ").append(ls.getYKien()).append("\n");
                sb.append("    Thời gian: ").append(ls.getThoiGianTao() != null ? ls.getThoiGianTao().toString() : "").append("\n\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString(), 14, 45);
        ta.setFont(UIUtil.FONT_REGULAR);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Chi Tiết Lịch Sử Duyệt", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class RequestCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (column == 8 && value != null) { // Cột Trạng thái
                String status = value.toString();
                setFont(UIUtil.FONT_BOLD);

                if ("CHO_KHOA_DUYET".equalsIgnoreCase(status)) {
                    setText("Chờ Khoa Duyệt");
                    setForeground(new Color(202, 138, 4));
                } else if ("CHO_BGH_DUYET".equalsIgnoreCase(status)) {
                    setText("Chờ BGH Duyệt");
                    setForeground(new Color(37, 99, 235));
                } else if ("DA_PHE_DUYET".equalsIgnoreCase(status)) {
                    setText("Đã Phê Duyệt");
                    setForeground(new Color(22, 163, 74));
                } else if ("TU_CHOI".equalsIgnoreCase(status)) {
                    setText("Bị Từ Chối");
                    setForeground(new Color(220, 38, 38));
                }
            } else {
                setForeground(UIUtil.TEXT_DARK);
                if (column == 0 || column == 1) {
                    setFont(UIUtil.FONT_BOLD);
                } else {
                    setFont(UIUtil.FONT_REGULAR);
                }
            }
            return c;
        }
    }
}
