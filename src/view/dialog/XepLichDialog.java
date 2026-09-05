package view.dialog;

import dao.GiangVienDAO;
import dao.LopHocDAO;
import dao.MonHocDAO;
import dao.PhongHocDAO;
import model.GiangVien;
import model.LopHoc;
import model.MonHoc;
import model.PhongHoc;
import model.ThoiKhoaBieu;
import service.XepLichService;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Hộp thoại Xếp lịch / Cập nhật Thời khóa biểu.
 * Tự động kiểm tra và ngăn chặn xung đột lịch thông qua XepLichService.
 */
public class XepLichDialog extends JDialog {

    private final XepLichService xepLichService;
    private final MonHocDAO monHocDAO;
    private final LopHocDAO lopHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final PhongHocDAO phongHocDAO;

    private final ThoiKhoaBieu tkbToEdit;
    private boolean saved = false;

    private JComboBox<MonHocItem> cbMonHoc;
    private JComboBox<LopHocItem> cbLopHoc;
    private JComboBox<GiangVienItem> cbGiangVien;
    private JComboBox<PhongHocItem> cbPhongHoc;
    private JComboBox<String> cbThu;
    private JSpinner spnrTietBatDau;
    private JSpinner spnrSoTiet;
    private JLabel lblTietKetThuc;
    private JSpinner spnrTuanBatDau;
    private JSpinner spnrTuanKetThuc;
    private JComboBox<String> cbHocKy;
    private JComboBox<String> cbNamHoc;
    private JTextField txtGhiChu;

    // Helper wrapper classes for ComboBox items
    private static class MonHocItem {
        final MonHoc monHoc;
        MonHocItem(MonHoc m) { this.monHoc = m; }
        @Override public String toString() { return monHoc.getMaMon() + " - " + monHoc.getTenMon() + " (" + monHoc.getLoaiMonDisplay() + ")"; }
    }
    private static class LopHocItem {
        final LopHoc lopHoc;
        LopHocItem(LopHoc l) { this.lopHoc = l; }
        @Override public String toString() { return lopHoc.getMaLop() + " - " + lopHoc.getTenLop() + " (" + lopHoc.getSiSo() + " SV)"; }
    }
    private static class GiangVienItem {
        final GiangVien gv;
        GiangVienItem(GiangVien g) { this.gv = g; }
        @Override public String toString() { return gv.getMaGv() + " - " + gv.getHoTen(); }
    }
    private static class PhongHocItem {
        final PhongHoc phong;
        PhongHocItem(PhongHoc p) { this.phong = p; }
        @Override public String toString() { return phong.getMaPhong() + " - " + phong.getTenPhong() + " (" + phong.getSucChua() + " chỗ, " + phong.getLoaiPhongDisplay() + ")"; }
    }

    public XepLichDialog(Frame parent, ThoiKhoaBieu tkbToEdit) {
        super(parent, tkbToEdit == null ? "Xếp Lịch Học Mới" : "Cập Nhật Lịch Học", true);
        this.xepLichService = new XepLichService();
        this.monHocDAO = new MonHocDAO();
        this.lopHocDAO = new LopHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.tkbToEdit = tkbToEdit;

        initComponents();
        loadDropdownData();
        if (tkbToEdit != null) {
            fillData(tkbToEdit);
        }
    }

    private void initComponents() {
        setSize(560, 620);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel(tkbToEdit == null ? "XẾP THỜI KHÓA BIỂU MỚI" : "CẬP NHẬT THỜI KHÓA BIỂU");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(14, 18, 14, 18));
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        cbMonHoc = new JComboBox<>();
        cbLopHoc = new JComboBox<>();
        cbGiangVien = new JComboBox<>();
        cbPhongHoc = new JComboBox<>();

        cbThu = new JComboBox<>(new String[]{
                "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"
        });

        spnrTietBatDau = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnrSoTiet = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        lblTietKetThuc = new JLabel("-> Tiết kết thúc: 3");
        lblTietKetThuc.setFont(UIUtil.FONT_BOLD);
        lblTietKetThuc.setForeground(UIUtil.PRIMARY);

        spnrTietBatDau.addChangeListener(e -> updateTietKetThuc());
        spnrSoTiet.addChangeListener(e -> updateTietKetThuc());

        JPanel pnlTiet = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlTiet.setOpaque(false);
        pnlTiet.add(new JLabel("Bắt đầu:"));
        pnlTiet.add(spnrTietBatDau);
        pnlTiet.add(new JLabel("Số tiết:"));
        pnlTiet.add(spnrSoTiet);
        pnlTiet.add(lblTietKetThuc);

        spnrTuanBatDau = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        spnrTuanKetThuc = new JSpinner(new SpinnerNumberModel(15, 1, 52, 1));

        JPanel pnlTuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlTuan.setOpaque(false);
        pnlTuan.add(new JLabel("Từ tuần:"));
        pnlTuan.add(spnrTuanBatDau);
        pnlTuan.add(new JLabel("Đến tuần:"));
        pnlTuan.add(spnrTuanKetThuc);

        cbHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3 (Học kỳ hè)"});
        cbNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025", "2026-2027"});
        cbNamHoc.setEditable(true);

        txtGhiChu = new JTextField(15);

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Môn học (*):", cbMonHoc);
        addFormField(pnlForm, gbc, row++, "Lớp học (*):", cbLopHoc);
        addFormField(pnlForm, gbc, row++, "Giảng viên (*):", cbGiangVien);
        addFormField(pnlForm, gbc, row++, "Phòng học (*):", cbPhongHoc);
        addFormField(pnlForm, gbc, row++, "Thứ trong tuần (*):", cbThu);
        addFormField(pnlForm, gbc, row++, "Tiết học (*):", pnlTiet);
        addFormField(pnlForm, gbc, row++, "Tuần áp dụng (*):", pnlTuan);
        addFormField(pnlForm, gbc, row++, "Học kỳ (*):", cbHocKy);
        addFormField(pnlForm, gbc, row++, "Năm học (*):", cbNamHoc);
        addFormField(pnlForm, gbc, row++, "Ghi chú:", txtGhiChu);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pnlButtons.setBackground(UIUtil.BG_LIGHT);
        JButton btnSave = UIUtil.createPrimaryButton("Kiểm Tra & Lưu Lịch");
        JButton btnCancel = UIUtil.createSecondaryButton("Hủy Bỏ");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void updateTietKetThuc() {
        int start = (int) spnrTietBatDau.getValue();
        int count = (int) spnrSoTiet.getValue();
        int end = start + count - 1;
        lblTietKetThuc.setText("-> Tiết kết thúc: " + end + (end > 12 ? " (VƯỢT QUÁ 12!)" : ""));
        if (end > 12) {
            lblTietKetThuc.setForeground(UIUtil.DANGER);
        } else {
            lblTietKetThuc.setForeground(UIUtil.PRIMARY);
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.32;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtil.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.68;
        panel.add(comp, gbc);
    }

    private void loadDropdownData() {
        cbMonHoc.removeAllItems();
        List<MonHoc> monList = monHocDAO.getAll();
        for (MonHoc m : monList) cbMonHoc.addItem(new MonHocItem(m));

        cbLopHoc.removeAllItems();
        List<LopHoc> lopList = lopHocDAO.getAll();
        for (LopHoc l : lopList) cbLopHoc.addItem(new LopHocItem(l));

        cbGiangVien.removeAllItems();
        List<GiangVien> gvList = giangVienDAO.getAll();
        for (GiangVien g : gvList) cbGiangVien.addItem(new GiangVienItem(g));

        cbPhongHoc.removeAllItems();
        List<PhongHoc> phongList = phongHocDAO.getAvailableRooms();
        for (PhongHoc p : phongList) cbPhongHoc.addItem(new PhongHocItem(p));
    }

    private void fillData(ThoiKhoaBieu tkb) {
        // Chọn môn
        for (int i = 0; i < cbMonHoc.getItemCount(); i++) {
            if (cbMonHoc.getItemAt(i).monHoc.getMaMon().equalsIgnoreCase(tkb.getMaMon())) {
                cbMonHoc.setSelectedIndex(i);
                break;
            }
        }
        // Chọn lớp
        for (int i = 0; i < cbLopHoc.getItemCount(); i++) {
            if (cbLopHoc.getItemAt(i).lopHoc.getMaLop().equalsIgnoreCase(tkb.getMaLop())) {
                cbLopHoc.setSelectedIndex(i);
                break;
            }
        }
        // Chọn GV
        for (int i = 0; i < cbGiangVien.getItemCount(); i++) {
            if (cbGiangVien.getItemAt(i).gv.getMaGv().equalsIgnoreCase(tkb.getMaGv())) {
                cbGiangVien.setSelectedIndex(i);
                break;
            }
        }
        // Chọn Phòng
        for (int i = 0; i < cbPhongHoc.getItemCount(); i++) {
            if (cbPhongHoc.getItemAt(i).phong.getMaPhong().equalsIgnoreCase(tkb.getMaPhong())) {
                cbPhongHoc.setSelectedIndex(i);
                break;
            }
        }

        // Thứ
        int thuIndex = tkb.getThuTrongTuan() - 2;
        if (thuIndex >= 0 && thuIndex < cbThu.getItemCount()) {
            cbThu.setSelectedIndex(thuIndex);
        }

        spnrTietBatDau.setValue(tkb.getTietBatDau());
        spnrSoTiet.setValue(tkb.getSoTiet());
        spnrTuanBatDau.setValue(tkb.getTuanBatDau());
        spnrTuanKetThuc.setValue(tkb.getTuanKetThuc());
        cbHocKy.setSelectedItem(tkb.getHocKy());
        cbNamHoc.setSelectedItem(tkb.getNamHoc());
        txtGhiChu.setText(tkb.getGhiChu());
        updateTietKetThuc();
    }

    private void onSave() {
        MonHocItem selectedMon = (MonHocItem) cbMonHoc.getSelectedItem();
        LopHocItem selectedLop = (LopHocItem) cbLopHoc.getSelectedItem();
        GiangVienItem selectedGv = (GiangVienItem) cbGiangVien.getSelectedItem();
        PhongHocItem selectedPhong = (PhongHocItem) cbPhongHoc.getSelectedItem();

        if (selectedMon == null || selectedLop == null || selectedGv == null || selectedPhong == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Môn học, Lớp học, Giảng viên và Phòng học!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int thuTrongTuan = cbThu.getSelectedIndex() + 2; // 0 -> Thứ 2 (2)
        int tietBatDau = (int) spnrTietBatDau.getValue();
        int soTiet = (int) spnrSoTiet.getValue();
        int tuanBatDau = (int) spnrTuanBatDau.getValue();
        int tuanKetThuc = (int) spnrTuanKetThuc.getValue();
        String hocKy = (String) cbHocKy.getSelectedItem();
        if (hocKy != null && hocKy.contains(" ")) {
            hocKy = hocKy.split(" ")[0]; // Lấy 'HK1', 'HK2', 'HK3'
        }
        String namHoc = (String) cbNamHoc.getSelectedItem();
        String ghiChu = txtGhiChu.getText().trim();

        // Validate cơ bản
        if (!ValidationUtil.isValidPeriod(tietBatDau, soTiet)) {
            JOptionPane.showMessageDialog(this, "Tiết học không hợp lệ! Tổng tiết bắt đầu + số tiết không được vượt quá 12.", "Lỗi tiết học", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtil.isValidWeekRange(tuanBatDau, tuanKetThuc)) {
            JOptionPane.showMessageDialog(this, "Khoảng tuần học không hợp lệ (Tuần bắt đầu phải nhỏ hơn hoặc bằng Tuần kết thúc)!", "Lỗi tuần học", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ThoiKhoaBieu target = new ThoiKhoaBieu(
                tkbToEdit != null ? tkbToEdit.getId() : 0,
                selectedMon.monHoc.getMaMon(),
                selectedLop.lopHoc.getMaLop(),
                selectedGv.gv.getMaGv(),
                selectedPhong.phong.getMaPhong(),
                thuTrongTuan,
                tietBatDau,
                soTiet,
                tietBatDau + soTiet - 1,
                tuanBatDau,
                tuanKetThuc,
                namHoc != null ? hocKy : "HK1",
                namHoc != null ? namHoc.trim() : "2025-2026",
                ghiChu
        );

        // KIỂM TRA XUNG ĐỘT TOÀN DIỆN
        Integer excludeId = (tkbToEdit != null) ? tkbToEdit.getId() : null;
        XepLichService.ConflictResult result = xepLichService.validateAndCheckConflict(target, excludeId);

        if (!result.isValid()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "PHÁT HIỆN XUNG ĐỘT LỊCH", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (result.isWarningOnly()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    result.getMessage() + "\n\nBạn có muốn tiếp tục xếp lịch này không?",
                    "Cảnh báo xếp lịch",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Thực hiện lưu
        boolean ok;
        if (tkbToEdit == null) {
            ok = xepLichService.insertSchedule(target);
        } else {
            ok = xepLichService.updateSchedule(target);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Xếp lịch thời khóa biểu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu thời khóa biểu xuống CSDL. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
