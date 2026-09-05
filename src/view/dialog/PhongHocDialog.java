package view.dialog;

import dao.PhongHocDAO;
import model.PhongHoc;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Thêm mới / Cập nhật thông tin Phòng học.
 */
public class PhongHocDialog extends JDialog {

    private final PhongHocDAO phongHocDAO;
    private final PhongHoc phongToEdit;
    private boolean saved = false;

    private JTextField txtMaPhong;
    private JTextField txtTenPhong;
    private JTextField txtToaNha;
    private JSpinner spnrSucChua;
    private JComboBox<String> cbLoaiPhong;
    private JTextArea txtTrangThietBi;
    private JComboBox<String> cbTrangThai;

    public PhongHocDialog(Frame parent, PhongHoc phongToEdit) {
        super(parent, phongToEdit == null ? "Thêm Phòng Học Mới" : "Cập Nhật Phòng Học", true);
        this.phongHocDAO = new PhongHocDAO();
        this.phongToEdit = phongToEdit;
        initComponents();
        if (phongToEdit != null) {
            fillData(phongToEdit);
        }
    }

    private void initComponents() {
        setSize(480, 520);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel(phongToEdit == null ? "THÊM PHÒNG HỌC MỚI" : "CẬP NHẬT PHÒNG HỌC");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(16, 20, 16, 20));
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtMaPhong = new JTextField(15);
        txtTenPhong = new JTextField(15);
        txtToaNha = new JTextField(15);
        spnrSucChua = new JSpinner(new SpinnerNumberModel(40, 1, 1000, 5));

        cbLoaiPhong = new JComboBox<>(new String[]{
                "LY_THUYET (Phòng Lý thuyết)",
                "THUC_HANH (Phòng Máy tính / Thực hành)",
                "HOI_TRUONG (Hội trường)"
        });

        txtTrangThietBi = new JTextArea(3, 15);
        txtTrangThietBi.setLineWrap(true);
        txtTrangThietBi.setWrapStyleWord(true);
        JScrollPane spTrangThietBi = new JScrollPane(txtTrangThietBi);

        cbTrangThai = new JComboBox<>(new String[]{
                "DANG_SU_DUNG (Đang sử dụng)",
                "BAO_TRI (Đang bảo trì)",
                "NGUNG_SU_DUNG (Ngừng sử dụng)"
        });

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Mã phòng học (*):", txtMaPhong);
        addFormField(pnlForm, gbc, row++, "Tên phòng (*):", txtTenPhong);
        addFormField(pnlForm, gbc, row++, "Tòa nhà (*):", txtToaNha);
        addFormField(pnlForm, gbc, row++, "Sức chứa (chỗ ngồi) (*):", spnrSucChua);
        addFormField(pnlForm, gbc, row++, "Loại phòng (*):", cbLoaiPhong);
        addFormField(pnlForm, gbc, row++, "Trang thiết bị:", spTrangThietBi);
        addFormField(pnlForm, gbc, row++, "Trạng thái hoạt động (*):", cbTrangThai);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pnlButtons.setBackground(UIUtil.BG_LIGHT);
        JButton btnSave = UIUtil.createPrimaryButton("Lưu Dữ Liệu");
        JButton btnCancel = UIUtil.createSecondaryButton("Hủy Bỏ");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtil.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private void fillData(PhongHoc p) {
        txtMaPhong.setText(p.getMaPhong());
        txtMaPhong.setEditable(false);
        txtTenPhong.setText(p.getTenPhong());
        txtToaNha.setText(p.getToaNha());
        spnrSucChua.setValue(p.getSucChua());

        if ("THUC_HANH".equalsIgnoreCase(p.getLoaiPhong())) cbLoaiPhong.setSelectedIndex(1);
        else if ("HOI_TRUONG".equalsIgnoreCase(p.getLoaiPhong())) cbLoaiPhong.setSelectedIndex(2);
        else cbLoaiPhong.setSelectedIndex(0);

        txtTrangThietBi.setText(p.getTrangThietBi());

        if ("BAO_TRI".equalsIgnoreCase(p.getTrangThai())) cbTrangThai.setSelectedIndex(1);
        else if ("NGUNG_SU_DUNG".equalsIgnoreCase(p.getTrangThai())) cbTrangThai.setSelectedIndex(2);
        else cbTrangThai.setSelectedIndex(0);
    }

    private void onSave() {
        String maPhong = txtMaPhong.getText().trim().toUpperCase();
        String tenPhong = txtTenPhong.getText().trim();
        String toaNha = txtToaNha.getText().trim();
        int sucChua = (int) spnrSucChua.getValue();
        String trangThietBi = txtTrangThietBi.getText().trim();

        String loaiPhong = "LY_THUYET";
        if (cbLoaiPhong.getSelectedIndex() == 1) loaiPhong = "THUC_HANH";
        else if (cbLoaiPhong.getSelectedIndex() == 2) loaiPhong = "HOI_TRUONG";

        String trangThai = "DANG_SU_DUNG";
        if (cbTrangThai.getSelectedIndex() == 1) trangThai = "BAO_TRI";
        else if (cbTrangThai.getSelectedIndex() == 2) trangThai = "NGUNG_SU_DUNG";

        // Validate
        if (ValidationUtil.isNullOrEmpty(maPhong)) {
            JOptionPane.showMessageDialog(this, "Mã phòng không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaPhong.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(tenPhong)) {
            JOptionPane.showMessageDialog(this, "Tên phòng không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenPhong.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(toaNha)) {
            JOptionPane.showMessageDialog(this, "Tòa nhà không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtToaNha.requestFocus();
            return;
        }

        PhongHoc p = new PhongHoc(maPhong, tenPhong, toaNha, sucChua, loaiPhong, trangThietBi, trangThai);
        boolean ok;
        if (phongToEdit == null) {
            if (phongHocDAO.getById(maPhong) != null) {
                JOptionPane.showMessageDialog(this, "Mã phòng [" + maPhong + "] đã tồn tại!", "Trùng mã phòng", JOptionPane.ERROR_MESSAGE);
                txtMaPhong.requestFocus();
                return;
            }
            ok = phongHocDAO.insert(p);
        } else {
            ok = phongHocDAO.update(p);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Lưu thông tin phòng học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
