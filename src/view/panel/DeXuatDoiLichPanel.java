package view.panel;

import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import dao.YeuCauDoiLichDAO;
import model.PhongHoc;
import model.TaiKhoan;
import model.ThoiKhoaBieu;
import model.YeuCauDoiLich;
import service.AuthService;
import service.WorkflowService;
import service.XepLichService;
import util.UIUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Phân hệ Độc lập: Đề Xuất Đổi Lịch Giảng Dạy.
 * Cho phép Giảng viên / Phòng Đào tạo lập đề xuất đổi lịch, kiểm tra xung đột trực tiếp,
 * và theo dõi trạng thái phê duyệt qua từng cấp.
 */
public class DeXuatDoiLichPanel extends JPanel {

    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final YeuCauDoiLichDAO yeuCauDAO;
    private final WorkflowService workflowService;
    private final XepLichService xepLichService;

    // Components - Form Đề Xuất
    private JComboBox<TkbItem> cbCaHoc;
    private JComboBox<String> cbThuMoi;
    private JSpinner spTietBatDau;
    private JSpinner spSoTiet;
    private JComboBox<String> cbPhongMoi;
    private JSpinner spTuanBatDau;
    private JSpinner spTuanKetThuc;
    private JTextField txtLyDo;
    private JButton btnCheckConflict;
    private JButton btnSubmit;
    private JButton btnClear;

    // Components - Danh Sách Đề Xuất
    private JTable tblRequests;
    private DefaultTableModel tableModel;
    private PaginationBar paginationBar;
    private List<YeuCauDoiLich> fullRequests = new ArrayList<>();
    private JButton btnCancelRequest;
    private JButton btnRefresh;

    public DeXuatDoiLichPanel() {
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.yeuCauDAO = new YeuCauDoiLichDAO();
        this.workflowService = new WorkflowService();
        this.xepLichService = new XepLichService();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. Top Title
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐỀ XUẤT ĐỔI LỊCH GIẢNG DẠY (GIẢNG VIÊN & ĐÀO TẠO)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        JLabel lblSub = new JLabel("Lập đề xuất đổi ca học, kiểm tra xung đột phòng/giảng viên tức thời và theo dõi tiến độ duyệt");
        lblSub.setFont(UIUtil.FONT_SMALL);
        lblSub.setForeground(UIUtil.TEXT_MUTED);
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Center Panel (Split into Form and Table)
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);

        // 2.1 Form Panel
        JPanel pnlFormCard = new JPanel(new BorderLayout(0, 8));
        pnlFormCard.setBackground(Color.WHITE);
        pnlFormCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblFormTitle = new JLabel("📝 THÔNG TIN ĐỀ XUẤT ĐỔI CA HỌC MỚI");
        lblFormTitle.setFont(UIUtil.FONT_BOLD);
        lblFormTitle.setForeground(UIUtil.PRIMARY);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFormGrid = new JPanel(new GridBagLayout());
        pnlFormGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Chọn ca học gốc
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        pnlFormGrid.add(createLabel("Ca học cần đổi:"), gbc);

        cbCaHoc = new JComboBox<>();
        cbCaHoc.setFont(UIUtil.FONT_REGULAR);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 0.85;
        pnlFormGrid.add(cbCaHoc, gbc);

        // Row 2: Thứ mới, Tiết bắt đầu, Số tiết, Phòng mới
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        pnlFormGrid.add(createLabel("Thứ mới:"), gbc);

        cbThuMoi = new JComboBox<>(new String[]{"Thứ Hai (2)", "Thứ Ba (3)", "Thứ Tư (4)", "Thứ Năm (5)", "Thứ Sáu (6)", "Thứ Bảy (7)"});
        cbThuMoi.setFont(UIUtil.FONT_REGULAR);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35;
        pnlFormGrid.add(cbThuMoi, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.15;
        pnlFormGrid.add(createLabel("Phòng học mới:"), gbc);

        cbPhongMoi = new JComboBox<>();
        cbPhongMoi.setFont(UIUtil.FONT_REGULAR);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.35;
        pnlFormGrid.add(cbPhongMoi, gbc);

        // Row 3: Tiết bắt đầu, Số tiết, Tuần BD, Tuần KT
        JPanel pnlTimeInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTimeInputs.setOpaque(false);

        spTietBatDau = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spSoTiet = new JSpinner(new SpinnerNumberModel(5, 1, 6, 1));
        spTuanBatDau = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        spTuanKetThuc = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));

        pnlTimeInputs.add(new JLabel("Tiết BĐ:"));
        pnlTimeInputs.add(spTietBatDau);
        pnlTimeInputs.add(new JLabel("Số tiết:"));
        pnlTimeInputs.add(spSoTiet);
        pnlTimeInputs.add(new JLabel("Tuần BĐ:"));
        pnlTimeInputs.add(spTuanBatDau);
        pnlTimeInputs.add(new JLabel("Tuần KT:"));
        pnlTimeInputs.add(spTuanKetThuc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        pnlFormGrid.add(createLabel("Thời gian đề xuất:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 0.85;
        pnlFormGrid.add(pnlTimeInputs, gbc);

        // Row 4: Lý do đề xuất
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.15;
        pnlFormGrid.add(createLabel("Lý do đề xuất:"), gbc);

        txtLyDo = new JTextField();
        txtLyDo.setFont(UIUtil.FONT_REGULAR);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; gbc.weightx = 0.85;
        pnlFormGrid.add(txtLyDo, gbc);

        // Row 5: Action buttons
        JPanel pnlBtnForm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtnForm.setOpaque(false);

        btnCheckConflict = UIUtil.createPrimaryButton("Kiểm Tra Xung Đột");
        btnSubmit = UIUtil.createSuccessButton("Gửi Đề Xuất Đổi Lịch");
        btnClear = UIUtil.createSecondaryButton("Làm Mới Form");

        pnlBtnForm.add(btnClear);
        pnlBtnForm.add(btnCheckConflict);
        pnlBtnForm.add(btnSubmit);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.insets = new Insets(10, 6, 2, 6);
        pnlFormGrid.add(pnlBtnForm, gbc);

        pnlFormCard.add(pnlFormGrid, BorderLayout.CENTER);
        pnlCenter.add(pnlFormCard, BorderLayout.NORTH);

        // 2.2 Table Panel - Danh sách đề xuất đã gửi
        JPanel pnlTableCard = new JPanel(new BorderLayout(0, 6));
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JPanel pnlTableTop = new JPanel(new BorderLayout());
        pnlTableTop.setOpaque(false);
        JLabel lblListTitle = new JLabel("📋 DANH SÁCH CÁC ĐỀ XUẤT ĐÃ GỬI & TIẾN ĐỘ PHÊ DUYỆT");
        lblListTitle.setFont(UIUtil.FONT_BOLD);
        lblListTitle.setForeground(UIUtil.TEXT_DARK);

        JPanel pnlTableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlTableActions.setOpaque(false);
        btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnCancelRequest = UIUtil.createDangerButton("Thu Hồi Đề Xuất");
        pnlTableActions.add(btnRefresh);
        pnlTableActions.add(btnCancelRequest);

        pnlTableTop.add(lblListTitle, BorderLayout.WEST);
        pnlTableTop.add(pnlTableActions, BorderLayout.EAST);
        pnlTableCard.add(pnlTableTop, BorderLayout.NORTH);

        // Table
        String[] columns = {"STT", "Mã YC", "Môn Học", "Lớp Học", "Giảng Viên", "Lịch Đề Xuất Mới", "Lý Do", "Trạng Thái", "Cấp Duyệt", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblRequests = new JTable(tableModel);
        UIUtil.formatTable(tblRequests);
        tblRequests.setRowHeight(34);
        tblRequests.setFont(UIUtil.FONT_REGULAR);
        tblRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblRequests.getColumnModel().getColumn(0).setMaxWidth(55);
        tblRequests.getColumnModel().getColumn(1).setMaxWidth(70);

        // Căn giữa TOÀN BỘ các cột trong bảng
        UIUtil.centerAllColumns(tblRequests);
        tblRequests.getColumnModel().getColumn(7).setCellRenderer(new RequestStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(tblRequests);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);

        // Pagination
        paginationBar = new PaginationBar(20);
        paginationBar.setPageChangeListener(page -> renderPage(page));
        pnlTableCard.add(paginationBar, BorderLayout.SOUTH);

        pnlCenter.add(pnlTableCard, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Event listeners
        btnCheckConflict.addActionListener(e -> performCheckConflict());
        btnSubmit.addActionListener(e -> performSubmit());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
        btnCancelRequest.addActionListener(e -> performCancel());
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtil.FONT_BOLD);
        lbl.setForeground(UIUtil.TEXT_DARK);
        return lbl;
    }

    public void loadData() {
        // 1. Load combobox Ca học
        cbCaHoc.removeAllItems();
        List<ThoiKhoaBieu> tkbList = thoiKhoaBieuDAO.getAll();
        for (ThoiKhoaBieu tkb : tkbList) {
            cbCaHoc.addItem(new TkbItem(tkb));
        }

        // 2. Load combobox Phòng mới
        cbPhongMoi.removeAllItems();
        List<PhongHoc> phongList = phongHocDAO.getAll();
        for (PhongHoc ph : phongList) {
            cbPhongMoi.addItem(ph.getMaPhong() + " - " + ph.getTenPhong());
        }

        // 3. Load danh sách đề xuất
        fullRequests = yeuCauDAO.getAll();
        renderPage(1);
    }

    private void renderPage(int page) {
        tableModel.setRowCount(0);
        paginationBar.update(page, 20, fullRequests.size());
        List<YeuCauDoiLich> pageItems = PaginationBar.getPageSlice(fullRequests, page, 20);
        int startStt = (page - 1) * 20 + 1;

        for (int i = 0; i < pageItems.size(); i++) {
            YeuCauDoiLich r = pageItems.get(i);
            String lichMoi = "Thứ " + r.getThuMoi() + ", Tiết " + r.getTietBatDauMoi() + "-" + (r.getTietBatDauMoi() + r.getSoTiet() - 1)
                    + " (" + r.getMaPhongMoi() + ") Tuần " + r.getTuanBatDauMoi() + "-" + r.getTuanKetThucMoi();

            tableModel.addRow(new Object[]{
                    startStt + i,
                    r.getId(),
                    r.getTenMon() != null ? r.getTenMon() : ("TKB #" + r.getMaTkb()),
                    r.getTenLop() != null ? r.getTenLop() : "",
                    r.getHoTenGv() != null ? r.getHoTenGv() : r.getMaGv(),
                    lichMoi,
                    r.getLyDo(),
                    r.getTrangThai(),
                    "Cấp " + r.getCapPheDuyet(),
                    r.getNgayTao() != null ? r.getNgayTao().toString().substring(0, 16) : ""
            });
        }
    }

    private void performCheckConflict() {
        TkbItem item = (TkbItem) cbCaHoc.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca học cần đổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ThoiKhoaBieu orig = item.tkb;
        String phongMoi = getSelectedPhongCode();
        int thuMoi = cbThuMoi.getSelectedIndex() + 2;
        int tietBD = (int) spTietBatDau.getValue();
        int soTiet = (int) spSoTiet.getValue();
        int tuanBD = (int) spTuanBatDau.getValue();
        int tuanKT = (int) spTuanKetThuc.getValue();

        ThoiKhoaBieu target = new ThoiKhoaBieu();
        target.setMaMon(orig.getMaMon());
        target.setMaLop(orig.getMaLop());
        target.setMaGv(orig.getMaGv());
        target.setMaPhong(phongMoi);
        target.setThuTrongTuan(thuMoi);
        target.setTietBatDau(tietBD);
        target.setSoTiet(soTiet);
        target.setTietKetThuc(tietBD + soTiet - 1);
        target.setTuanBatDau(tuanBD);
        target.setTuanKetThuc(tuanKT);
        target.setHocKy(orig.getHocKy());
        target.setNamHoc(orig.getNamHoc());

        XepLichService.ConflictResult conflict = xepLichService.validateAndCheckConflict(target, orig.getId());
        if (conflict.isValid()) {
            JOptionPane.showMessageDialog(this,
                    "✅ Ca học mới HOÀN TOÀN HỢP LỆ, KHÔNG CÓ XUNG ĐỘT!\n\n"
                            + "- Phòng: " + phongMoi + " còn trống trong khung giờ này.\n"
                            + "- Giảng viên: " + orig.getMaGv() + " không bị trùng lịch dạy.\n"
                            + "- Lớp: " + orig.getMaLop() + " không có môn khác trùng giờ.",
                    "Kiểm Tra Xung Đột Thành Công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ PHÁT HIỆN XUNG ĐỘT LỊCH:\n\n" + conflict.getMessage(),
                    "Cảnh Báo Xung Đột",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSubmit() {
        TkbItem item = (TkbItem) cbCaHoc.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca học cần đổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String lyDo = txtLyDo.getText().trim();
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do đề xuất đổi lịch!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtLyDo.requestFocus();
            return;
        }

        ThoiKhoaBieu orig = item.tkb;
        String phongMoi = getSelectedPhongCode();
        int thuMoi = cbThuMoi.getSelectedIndex() + 2;
        int tietBD = (int) spTietBatDau.getValue();
        int soTiet = (int) spSoTiet.getValue();
        int tuanBD = (int) spTuanBatDau.getValue();
        int tuanKT = (int) spTuanKetThuc.getValue();

        YeuCauDoiLich req = new YeuCauDoiLich();
        req.setMaTkb(orig.getId());
        req.setMaGv(orig.getMaGv());
        req.setMaPhongMoi(phongMoi);
        req.setThuMoi(thuMoi);
        req.setTietBatDauMoi(tietBD);
        req.setSoTiet(soTiet);
        req.setTuanBatDauMoi(tuanBD);
        req.setTuanKetThucMoi(tuanKT);
        req.setLyDo(lyDo);
        req.setTrangThai("CHO_KHOA_DUYET");
        req.setCapPheDuyet(1);

        String result = workflowService.submitRequest(req);
        if (result.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, result, "Gửi đề xuất thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi đề xuất", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performCancel() {
        int row = tblRequests.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đề xuất trong danh sách để thu hồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reqId = (int) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 7);

        if ("DA_PHE_DUYET".equals(status)) {
            JOptionPane.showMessageDialog(this, "Đề xuất này đã được Ban Giám Hiệu phê duyệt chính thức, không thể thu hồi!", "Không thể thu hồi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn thu hồi và xóa đề xuất ID #" + reqId + " này không?",
                "Xác nhận thu hồi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = yeuCauDAO.delete(reqId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã thu hồi đề xuất thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa đề xuất khỏi cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        if (cbCaHoc.getItemCount() > 0) cbCaHoc.setSelectedIndex(0);
        cbThuMoi.setSelectedIndex(0);
        if (cbPhongMoi.getItemCount() > 0) cbPhongMoi.setSelectedIndex(0);
        spTietBatDau.setValue(1);
        spSoTiet.setValue(5);
        spTuanBatDau.setValue(1);
        spTuanKetThuc.setValue(7);
        txtLyDo.setText("");
    }

    private String getSelectedPhongCode() {
        String item = (String) cbPhongMoi.getSelectedItem();
        if (item != null && item.contains(" - ")) {
            return item.split(" - ")[0].trim();
        }
        return item != null ? item : "PM101";
    }

    // Helper item for Combobox
    private static class TkbItem {
        final ThoiKhoaBieu tkb;

        TkbItem(ThoiKhoaBieu tkb) {
            this.tkb = tkb;
        }

        @Override
        public String toString() {
            return "ID " + tkb.getId() + ": " + (tkb.getTenMon() != null ? tkb.getTenMon() : tkb.getMaMon())
                    + " | Lớp: " + tkb.getMaLop() + " | Thứ " + tkb.getThuTrongTuan()
                    + " (Tiết " + tkb.getTietBatDau() + "-" + tkb.getTietKetThuc() + ")"
                    + " | Phòng: " + tkb.getMaPhong() + " | " + tkb.getNamHoc();
        }
    }

    // Status Cell Renderer with Badges
    private static class RequestStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            String status = value != null ? value.toString() : "";
            switch (status) {
                case "CHO_KHOA_DUYET":
                    label.setText("Chờ Khoa Duyệt");
                    label.setForeground(new Color(217, 119, 6)); // Amber
                    break;
                case "CHO_BGH_DUYET":
                    label.setText("Chờ BGH Duyệt");
                    label.setForeground(new Color(37, 99, 235)); // Blue
                    break;
                case "DA_PHE_DUYET":
                    label.setText("Đã Phê Duyệt");
                    label.setForeground(new Color(22, 163, 74)); // Green
                    break;
                case "TU_CHOI":
                    label.setText("Từ Chối");
                    label.setForeground(new Color(220, 38, 38)); // Red
                    break;
                default:
                    label.setText(status);
                    label.setForeground(UIUtil.TEXT_DARK);
            }
            return label;
        }
    }
}