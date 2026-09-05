package view.panel;

import dao.MonHocDAO;
import model.MonHoc;
import service.AuthService;
import util.UIUtil;
import view.dialog.MonHocDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quản lý danh mục Môn học.
 */
public class MonHocPanel extends JPanel {

    private final MonHocDAO monHocDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterLoaiMon;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public MonHocPanel() {
        this.monHocDAO = new MonHocDAO();
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

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC MÔN HỌC");
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

        pnlFilter.add(new JLabel("Loại môn:"));
        cbFilterLoaiMon = new JComboBox<>(new String[]{"TẤT CẢ", "LY_THUYET", "THUC_HANH"});
        pnlFilter.add(cbFilterLoaiMon);

        JButton btnSearch = UIUtil.createPrimaryButton("Lọc Dữ Liệu");
        btnSearch.addActionListener(e -> searchData());
        pnlFilter.add(btnSearch);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"Mã Môn", "Tên Môn Học", "Số Tín Chỉ", "Loại Môn Học"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtil.formatTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnAdd = UIUtil.createPrimaryButton("+ Thêm Môn Học");
        btnEdit = UIUtil.createSecondaryButton("Sửa Môn Học");
        btnDelete = UIUtil.createDangerButton("Xóa Môn Học");

        if (!AuthService.getInstance().isAdmin()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Chỉ Quản trị viên mới có quyền xóa môn học");
        }

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterLoaiMon.setSelectedIndex(0);
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
        searchData();
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String loaiMon = (String) cbFilterLoaiMon.getSelectedItem();

        List<MonHoc> list = monHocDAO.search(keyword, loaiMon);
        tableModel.setRowCount(0);

        for (MonHoc mh : list) {
            tableModel.addRow(new Object[]{
                    mh.getMaMon(),
                    mh.getTenMon(),
                    mh.getSoTinChi() + " TC",
                    mh.getLoaiMonDisplay()
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MonHocDialog dialog = new MonHocDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một môn học trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maMon = (String) tableModel.getValueAt(selectedRow, 0);
        MonHoc mh = monHocDAO.getById(maMon);
        if (mh != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            MonHocDialog dialog = new MonHocDialog(parent, mh);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        if (!AuthService.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa môn học!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một môn học trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maMon = (String) tableModel.getValueAt(selectedRow, 0);
        String tenMon = (String) tableModel.getValueAt(selectedRow, 1);

        if (monHocDAO.isReferencedInSchedule(maMon)) {
            JOptionPane.showMessageDialog(this,
                    "KHÔNG THỂ XÓA: Môn học [" + maMon + " - " + tenMon 
                    + "] đang có lịch giảng dạy trong Thời khóa biểu!\n"
                    + "Vui lòng xóa các lịch học liên quan trước khi xóa môn học này.",
                    "Ràng buộc dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa môn học [" + maMon + " - " + tenMon + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = monHocDAO.delete(maMon);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa môn học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa môn học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
