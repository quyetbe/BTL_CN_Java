package view.panel;

import dao.GiangVienDAO;
import dao.LopHocDAO;
import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import model.GiangVien;
import model.LopHoc;
import model.PhongHoc;
import model.ThoiKhoaBieu;
import service.XepLichService;
import util.ExportUtil;
import util.UIUtil;
import view.dialog.XepLichDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Giao diện Quản lý danh sách Thời khóa biểu và Thao tác Xếp lịch.
 */
public class ThoiKhoaBieuPanel extends JPanel {

    private final XepLichService xepLichService;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final LopHocDAO lopHocDAO;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<ThoiKhoaBieu> currentScheduleList;

    private JComboBox<String> cbFilterHocKy;
    private JComboBox<String> cbFilterNamHoc;
    private JComboBox<String> cbFilterKhoa;
    private JSpinner spnrFilterTuan;
    private JCheckBox chkFilterTuan;
    private JComboBox<String> cbFilterThu;
    private JComboBox<String> cbFilterPhong;
    private JComboBox<String> cbFilterGv;
    private JComboBox<String> cbFilterLop;
    private PaginationBar paginationBar;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnExport;
    private JButton btnRefresh;

    public ThoiKhoaBieuPanel() {
        this.xepLichService = new XepLichService();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.lopHocDAO = new LopHocDAO();

        initComponents();
        loadDropdownFilters();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // Top Panel: Header + Filter Toolbar
        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("DANH SÁCH THỜI KHÓA BIỂU & XẾP LỊCH GIẢNG DẠY");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Filter Box
        JPanel pnlFilter = new JPanel(new GridLayout(2, 1, 6, 6));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(8, 12, 8, 12)));

        // Row 1
        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        pnlRow1.setOpaque(false);

        pnlRow1.add(new JLabel("Học kỳ:"));
        cbFilterHocKy = new JComboBox<>(new String[]{"TẤT CẢ", "HK1", "HK2", "HK3"});
        pnlRow1.add(cbFilterHocKy);

        pnlRow1.add(new JLabel("Năm học:"));
        cbFilterNamHoc = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlRow1.add(cbFilterNamHoc);

        pnlRow1.add(new JLabel("Khóa:"));
        cbFilterKhoa = new JComboBox<>(new String[]{"TẤT CẢ KHÓA", "K21", "K22", "K23", "K24"});
        cbFilterKhoa.addActionListener(e -> updateClassDropdownByCohort());
        pnlRow1.add(cbFilterKhoa);

        chkFilterTuan = new JCheckBox("Lọc theo Tuần:");
        chkFilterTuan.setOpaque(false);
        spnrFilterTuan = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        spnrFilterTuan.setEnabled(false);
        chkFilterTuan.addActionListener(e -> spnrFilterTuan.setEnabled(chkFilterTuan.isSelected()));
        pnlRow1.add(chkFilterTuan);
        pnlRow1.add(spnrFilterTuan);

        pnlRow1.add(new JLabel("Thứ:"));
        cbFilterThu = new JComboBox<>(new String[]{"TẤT CẢ", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"});
        pnlRow1.add(cbFilterThu);

        pnlFilter.add(pnlRow1);

        // Row 2
        JPanel pnlRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        pnlRow2.setOpaque(false);

        pnlRow2.add(new JLabel("Phòng học:"));
        cbFilterPhong = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlRow2.add(cbFilterPhong);

        pnlRow2.add(new JLabel("Giảng viên:"));
        cbFilterGv = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlRow2.add(cbFilterGv);

        pnlRow2.add(new JLabel("Lớp học:"));
        cbFilterLop = new JComboBox<>(new String[]{"TẤT CẢ"});
        pnlRow2.add(cbFilterLop);

        JButton btnSearch = UIUtil.createPrimaryButton("Áp Dụng Lọc");
        btnSearch.addActionListener(e -> searchData());
        pnlRow2.add(btnSearch);

        pnlFilter.add(pnlRow2);
        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table
        String[] columns = {
                "STT", "ID", "Môn Học", "Loại", "Khóa", "Lớp Học", "Sĩ Số", "Giảng Viên",
                "Phòng Học", "Thứ", "Tiết Học", "Số Tiết", "Tuần Áp Dụng", "Kỳ / Năm", "Ghi Chú"
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
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(45);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(55);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(55);
        table.getColumnModel().getColumn(7).setPreferredWidth(140);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
        table.getColumnModel().getColumn(9).setPreferredWidth(70);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);
        table.getColumnModel().getColumn(11).setPreferredWidth(55);
        table.getColumnModel().getColumn(12).setPreferredWidth(85);
        table.getColumnModel().getColumn(13).setPreferredWidth(100);
        table.getColumnModel().getColumn(14).setPreferredWidth(120);

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
        btnExport = UIUtil.createSuccessButton("Xuất Excel / CSV");
        btnAdd = UIUtil.createPrimaryButton("+ Xếp Lịch Mới");
        btnEdit = UIUtil.createSecondaryButton("Sửa Lịch");
        btnDelete = UIUtil.createDangerButton("Xóa Lịch");

        btnRefresh.addActionListener(e -> {
            cbFilterHocKy.setSelectedIndex(0);
            cbFilterNamHoc.setSelectedIndex(0);
            cbFilterKhoa.setSelectedIndex(0);
            chkFilterTuan.setSelected(false);
            spnrFilterTuan.setEnabled(false);
            cbFilterThu.setSelectedIndex(0);
            cbFilterPhong.setSelectedIndex(0);
            cbFilterGv.setSelectedIndex(0);
            updateClassDropdownByCohort();
            cbFilterLop.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnExport.addActionListener(e -> onExport());

        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnExport);
        pnlBottom.add(btnAdd);
        pnlBottom.add(btnEdit);
        pnlBottom.add(btnDelete);
        pnlSouth.add(pnlBottom, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    public void loadDropdownFilters() {
        cbFilterNamHoc.removeAllItems();
        cbFilterNamHoc.addItem("TẤT CẢ");
        for (String y : thoiKhoaBieuDAO.getDistinctAcademicYears()) {
            cbFilterNamHoc.addItem(y);
        }

        cbFilterKhoa.removeAllItems();
        cbFilterKhoa.addItem("TẤT CẢ KHÓA");
        for (String k : thoiKhoaBieuDAO.getDistinctKhoaHoc()) {
            cbFilterKhoa.addItem(k);
        }

        cbFilterPhong.removeAllItems();
        cbFilterPhong.addItem("TẤT CẢ");
        for (PhongHoc p : phongHocDAO.getAll()) {
            cbFilterPhong.addItem(p.getMaPhong() + " - " + p.getTenPhong());
        }

        cbFilterGv.removeAllItems();
        cbFilterGv.addItem("TẤT CẢ");
        for (GiangVien g : giangVienDAO.getAll()) {
            cbFilterGv.addItem(g.getMaGv() + " - " + g.getHoTen());
        }

        updateClassDropdownByCohort();
    }

    private void updateClassDropdownByCohort() {
        if (cbFilterLop == null) return;
        String selKhoa = (String) cbFilterKhoa.getSelectedItem();
        cbFilterLop.removeAllItems();
        cbFilterLop.addItem("TẤT CẢ");
        for (LopHoc l : lopHocDAO.getAll()) {
            if (selKhoa != null && !selKhoa.equals("TẤT CẢ KHÓA") && !selKhoa.equalsIgnoreCase(l.getKhoaHoc())) {
                continue;
            }
            cbFilterLop.addItem(l.getMaLop() + " - " + l.getTenLop());
        }
    }

    public void loadData() {
        searchData();
    }

    private void searchData() {
        String hocKy = (String) cbFilterHocKy.getSelectedItem();
        String namHoc = (String) cbFilterNamHoc.getSelectedItem();
        Integer tuan = chkFilterTuan.isSelected() ? (Integer) spnrFilterTuan.getValue() : null;

        String phongStr = (String) cbFilterPhong.getSelectedItem();
        String maPhong = (phongStr != null && !phongStr.equals("TẤT CẢ")) ? phongStr.split(" - ")[0] : null;

        String gvStr = (String) cbFilterGv.getSelectedItem();
        String maGv = (gvStr != null && !gvStr.equals("TẤT CẢ")) ? gvStr.split(" - ")[0] : null;

        String lopStr = (String) cbFilterLop.getSelectedItem();
        String maLop = (lopStr != null && !lopStr.equals("TẤT CẢ")) ? lopStr.split(" - ")[0] : null;

        int thuIndex = cbFilterThu.getSelectedIndex();
        Integer thu = (thuIndex > 0) ? (thuIndex + 1) : null;

        String selKhoa = (String) cbFilterKhoa.getSelectedItem();
        String khoaHoc = (selKhoa != null && !selKhoa.equals("TẤT CẢ KHÓA")) ? selKhoa : null;

        currentScheduleList = xepLichService.getScheduleList(hocKy, namHoc, tuan, maPhong, maGv, maLop, thu, khoaHoc);
        paginationBar.update(1, 20, currentScheduleList.size());
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        if (currentScheduleList == null || currentScheduleList.isEmpty()) return;

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        List<ThoiKhoaBieu> pageList = PaginationBar.getPageSlice(currentScheduleList, page, pageSize);

        int startStt = (page - 1) * pageSize + 1;
        for (int i = 0; i < pageList.size(); i++) {
            ThoiKhoaBieu tkb = pageList.get(i);
            String khoaLop = "";
            LopHoc lh = lopHocDAO.getById(tkb.getMaLop());
            if (lh != null && lh.getKhoaHoc() != null) {
                khoaLop = lh.getKhoaHoc();
            }

            tableModel.addRow(new Object[]{
                    startStt + i,
                    tkb.getId(),
                    tkb.getMaMon() + " - " + tkb.getTenMon(),
                    "THUC_HANH".equalsIgnoreCase(tkb.getLoaiMon()) ? "Thực hành" : "Lý thuyết",
                    khoaLop,
                    tkb.getMaLop(),
                    tkb.getSiSoLop(),
                    tkb.getHoTenGv(),
                    tkb.getMaPhong(),
                    tkb.getThuText(),
                    "Tiết " + tkb.getTietBatDau() + " - " + tkb.getTietKetThuc(),
                    tkb.getSoTiet(),
                    "Tuần " + tkb.getTuanBatDau() + " - " + tkb.getTuanKetThuc(),
                    tkb.getHocKy() + " / " + tkb.getNamHoc(),
                    tkb.getGhiChu()
            });
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        XepLichDialog dialog = new XepLichDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch học trong bảng để sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        ThoiKhoaBieu tkb = thoiKhoaBieuDAO.getById(id);
        if (tkb != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            XepLichDialog dialog = new XepLichDialog(parent, tkb);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch học trong bảng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        String mon = (String) tableModel.getValueAt(selectedRow, 2);
        String lop = (String) tableModel.getValueAt(selectedRow, 5);
        String thu = (String) tableModel.getValueAt(selectedRow, 9);
        String tiet = (String) tableModel.getValueAt(selectedRow, 10);

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa lịch học sau không?\n\n"
              + "• Môn: " + mon + "\n"
              + "• Lớp: " + lop + "\n"
              + "• Thời gian: " + thu + ", " + tiet,
                "Xác nhận xóa lịch",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = xepLichService.deleteSchedule(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa lịch học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa lịch học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onExport() {
        if (currentScheduleList == null || currentScheduleList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu lịch học để xuất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Thời Khóa Biểu Ra File CSV / Excel");
        fileChooser.setSelectedFile(new File("ThoiKhoaBieu_Xuat_" + System.currentTimeMillis() + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }
            boolean ok = ExportUtil.exportTimetableToCSV(currentScheduleList, selectedFile, "DANH SÁCH THỜI KHÓA BIỂU");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất file thành công tới:\n" + selectedFile.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
