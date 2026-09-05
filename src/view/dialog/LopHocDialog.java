package view.dialog;

import dao.LopHocDAO;
import model.LopHoc;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Thêm mới / Cập nhật thông tin Lớp học.
 */
public class LopHocDialog extends JDialog {

    private final LopHocDAO lopHocDAO;
    private final LopHoc lhToEdit;
    private boolean saved = false;

    private JTextField txtMaLop;
    private JTextField txtTenLop;
    private JSpinner spnrSiSo;
    private JTextField txtKhoaHoc;

    public LopHocDialog(Frame parent, LopHoc lhToEdit) {
        super(parent, lhToEdit == null ? "Thêm Lớp Học Mới" : "Cập Nhật Lớp Học", true);
        this.lopHocDAO = new LopHocDAO();
        this.lhToEdit = lhToEdit;
        initComponents();
        if (lhToEdit != null) {
            fillData(lhToEdit);
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
        JLabel lblTitle = new JLabel(lhToEdit == null ? "THÊM LỚP HỌC MỚI" : "CẬP NHẬT LỚP HỌC");
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

        txtMaLop = new JTextField(15);
        txtTenLop = new JTextField(15);
        spnrSiSo = new JSpinner(new SpinnerNumberModel(40, 1, 200, 1));
        txtKhoaHoc = new JTextField("2023-2027", 15);

        int row = 0;
        addFormField(pnlForm, gbc, row++, "Mã lớp học (*):", txtMaLop);
        addFormField(pnlForm, gbc, row++, "Tên lớp (*):", txtTenLop);
        addFormField(pnlForm, gbc, row++, "Sĩ số sinh viên (*):", spnrSiSo);
        addFormField(pnlForm, gbc, row++, "Khóa học / Niên khóa (*):", txtKhoaHoc);

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

    private void fillData(LopHoc lh) {
        txtMaLop.setText(lh.getMaLop());
        txtMaLop.setEditable(false);
        txtTenLop.setText(lh.getTenLop());
        spnrSiSo.setValue(lh.getSiSo());
        txtKhoaHoc.setText(lh.getKhoaHoc());
    }

    private void onSave() {
        String maLop = txtMaLop.getText().trim().toUpperCase();
        String tenLop = txtTenLop.getText().trim();
        int siSo = (int) spnrSiSo.getValue();
        String khoaHoc = txtKhoaHoc.getText().trim();

        if (ValidationUtil.isNullOrEmpty(maLop)) {
            JOptionPane.showMessageDialog(this, "Mã lớp không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaLop.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(tenLop)) {
            JOptionPane.showMessageDialog(this, "Tên lớp không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenLop.requestFocus();
            return;
        }
        if (ValidationUtil.isNullOrEmpty(khoaHoc)) {
            JOptionPane.showMessageDialog(this, "Khóa học không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtKhoaHoc.requestFocus();
            return;
        }

        LopHoc lh = new LopHoc(maLop, tenLop, siSo, khoaHoc);
        boolean ok;
        if (lhToEdit == null) {
            if (lopHocDAO.getById(maLop) != null) {
                JOptionPane.showMessageDialog(this, "Mã lớp [" + maLop + "] đã tồn tại!", "Trùng mã lớp", JOptionPane.ERROR_MESSAGE);
                txtMaLop.requestFocus();
                return;
            }
            ok = lopHocDAO.insert(lh);
        } else {
            ok = lopHocDAO.update(lh);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Lưu thông tin lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
