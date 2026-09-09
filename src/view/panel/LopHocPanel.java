package view.panel;

import dao.LopHocDAO;
import model.LopHoc;
import service.AuthService;
import util.UIUtil;
import view.dialog.LopHocDialog;

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
 * Giao diện Quản lý danh mục Lớp học.
 */
public class LopHocPanel extends JPanel {

    private final LopHocDAO lopHocDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterKhoaHoc;
    private PaginationBar paginationBar;

    private List<LopHoc> currentList = new ArrayList<>();

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public LopHocPanel() {
        this.lopHocDAO = new LopHocDAO();
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

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC LỚP HỌC");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        txtSearch.addActionListener(e -> searchData());
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Khóa học:"));
        cbFilterKhoaHoc = new JComboBox<>(new String[]{"TẤT CẢ"});
        cbFilterKhoaHoc.addActionListener(e -> searchData());
        pnlFilter.add(cbFilterKhoaHoc);

        JButton btnSearch = UIUtil.createPrimaryButton("Lọc Dữ Liệu");
        btnSearch.addActionListener(e -> searchData());
        pnlFilter.add(btnSearch);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"STT", "Mã Lớp", "Tên Lớp Học", "Sĩ Số Sinh Viên", "Khóa Học"};
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
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(260);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

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
        btnAdd = UIUtil.createPrimaryButton("+ Thêm Lớp Học");
        btnEdit = UIUtil.createSecondaryButton("Sửa Lớp Học");
        btnDelete = UIUtil.createDangerButton("Xóa Lớp Học");

        if (!AuthService.getInstance().isAdmin()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Chỉ Quản trị viên mới có quyền xóa lớp học");
        }

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterKhoaHoc.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnAdd);
        pnlBottom.add(btnEdit);
        pnlBottom.add(btnDelete);
        pnlSouth.add(pnlBottom, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    public void loadData() {
        cbFilterKhoaHoc.removeAllItems();
        cbFilterKhoaHoc.addItem("TẤT CẢ");
        List<String> batches = lopHocDAO.getAllAcademicBatches();
        for (String b : batches) {
            cbFilterKhoaHoc.addItem(b);
        }

        searchData();
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String batch = (String) cbFilterKhoaHoc.getSelectedItem();

        currentList = lopHocDAO.search(keyword, batch);
        paginationBar.update(1, 20, currentList.size());
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        if (currentList == null || currentList.isEmpty()) return;

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        List<LopHoc> pageList = PaginationBar.getPageSlice(currentList, page, pageSize);

        int startStt = (page - 1) * pageSize + 1;
        for (int i = 0; i < pageList.size(); i++) {
            LopHoc lh = pageList.get(i);
            tableModel.addRow(new Object[]{
                    startStt + i,
                    lh.getMaLop(),
                    lh.getTenLop(),
                    lh.getSiSo() + " sinh viên",
                    lh.getKhoaHoc()
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        LopHocDialog dialog = new LopHocDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLop = (String) tableModel.getValueAt(selectedRow, 1);
        LopHoc lh = lopHocDAO.getById(maLop);
        if (lh != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            LopHocDialog dialog = new LopHocDialog(parent, lh);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        if (!AuthService.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa lớp học!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLop = (String) tableModel.getValueAt(selectedRow, 1);
        String tenLop = (String) tableModel.getValueAt(selectedRow, 2);

        if (lopHocDAO.isReferencedInSchedule(maLop)) {
            JOptionPane.showMessageDialog(this,
                    "KHÔNG THỂ XÓA: Lớp học [" + maLop + " - " + tenLop 
                    + "] đang có lịch giảng dạy trong Thời khóa biểu!\n"
                    + "Vui lòng xóa các lịch học liên quan trước khi xóa lớp này.",
                    "Ràng buộc dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa lớp học [" + maLop + " - " + tenLop + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = lopHocDAO.delete(maLop);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa lớp học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
