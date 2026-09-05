package view.dialog;

import dao.MonHocDAO;
import model.MonHoc;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Thêm mới / Cập nhật thông tin Môn học.
 */
public class MonHocDialog extends JDialog {

    private final MonHocDAO monHocDAO;
    private final MonHoc mhToEdit;
    private boolean saved = false;

    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private JSpinner spnrSoTinChi;
    private JComboBox<String> cbLoaiMon;

    public MonHocDialog(Frame parent, MonHoc mhToEdit) {
        super(parent, mhToEdit == null ? "Thêm Môn Học Mới" : "Cập Nhật Môn Học", true);
        this.monHocDAO = new MonHocDAO();
        this.mhToEdit = mhToEdit;
        initComponents();
        if (mhToEdit != null) {
            fillData(mhToEdit);
        }
    }

    private void initComponents() {
        setSize(440, 360);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel(mhToEdit == null ? "THÊM MÔN HỌC MỚI" : "CẬP NHẬT MÔN HỌC");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(16, 20, 16, 20));
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtMaMon = new JTextField(15);
        txtTenMon = new JTextField(15);
        spnrSoTinChi = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        cbLoaiMon = new JComboBox<>(new String[]{
                "LY_THUYET (Lý thuyết)",
                "THUC_HANH (Thực hành / Máy tính)"
        });

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Mã môn học (*):", txtMaMon);
        addFormField(pnlForm, gbc, row++, "Tên môn học (*):", txtTenMon);
        addFormField(pnlForm, gbc, row++, "Số tín chỉ (*):", spnrSoTinChi);
        addFormField(pnlForm, gbc, row++, "Loại môn học (*):", cbLoaiMon);

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

    private void fillData(MonHoc mh) {
        txtMaMon.setText(mh.getMaMon());
        txtMaMon.setEditable(false);
        txtTenMon.setText(mh.getTenMon());
        spnrSoTinChi.setValue(mh.getSoTinChi());
        cbLoaiMon.setSelectedIndex("THUC_HANH".equalsIgnoreCase(mh.getLoaiMon()) ? 1 : 0);
    }

    private void onSave() {
        String maMon = txtMaMon.getText().trim().toUpperCase();
        String tenMon = txtTenMon.getText().trim();
        int soTinChi = (int) spnrSoTinChi.getValue();
        String loaiMon = cbLoaiMon.getSelectedIndex() == 1 ? "THUC_HANH" : "LY_THUYET";

        if (ValidationUtil.isNullOrEmpty(maMon)) {
            JOptionPane.showMessageDialog(this, "Mã môn học không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaMon.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(tenMon)) {
            JOptionPane.showMessageDialog(this, "Tên môn học không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenMon.requestFocus();
            return;
        }

        MonHoc mh = new MonHoc(maMon, tenMon, soTinChi, loaiMon);
        boolean ok;
        if (mhToEdit == null) {
            if (monHocDAO.getById(maMon) != null) {
                JOptionPane.showMessageDialog(this, "Mã môn học [" + maMon + "] đã tồn tại!", "Trùng mã môn", JOptionPane.ERROR_MESSAGE);
                txtMaMon.requestFocus();
                return;
            }
            ok = monHocDAO.insert(mh);
        } else {
            ok = monHocDAO.update(mh);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Lưu thông tin môn học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
