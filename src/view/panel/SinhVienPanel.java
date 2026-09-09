package view.panel;

import dao.LopHocDAO;
import dao.SinhVienDAO;
import model.LopHoc;
import model.SinhVien;
import util.ExportUtil;
import util.UIUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Quản lý danh mục Sinh viên trên Java Desktop Swing.
 * Quản lý và tra cứu 2.000 sinh viên theo Khóa (K21-K24) và Lớp sinh hoạt.
 */
public class SinhVienPanel extends JPanel {

    private final SinhVienDAO sinhVienDAO;
    private final LopHocDAO lopHocDAO;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterKhoa;
    private JComboBox<String> cbFilterLop;
    private JLabel lblTotal;
    private PaginationBar paginationBar;

    private List<SinhVien> allStudents = new ArrayList<>();
    private List<SinhVien> filteredStudents = new ArrayList<>();

    public SinhVienPanel() {
        this.sinhVienDAO = new SinhVienDAO();
        this.lopHocDAO = new LopHocDAO();

        initComponents();
        loadFilters();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC SINH VIÊN");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTitle.add(lblTitle, BorderLayout.WEST);

        lblTotal = new JLabel("Tổng số: 0 sinh viên");
        lblTotal.setFont(UIUtil.FONT_BOLD);
        lblTotal.setForeground(UIUtil.PRIMARY);
        pnlTitle.add(lblTotal, BorderLayout.EAST);

        pnlTop.add(pnlTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm (Mã/Tên):"));
        txtSearch = new JTextField(14);
        txtSearch.addActionListener(e -> applyFilter());
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Khóa:"));
        cbFilterKhoa = new JComboBox<>(new String[]{"TẤT CẢ", "K21", "K22", "K23", "K24"});
        cbFilterKhoa.addActionListener(e -> {
            updateLopFilterByKhoa();
            applyFilter();
        });
        pnlFilter.add(cbFilterKhoa);

        pnlFilter.add(new JLabel("Lớp học:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.setPreferredSize(new Dimension(140, 26));
        cbFilterLop.addActionListener(e -> applyFilter());
        pnlFilter.add(cbFilterLop);

        JButton btnSearch = UIUtil.createPrimaryButton("Tìm Kiếm");
        btnSearch.addActionListener(e -> applyFilter());
        pnlFilter.add(btnSearch);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Bảng CSV");
        btnExport.addActionListener(e -> exportToCSV());
        pnlFilter.add(btnExport);

        JButton btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterKhoa.setSelectedIndex(0);
            updateLopFilterByKhoa();
            cbFilterLop.setSelectedIndex(0);
            loadData();
        });
        pnlFilter.add(btnRefresh);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {
            "STT", "Mã SV", "Họ và Tên", "Giới Tính", "Ngày Sinh", "Lớp Học", "Khóa", "Email", "Số ĐT", "Trạng Thái"
        };

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
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(210);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
        table.getColumnModel().getColumn(9).setPreferredWidth(90);

        // Căn giữa TOÀN BỘ các cột trong bảng
        UIUtil.centerAllColumns(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Pagination Bar
        paginationBar = new PaginationBar(20);
        paginationBar.setPageChangeListener(newPage -> renderCurrentPage());
        add(paginationBar, BorderLayout.SOUTH);
    }

    private void loadFilters() {
        updateLopFilterByKhoa();
    }

    private void updateLopFilterByKhoa() {
        if (cbFilterLop == null) return;
        String selKhoa = (String) cbFilterKhoa.getSelectedItem();
        cbFilterLop.removeAllItems();
        cbFilterLop.addItem("TẤT CẢ LỚP");
        for (LopHoc l : lopHocDAO.getAll()) {
            if (selKhoa != null && !selKhoa.equals("TẤT CẢ") && !selKhoa.equalsIgnoreCase(l.getKhoaHoc())) {
                continue;
            }
            cbFilterLop.addItem(l.getMaLop());
        }
    }

    public void loadData() {
        allStudents = sinhVienDAO.getAll();
        applyFilter();
    }

    private void applyFilter() {
        if (allStudents == null) return;
        filteredStudents.clear();

        String search = txtSearch.getText().trim().toLowerCase();
        String selKhoa = (String) cbFilterKhoa.getSelectedItem();
        String selLop = (String) cbFilterLop.getSelectedItem();

        for (SinhVien s : allStudents) {
            if (!search.isEmpty()) {
                boolean matchMa = s.getMaSv() != null && s.getMaSv().toLowerCase().contains(search);
                boolean matchTen = s.getHoTen() != null && s.getHoTen().toLowerCase().contains(search);
                if (!matchMa && !matchTen) continue;
            }

            if (selKhoa != null && !selKhoa.equals("TẤT CẢ") && !selKhoa.equalsIgnoreCase(s.getKhoaHoc())) {
                continue;
            }

            if (selLop != null && !selLop.equals("TẤT CẢ LỚP") && !selLop.equalsIgnoreCase(s.getMaLop())) {
                continue;
            }

            filteredStudents.add(s);
        }

        lblTotal.setText(String.format("Tổng số: %,d sinh viên", filteredStudents.size()));
        paginationBar.update(1, 20, filteredStudents.size());
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        if (filteredStudents == null || filteredStudents.isEmpty()) return;

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        List<SinhVien> pageList = PaginationBar.getPageSlice(filteredStudents, page, pageSize);

        int startStt = (page - 1) * pageSize + 1;
        for (int i = 0; i < pageList.size(); i++) {
            SinhVien s = pageList.get(i);
            tableModel.addRow(new Object[]{
                startStt + i,
                s.getMaSv(),
                s.getHoTen(),
                s.getGioiTinh(),
                s.getNgaySinh() != null ? s.getNgaySinh().toString() : "",
                s.getMaLop(),
                s.getKhoaHoc(),
                s.getEmail(),
                s.getSoDienThoai(),
                s.getTrangThai()
            });
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Danh Sách Sinh Viên");
        fileChooser.setSelectedFile(new File("Danh_Sach_Sinh_Vien.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            boolean ok = ExportUtil.exportTableToCSV(table, file, "DANH SÁCH SINH VIÊN");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất file thành công:\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
