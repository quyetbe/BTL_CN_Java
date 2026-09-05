package view.dialog;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import util.PasswordUtil;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Thêm mới / Cập nhật thông tin Tài khoản người dùng (Admin).
 */
public class TaiKhoanDialog extends JDialog {

    private final TaiKhoanDAO taiKhoanDAO;
    private final TaiKhoan tkToEdit;
    private boolean saved = false;

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JComboBox<String> cbVaiTro;
    private JCheckBox chkTrangThai;

    public TaiKhoanDialog(Frame parent, TaiKhoan tkToEdit) {
        super(parent, tkToEdit == null ? "Thêm Tài Khoản Mới" : "Cập Nhật Tài Khoản", true);
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.tkToEdit = tkToEdit;
        initComponents();
        if (tkToEdit != null) {
            fillData(tkToEdit);
        }
    }

    private void initComponents() {
        setSize(460, 440);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel(tkToEdit == null ? "THÊM TÀI KHOẢN MỚI" : "CẬP NHẬT TÀI KHOẢN");
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

        txtTenDangNhap = new JTextField(15);
        txtMatKhau = new JPasswordField(15);
        txtHoTen = new JTextField(15);
        txtEmail = new JTextField(15);
        cbVaiTro = new JComboBox<>(new String[]{"NHANVIEN (Nhân viên đào tạo)", "ADMIN (Quản trị viên)"});
        chkTrangThai = new JCheckBox("Kích hoạt hoạt động", true);
        chkTrangThai.setOpaque(false);

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Tên đăng nhập (*):", txtTenDangNhap);
        if (tkToEdit == null) {
            addFormField(pnlForm, gbc, row++, "Mật khẩu (*):", txtMatKhau);
        }
        addFormField(pnlForm, gbc, row++, "Họ và tên (*):", txtHoTen);
        addFormField(pnlForm, gbc, row++, "Email:", txtEmail);
        addFormField(pnlForm, gbc, row++, "Vai trò / Phân quyền (*):", cbVaiTro);
        addFormField(pnlForm, gbc, row++, "Trạng thái:", chkTrangThai);

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

    private void fillData(TaiKhoan tk) {
        txtTenDangNhap.setText(tk.getTenDangNhap());
        txtTenDangNhap.setEditable(false);
        txtHoTen.setText(tk.getHoTen());
        txtEmail.setText(tk.getEmail());
        cbVaiTro.setSelectedIndex("ADMIN".equalsIgnoreCase(tk.getVaiTro()) ? 1 : 0);
        chkTrangThai.setSelected(tk.isTrangThai());
    }

    private void onSave() {
        String username = txtTenDangNhap.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String vaiTro = cbVaiTro.getSelectedIndex() == 1 ? "ADMIN" : "NHANVIEN";
        boolean trangThai = chkTrangThai.isSelected();

        if (ValidationUtil.isNullOrEmpty(username)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(hoTen)) {
            JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        if (!ValidationUtil.isNullOrEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        boolean ok;
        if (tkToEdit == null) {
            String rawPassword = new String(txtMatKhau.getPassword()).trim();
            if (rawPassword.length() < 4) {
                JOptionPane.showMessageDialog(this, "Mật khẩu tối thiểu 4 ký tự!", "Lỗi mật khẩu", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.requestFocus();
                return;
            }
            if (taiKhoanDAO.getByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập [" + username + "] đã tồn tại!", "Trùng tài khoản", JOptionPane.ERROR_MESSAGE);
                txtTenDangNhap.requestFocus();
                return;
            }
            String passwordHash = PasswordUtil.hashPassword(rawPassword);
            TaiKhoan tk = new TaiKhoan(username, passwordHash, hoTen, email, vaiTro, trangThai);
            ok = taiKhoanDAO.insert(tk);
        } else {
            tkToEdit.setHoTen(hoTen);
            tkToEdit.setEmail(email);
            tkToEdit.setVaiTro(vaiTro);
            tkToEdit.setTrangThai(trangThai);
            ok = taiKhoanDAO.update(tkToEdit);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Lưu thông tin tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
