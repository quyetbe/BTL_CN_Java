package view.dialog;

import dao.GiangVienDAO;
import model.GiangVien;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Thêm mới / Cập nhật thông tin Giảng viên.
 */
public class GiangVienDialog extends JDialog {

    private final GiangVienDAO giangVienDAO;
    private final GiangVien gvToEdit;
    private boolean saved = false;

    private JTextField txtMaGV;
    private JTextField txtHoTen;
    private JTextField txtKhoaBoMon;
    private JTextField txtEmail;
    private JTextField txtSoDienThoai;

    public GiangVienDialog(Frame parent, GiangVien gvToEdit) {
        super(parent, gvToEdit == null ? "Thêm Giảng Viên Mới" : "Cập Nhật Giảng Viên", true);
        this.giangVienDAO = new GiangVienDAO();
        this.gvToEdit = gvToEdit;
        initComponents();
        if (gvToEdit != null) {
            fillData(gvToEdit);
        }
    }

    private void initComponents() {
        setSize(460, 420);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel(gvToEdit == null ? "THÊM GIẢNG VIÊN MỚI" : "CẬP NHẬT GIẢNG VIÊN");
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

        txtMaGV = new JTextField(15);
        txtHoTen = new JTextField(15);
        txtKhoaBoMon = new JTextField(15);
        txtEmail = new JTextField(15);
        txtSoDienThoai = new JTextField(15);

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Mã giảng viên (*):", txtMaGV);
        addFormField(pnlForm, gbc, row++, "Họ và tên (*):", txtHoTen);
        addFormField(pnlForm, gbc, row++, "Khoa / Bộ môn (*):", txtKhoaBoMon);
        addFormField(pnlForm, gbc, row++, "Email (*):", txtEmail);
        addFormField(pnlForm, gbc, row++, "Số điện thoại:", txtSoDienThoai);

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

    private void fillData(GiangVien gv) {
        txtMaGV.setText(gv.getMaGv());
        txtMaGV.setEditable(false);
        txtHoTen.setText(gv.getHoTen());
        txtKhoaBoMon.setText(gv.getKhoaBoMon());
        txtEmail.setText(gv.getEmail());
        txtSoDienThoai.setText(gv.getSoDienThoai());
    }

    private void onSave() {
        String maGV = txtMaGV.getText().trim().toUpperCase();
        String hoTen = txtHoTen.getText().trim();
        String khoaBoMon = txtKhoaBoMon.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();

        // Validate
        if (ValidationUtil.isNullOrEmpty(maGV)) {
            JOptionPane.showMessageDialog(this, "Mã giảng viên không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaGV.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(hoTen)) {
            JOptionPane.showMessageDialog(this, "Họ tên giảng viên không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(khoaBoMon)) {
            JOptionPane.showMessageDialog(this, "Khoa / Bộ môn không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtKhoaBoMon.requestFocus();
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ (Ví dụ: name@school.edu.vn)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!ValidationUtil.isNullOrEmpty(sdt) && !ValidationUtil.isValidPhone(sdt)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (Bắt buộc 10 chữ số)! Ví dụ: 0912345678", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }

        GiangVien gv = new GiangVien(maGV, hoTen, khoaBoMon, email, sdt);
        boolean ok;
        if (gvToEdit == null) {
            if (giangVienDAO.getById(maGV) != null) {
                JOptionPane.showMessageDialog(this, "Mã giảng viên [" + maGV + "] đã tồn tại!", "Trùng mã giảng viên", JOptionPane.ERROR_MESSAGE);
                txtMaGV.requestFocus();
                return;
            }
            ok = giangVienDAO.insert(gv);
        } else {
            ok = giangVienDAO.update(gv);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Lưu thông tin giảng viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Email có thể đã bị trùng với giảng viên khác.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
