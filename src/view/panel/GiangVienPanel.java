package view.panel;

import dao.GiangVienDAO;
import model.GiangVien;
import service.AuthService;
import util.UIUtil;
import view.dialog.GiangVienDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quản lý danh mục Giảng viên.
 */
public class GiangVienPanel extends JPanel {

    private final GiangVienDAO giangVienDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterKhoa;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public GiangVienPanel() {
        this.giangVienDAO = new GiangVienDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC GIẢNG VIÊN");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Khoa / Bộ môn:"));
        cbFilterKhoa = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlFilter.add(cbFilterKhoa);

        JButton btnSearch = UIUtil.createPrimaryButton("Lọc Dữ Liệu");
        btnSearch.addActionListener(e -> searchData());
        pnlFilter.add(btnSearch);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"Mã GV", "Họ và Tên", "Khoa / Bộ Môn", "Email", "Số Điện Thoại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtil.formatTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnAdd = UIUtil.createPrimaryButton("+ Thêm Giảng Viên");
        btnEdit = UIUtil.createSecondaryButton("Sửa Giảng Viên");
        btnDelete = UIUtil.createDangerButton("Xóa Giảng Viên");

        if (!AuthService.getInstance().isAdmin()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Chỉ Quản trị viên mới có quyền xóa giảng viên");
        }

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterKhoa.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnAdd);
        pnlBottom.add(btnEdit);
        pnlBottom.add(btnDelete);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    public void loadData() {
        cbFilterKhoa.removeAllItems();
        cbFilterKhoa.addItem("TẤT CẢ");
        List<String> depts = giangVienDAO.getAllDepartments();
        for (String d : depts) {
            cbFilterKhoa.addItem(d);
        }

        searchData();
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String khoa = (String) cbFilterKhoa.getSelectedItem();

        List<GiangVien> list = giangVienDAO.search(keyword, khoa);
        tableModel.setRowCount(0);

        for (GiangVien gv : list) {
            tableModel.addRow(new Object[]{
                    gv.getMaGv(),
                    gv.getHoTen(),
                    gv.getKhoaBoMon(),
                    gv.getEmail(),
                    gv.getSoDienThoai()
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        GiangVienDialog dialog = new GiangVienDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giảng viên trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maGv = (String) tableModel.getValueAt(selectedRow, 0);
        GiangVien gv = giangVienDAO.getById(maGv);
        if (gv != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            GiangVienDialog dialog = new GiangVienDialog(parent, gv);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        if (!AuthService.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa giảng viên!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giảng viên trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maGv = (String) tableModel.getValueAt(selectedRow, 0);
        String hoTen = (String) tableModel.getValueAt(selectedRow, 1);

        if (giangVienDAO.isReferencedInSchedule(maGv)) {
            JOptionPane.showMessageDialog(this,
                    "KHÔNG THỂ XÓA: Giảng viên [" + maGv + " - " + hoTen 
                    + "] đang có lịch giảng dạy trong Thời khóa biểu!\n"
                    + "Vui lòng xóa hoặc phân công giảng viên khác trước khi xóa.",
                    "Ràng buộc dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa giảng viên [" + maGv + " - " + hoTen + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = giangVienDAO.delete(maGv);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa giảng viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa giảng viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
