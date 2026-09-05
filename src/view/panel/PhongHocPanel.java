package view.panel;

import dao.PhongHocDAO;
import model.PhongHoc;
import service.AuthService;
import util.UIUtil;
import util.ValidationUtil;
import view.dialog.PhongHocDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quản lý Phòng học và Tài nguyên cơ sở vật chất.
 */
public class PhongHocPanel extends JPanel {

    private final PhongHocDAO phongHocDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterToaNha;
    private JComboBox<String> cbFilterLoaiPhong;
    private JComboBox<String> cbFilterTrangThai;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public PhongHocPanel() {
        this.phongHocDAO = new PhongHocDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Panel: Title + Filter Toolbar
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ PHÒNG HỌC & TÀI NGUYÊN");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(12);
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Tòa nhà:"));
        cbFilterToaNha = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlFilter.add(cbFilterToaNha);

        pnlFilter.add(new JLabel("Loại phòng:"));
        cbFilterLoaiPhong = new JComboBox<>(new String[]{"TẤT CẢ", "LY_THUYET", "THUC_HANH", "HOI_TRUONG"});
        pnlFilter.add(cbFilterLoaiPhong);

        pnlFilter.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{"TẤT CẢ", "DANG_SU_DUNG", "BAO_TRI", "NGUNG_SU_DUNG"});
        pnlFilter.add(cbFilterTrangThai);

        JButton btnSearch = UIUtil.createPrimaryButton("Lọc Dữ Liệu");
        btnSearch.addActionListener(e -> searchData());
        pnlFilter.add(btnSearch);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"Mã Phòng", "Tên Phòng Học", "Tòa Nhà", "Sức Chứa", "Loại Phòng", "Trang Thiết Bị", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtil.formatTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(260);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnAdd = UIUtil.createPrimaryButton("+ Thêm Phòng Mới");
        btnEdit = UIUtil.createSecondaryButton("Sửa Phòng");
        btnDelete = UIUtil.createDangerButton("Xóa Phòng");

        if (!AuthService.getInstance().isAdmin()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Chỉ Quản trị viên mới có quyền xóa phòng học");
        }

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterToaNha.setSelectedIndex(0);
            cbFilterLoaiPhong.setSelectedIndex(0);
            cbFilterTrangThai.setSelectedIndex(0);
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
        cbFilterToaNha.removeAllItems();
        cbFilterToaNha.addItem("TẤT CẢ");
        List<String> buildings = phongHocDAO.getAllBuildings();
        for (String b : buildings) {
            cbFilterToaNha.addItem(b);
        }

        searchData();
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String toaNha = (String) cbFilterToaNha.getSelectedItem();
        String loaiPhong = (String) cbFilterLoaiPhong.getSelectedItem();
        String trangThai = (String) cbFilterTrangThai.getSelectedItem();

        List<PhongHoc> list = phongHocDAO.search(keyword, toaNha, loaiPhong, trangThai);
        tableModel.setRowCount(0);

        for (PhongHoc p : list) {
            tableModel.addRow(new Object[]{
                    p.getMaPhong(),
                    p.getTenPhong(),
                    p.getToaNha(),
                    p.getSucChua() + " chỗ",
                    p.getLoaiPhongDisplay(),
                    p.getTrangThietBi(),
                    p.getTrangThaiDisplay()
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PhongHocDialog dialog = new PhongHocDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
        PhongHoc p = phongHocDAO.getById(maPhong);
        if (p != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            PhongHocDialog dialog = new PhongHocDialog(parent, p);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        if (!AuthService.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa phòng học!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maPhong = (String) tableModel.getValueAt(selectedRow, 0);
        String tenPhong = (String) tableModel.getValueAt(selectedRow, 1);

        if (phongHocDAO.isReferencedInSchedule(maPhong)) {
            JOptionPane.showMessageDialog(this,
                    "KHÔNG THỂ XÓA: Phòng học [" + maPhong + " - " + tenPhong 
                    + "] đang có lịch giảng dạy trong Thời khóa biểu!\n"
                    + "Vui lòng xóa hoặc chuyển các lịch học liên quan trước khi xóa phòng này.",
                    "Ràng buộc dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng học [" + maPhong + " - " + tenPhong + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = phongHocDAO.delete(maPhong);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa phòng học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa phòng học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
