package view.panel;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import service.AuthService;
import util.PasswordUtil;
import util.UIUtil;
import util.ValidationUtil;
import view.dialog.TaiKhoanDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Quản trị người dùng & Phân quyền hệ thống (Dành riêng cho ADMIN).
 */
public class TaiKhoanPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterRole;
    private PaginationBar paginationBar;

    private List<TaiKhoan> allAccounts = new ArrayList<>();
    private List<TaiKhoan> filteredAccounts = new ArrayList<>();

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnChangePassword;
    private JButton btnToggleStatus;
    private JButton btnDelete;
    private JButton btnRefresh;

    public TaiKhoanPanel() {
        this.taiKhoanDAO = new TaiKhoanDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN HỆ THỐNG");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);

        JLabel lblSub = new JLabel("Chức năng chỉ dành riêng cho Quản trị viên (ADMIN)");
        lblSub.setFont(UIUtil.FONT_SMALL);
        lblSub.setForeground(UIUtil.TEXT_MUTED);

        pnlTitle.add(lblTitle, BorderLayout.NORTH);
        pnlTitle.add(lblSub, BorderLayout.SOUTH);
        pnlTop.add(pnlTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm (Username/Tên/Email):"));
        txtSearch = new JTextField(16);
        txtSearch.addActionListener(e -> applyFilter());
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Vai trò:"));
        cbFilterRole = new JComboBox<>(new String[]{"TẤT CẢ", "ADMIN", "NHANVIEN", "GIANGVIEN", "SINHVIEN"});
        cbFilterRole.addActionListener(e -> applyFilter());
        pnlFilter.add(cbFilterRole);

        JButton btnSearch = UIUtil.createPrimaryButton("Tìm Kiếm");
        btnSearch.addActionListener(e -> applyFilter());
        pnlFilter.add(btnSearch);

        JButton btnReset = UIUtil.createSecondaryButton("Làm Mới");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterRole.setSelectedIndex(0);
            loadData();
        });
        pnlFilter.add(btnReset);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"STT", "ID", "Tên Đăng Nhập", "Họ và Tên", "Email", "Vai Trò", "Trạng Thái", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtil.formatTable(table);
        table.setRowHeight(34);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);

        // Căn giữa TOÀN BỘ các cột trong bảng
        UIUtil.centerAllColumns(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions & Pagination
        JPanel pnlSouth = new JPanel(new BorderLayout(0, 4));
        pnlSouth.setOpaque(false);

        paginationBar = new PaginationBar(20);
        paginationBar.setPageChangeListener(newPage -> renderCurrentPage());
        pnlSouth.add(paginationBar, BorderLayout.NORTH);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnAdd = UIUtil.createPrimaryButton("+ Thêm Tài Khoản");
        btnEdit = UIUtil.createSecondaryButton("Sửa Thông Tin");
        btnChangePassword = UIUtil.createSecondaryButton("Đổi Mật Khẩu");
        btnToggleStatus = UIUtil.createSecondaryButton("Khóa / Mở Khóa");
        btnDelete = UIUtil.createDangerButton("Xóa Tài Khoản");

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnChangePassword.addActionListener(e -> onChangePassword());
        btnToggleStatus.addActionListener(e -> onToggleStatus());
        btnDelete.addActionListener(e -> onDelete());

        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnAdd);
        pnlBottom.add(btnEdit);
        pnlBottom.add(btnChangePassword);
        pnlBottom.add(btnToggleStatus);
        pnlBottom.add(btnDelete);
        pnlSouth.add(pnlBottom, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    public void loadData() {
        allAccounts = taiKhoanDAO.getAll();
        applyFilter();
    }

    private void applyFilter() {
        if (allAccounts == null) return;
        filteredAccounts.clear();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String roleFilter = (String) cbFilterRole.getSelectedItem();

        for (TaiKhoan tk : allAccounts) {
            if (!keyword.isEmpty()) {
                boolean matchU = tk.getTenDangNhap() != null && tk.getTenDangNhap().toLowerCase().contains(keyword);
                boolean matchN = tk.getHoTen() != null && tk.getHoTen().toLowerCase().contains(keyword);
                boolean matchE = tk.getEmail() != null && tk.getEmail().toLowerCase().contains(keyword);
                if (!matchU && !matchN && !matchE) continue;
            }

            if (roleFilter != null && !roleFilter.equals("TẤT CẢ")) {
                if (tk.getVaiTro() == null || !tk.getVaiTro().equalsIgnoreCase(roleFilter)) {
                    continue;
                }
            }

            filteredAccounts.add(tk);
        }

        paginationBar.update(1, 20, filteredAccounts.size());
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        if (filteredAccounts == null || filteredAccounts.isEmpty()) return;

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        List<TaiKhoan> pageList = PaginationBar.getPageSlice(filteredAccounts, page, pageSize);

        int startStt = (page - 1) * pageSize + 1;
        for (int i = 0; i < pageList.size(); i++) {
            TaiKhoan tk = pageList.get(i);
            String roleText = tk.getVaiTro();
            if (tk.isAdmin()) {
                roleText = "ADMIN (Quản trị)";
            } else if ("NHANVIEN".equalsIgnoreCase(roleText)) {
                roleText = "NHANVIEN (Đào tạo)";
            } else if ("GIANGVIEN".equalsIgnoreCase(roleText)) {
                roleText = "GIẢNG VIÊN";
            } else if ("SINHVIEN".equalsIgnoreCase(roleText)) {
                roleText = "SINH VIÊN";
            }

            tableModel.addRow(new Object[]{
                    startStt + i,
                    tk.getId(),
                    tk.getTenDangNhap(),
                    tk.getHoTen(),
                    tk.getEmail() != null ? tk.getEmail() : "",
                    roleText,
                    tk.isTrangThai() ? "Đang hoạt động" : "Đã bị khóa",
                    tk.getNgayTao() != null ? tk.getNgayTao().toString() : ""
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        TaiKhoanDialog dialog = new TaiKhoanDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        TaiKhoan tk = taiKhoanDAO.getById(id);
        if (tk != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            TaiKhoanDialog dialog = new TaiKhoanDialog(parent, tk);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onChangePassword() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đổi mật khẩu!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        String username = (String) tableModel.getValueAt(selectedRow, 2);

        JPasswordField pwdField = new JPasswordField();
        int action = JOptionPane.showConfirmDialog(this, pwdField, "Nhập mật khẩu mới cho tài khoản [" + username + "]:", JOptionPane.OK_CANCEL_OPTION);

        if (action == JOptionPane.OK_OPTION) {
            String newPass = new String(pwdField.getPassword()).trim();
            if (newPass.length() < 4) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có tối thiểu 4 ký tự!", "Lỗi mật khẩu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String hashed = PasswordUtil.hashPassword(newPass);
            boolean ok = taiKhoanDAO.changePassword(id, hashed);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công cho tài khoản [" + username + "]!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onToggleStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần khóa / mở khóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        String username = (String) tableModel.getValueAt(selectedRow, 2);

        TaiKhoan current = AuthService.getInstance().getCurrentUser();
        if (current != null && current.getId() == id) {
            JOptionPane.showMessageDialog(this, "Bạn không thể tự khóa tài khoản của chính mình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan tk = taiKhoanDAO.getById(id);
        if (tk != null) {
            boolean newStatus = !tk.isTrangThai();
            boolean ok = taiKhoanDAO.setStatus(id, newStatus);
            if (ok) {
                JOptionPane.showMessageDialog(this, (newStatus ? "Mở khóa" : "Khóa") + " tài khoản [" + username + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        String username = (String) tableModel.getValueAt(selectedRow, 2);

        TaiKhoan current = AuthService.getInstance().getCurrentUser();
        if (current != null && current.getId() == id) {
            JOptionPane.showMessageDialog(this, "Bạn không thể tự xóa tài khoản của chính mình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản [" + username + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = taiKhoanDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
