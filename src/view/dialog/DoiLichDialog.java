package view.dialog;

import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import model.PhongHoc;
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
import java.awt.*;
import java.util.List;

/**
 * Hộp thoại Đề xuất đổi lịch giảng dạy (Dành cho Giảng viên / Quản lý đào tạo).
 * Khởi tạo quy trình phê duyệt 2 cấp: Khoa -> Ban Giám Hiệu.
 */
public class DoiLichDialog extends JDialog {

    private final WorkflowService workflowService;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final XepLichService xepLichService;

    private JComboBox<TkbItem> cbTkb;
    private JComboBox<String> cbThuMoi;
    private JSpinner spnrTietBatDauMoi;
    private JSpinner spnrSoTiet;
    private JComboBox<PhongItem> cbPhongMoi;
    private JSpinner spnrTuanBatDauMoi;
    private JSpinner spnrTuanKetThucMoi;
    private JTextArea txtLyDo;
    private JLabel lblConflictCheck;

    private boolean submitted = false;

    private static class TkbItem {
        final ThoiKhoaBieu tkb;
        TkbItem(ThoiKhoaBieu tkb) { this.tkb = tkb; }
        @Override
        public String toString() {
            return String.format("[ID %d] %s (%s) | %s | T%d-T%d | Phòng: %s",
                    tkb.getId(), tkb.getTenMon(), tkb.getMaLop(),
                    tkb.getThuText(), tkb.getTietBatDau(), tkb.getTietKetThuc(), tkb.getMaPhong());
        }
    }

    private static class PhongItem {
        final PhongHoc phong;
        PhongItem(PhongHoc p) { this.phong = p; }
        @Override
        public String toString() {
            return phong.getMaPhong() + " - " + phong.getTenPhong() + " (" + phong.getSucChua() + " chỗ)";
        }
    }

    public DoiLichDialog(Window owner, ThoiKhoaBieu initialTkb) {
        super(owner, "Đề Xuất Đổi Lịch Giảng Dạy", ModalityType.APPLICATION_MODAL);
        this.workflowService = new WorkflowService();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.xepLichService = new XepLichService();

        initComponents(initialTkb);
        loadData(initialTkb);
    }

    private void initComponents(ThoiKhoaBieu initialTkb) {
        setSize(650, 580);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("ĐỀ XUẤT ĐỔI LỊCH GIẢNG DẠY (QUY TRÌNH 2 CẤP)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Khoa duyệt -> BGH phê duyệt & cập nhật hệ thống");
        lblSub.setFont(UIUtil.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));
        pnlHeader.add(lblSub, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // Form body
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setBackground(Color.WHITE);
        pnlBody.setBorder(new EmptyBorder(16, 24, 16, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Chọn ca học cần đổi
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        pnlBody.add(new JLabel("Ca học cần đổi: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.75;
        cbTkb = new JComboBox<>();
        pnlBody.add(cbTkb, gbc);

        // 2. Thứ mới
        gbc.gridx = 0; gbc.gridy = 1;
        pnlBody.add(new JLabel("Thứ đề xuất mới: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        cbThuMoi = new JComboBox<>(new String[]{
            "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"
        });
        pnlBody.add(cbThuMoi, gbc);

        // 3. Tiết bắt đầu & Số tiết
        gbc.gridx = 0; gbc.gridy = 2;
        pnlBody.add(new JLabel("Tiết BĐ & Số tiết: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        JPanel pnlTiet = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTiet.setOpaque(false);
        spnrTietBatDauMoi = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnrTietBatDauMoi.setPreferredSize(new Dimension(60, 26));
        spnrSoTiet = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        spnrSoTiet.setPreferredSize(new Dimension(60, 26));
        pnlTiet.add(new JLabel("Từ tiết:"));
        pnlTiet.add(spnrTietBatDauMoi);
        pnlTiet.add(new JLabel("Số tiết:"));
        pnlTiet.add(spnrSoTiet);
        pnlBody.add(pnlTiet, gbc);

        // 4. Phòng học mới
        gbc.gridx = 0; gbc.gridy = 3;
        pnlBody.add(new JLabel("Phòng học đề xuất: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        cbPhongMoi = new JComboBox<>();
        pnlBody.add(cbPhongMoi, gbc);

        // 5. Tuần áp dụng
        gbc.gridx = 0; gbc.gridy = 4;
        pnlBody.add(new JLabel("Tuần áp dụng: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        JPanel pnlTuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTuan.setOpaque(false);
        spnrTuanBatDauMoi = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        spnrTuanBatDauMoi.setPreferredSize(new Dimension(60, 26));
        spnrTuanKetThucMoi = new JSpinner(new SpinnerNumberModel(15, 1, 52, 1));
        spnrTuanKetThucMoi.setPreferredSize(new Dimension(60, 26));
        pnlTuan.add(new JLabel("Từ tuần:"));
        pnlTuan.add(spnrTuanBatDauMoi);
        pnlTuan.add(new JLabel("Đến tuần:"));
        pnlTuan.add(spnrTuanKetThucMoi);
        pnlBody.add(pnlTuan, gbc);

        // 6. Lý do đổi lịch
        gbc.gridx = 0; gbc.gridy = 5;
        pnlBody.add(new JLabel("Lý do đổi lịch: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtLyDo = new JTextArea(3, 20);
        txtLyDo.setFont(UIUtil.FONT_REGULAR);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR), new EmptyBorder(4, 6, 4, 6)));
        pnlBody.add(new JScrollPane(txtLyDo), gbc);

        // 7. Kiểm tra trùng lịch
        gbc.gridx = 0; gbc.gridy = 6;
        JButton btnCheck = UIUtil.createSecondaryButton("🔍 Kiểm Tra Xung Đột");
        btnCheck.addActionListener(e -> performConflictCheck());
        pnlBody.add(btnCheck, gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        lblConflictCheck = new JLabel("Bấm 'Kiểm tra xung đột' để kiểm tra trước khi gửi.");
        lblConflictCheck.setFont(UIUtil.FONT_SMALL);
        lblConflictCheck.setForeground(UIUtil.TEXT_MUTED);
        pnlBody.add(lblConflictCheck, gbc);

        add(pnlBody, BorderLayout.CENTER);

        // Bottom Actions
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBottom.setBackground(UIUtil.BG_LIGHT);
        pnlBottom.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1, true));

        JButton btnCancel = UIUtil.createSecondaryButton("Hủy Bỏ");
        btnCancel.addActionListener(e -> dispose());
        pnlBottom.add(btnCancel);

        JButton btnSubmit = UIUtil.createPrimaryButton("✉️ Gửi Đề Xuất Đổi Lịch");
        btnSubmit.addActionListener(e -> performSubmit());
        pnlBottom.add(btnSubmit);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadData(ThoiKhoaBieu initialTkb) {
        cbTkb.removeAllItems();
        List<ThoiKhoaBieu> allSchedules = thoiKhoaBieuDAO.getAll();
        TkbItem selectedItem = null;

        for (ThoiKhoaBieu tkb : allSchedules) {
            TkbItem item = new TkbItem(tkb);
            cbTkb.addItem(item);
            if (initialTkb != null && tkb.getId() == initialTkb.getId()) {
                selectedItem = item;
            }
        }
        if (selectedItem != null) {
            cbTkb.setSelectedItem(selectedItem);
        }

        cbPhongMoi.removeAllItems();
        for (PhongHoc p : phongHocDAO.getAll()) {
            cbPhongMoi.addItem(new PhongItem(p));
        }

        cbTkb.addActionListener(e -> syncTkbDetails());
        syncTkbDetails();
    }

    private void syncTkbDetails() {
        TkbItem item = (TkbItem) cbTkb.getSelectedItem();
        if (item != null) {
            ThoiKhoaBieu t = item.tkb;
            spnrSoTiet.setValue(t.getSoTiet() > 0 ? t.getSoTiet() : (t.getTietKetThuc() - t.getTietBatDau() + 1));
            spnrTuanBatDauMoi.setValue(t.getTuanBatDau());
            spnrTuanKetThucMoi.setValue(t.getTuanKetThuc());
        }
    }

    private boolean performConflictCheck() {
        TkbItem item = (TkbItem) cbTkb.getSelectedItem();
        if (item == null) {
            lblConflictCheck.setText("❌ Chưa chọn ca học cần đổi!");
            lblConflictCheck.setForeground(UIUtil.DANGER);
            return false;
        }

        PhongItem pItem = (PhongItem) cbPhongMoi.getSelectedItem();
        if (pItem == null) {
            lblConflictCheck.setText("❌ Chưa chọn phòng học đề xuất!");
            lblConflictCheck.setForeground(UIUtil.DANGER);
            return false;
        }

        ThoiKhoaBieu orig = item.tkb;
        int thuMoi = cbThuMoi.getSelectedIndex() + 2;
        int tietBdMoi = (int) spnrTietBatDauMoi.getValue();
        int soTiet = (int) spnrSoTiet.getValue();
        int tuanBd = (int) spnrTuanBatDauMoi.getValue();
        int tuanKt = (int) spnrTuanKetThucMoi.getValue();

        ThoiKhoaBieu target = new ThoiKhoaBieu();
        target.setMaMon(orig.getMaMon());
        target.setMaLop(orig.getMaLop());
        target.setMaGv(orig.getMaGv());
        target.setMaPhong(pItem.phong.getMaPhong());
        target.setThuTrongTuan(thuMoi);
        target.setTietBatDau(tietBdMoi);
        target.setSoTiet(soTiet);
        target.setTuanBatDau(tuanBd);
        target.setTuanKetThuc(tuanKt);
        target.setHocKy(orig.getHocKy());
        target.setNamHoc(orig.getNamHoc());

        XepLichService.ConflictResult result = xepLichService.validateAndCheckConflict(target, orig.getId());
        if (result.isValid()) {
            lblConflictCheck.setText("✅ Hợp lệ! Phòng học & Giảng viên hoàn toàn rảnh.");
            lblConflictCheck.setForeground(UIUtil.SUCCESS);
            return true;
        } else {
            lblConflictCheck.setText("❌ Xung đột: " + result.getMessage());
            lblConflictCheck.setForeground(UIUtil.DANGER);
            return false;
        }
    }

    private void performSubmit() {
        TkbItem item = (TkbItem) cbTkb.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca học cần đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PhongItem pItem = (PhongItem) cbPhongMoi.getSelectedItem();
        if (pItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng học mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lyDo = txtLyDo.getText().trim();
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do đề xuất đổi lịch!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtLyDo.requestFocus();
            return;
        }

        if (!performConflictCheck()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Phát hiện trùng lịch hoặc xung đột ở vị trí mới!\nBạn có vẫn muốn tiếp tục gửi đề xuất để Khoa xem xét không?",
                    "Cảnh báo trùng lịch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        ThoiKhoaBieu orig = item.tkb;
        int thuMoi = cbThuMoi.getSelectedIndex() + 2;
        int tietBdMoi = (int) spnrTietBatDauMoi.getValue();
        int soTiet = (int) spnrSoTiet.getValue();
        int tuanBd = (int) spnrTuanBatDauMoi.getValue();
        int tuanKt = (int) spnrTuanKetThucMoi.getValue();

        YeuCauDoiLich req = new YeuCauDoiLich();
        req.setMaTkb(orig.getId());
        req.setMaGv(orig.getMaGv());
        req.setMaPhongMoi(pItem.phong.getMaPhong());
        req.setThuMoi(thuMoi);
        req.setTietBatDauMoi(tietBdMoi);
        req.setSoTiet(soTiet);
        req.setTuanBatDauMoi(tuanBd);
        req.setTuanKetThucMoi(tuanKt);
        req.setLyDo(lyDo);
        req.setTrangThai("CHO_KHOA_DUYET");
        req.setCapPheDuyet(1);

        String res = workflowService.submitRequest(req);
        if (res.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, res, "Đề Xuất Thành Công", JOptionPane.INFORMATION_MESSAGE);
            submitted = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, res, "Không Thể Gửi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
