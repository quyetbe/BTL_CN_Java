package view.panel;

import dao.GiangVienDAO;
import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import model.GiangVien;
import model.LopHoc;
import model.PhongHoc;
import service.ThongKeService;
import util.ExportUtil;
import util.UIUtil;
import view.dialog.XepLichDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Giao diện Thống kê & Báo cáo Toàn diện Hệ thống:
 * 1. Tỷ Lệ Sử Dụng Phòng Học
 * 2. Tra Cứu Phòng Trống Theo Khung Giờ
 * 3. Thống Kê Giảng Viên (Tải giảng dạy, số lớp, số tiết dạy/tuần)
 * 4. Thống Kê Sinh Viên (Theo 4 khóa K21-K24, 40 lớp học, giới tính, tải học tập)
 */
public class ThongKePanel extends JPanel {

    private final ThongKeService thongKeService;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;

    // Tab 1: Thống kê tỷ lệ
    private JComboBox<String> cbStatHocKy;
    private JComboBox<String> cbStatNamHoc;
    private JTable tableStats;
    private DefaultTableModel modelStats;
    private JLabel lblStatSummary;

    // Tab 2: Tìm phòng trống
    private JComboBox<String> cbFindHocKy;
    private JComboBox<String> cbFindNamHoc;
    private JSpinner spnrFindTuan;
    private JComboBox<String> cbFindThu;
    private JSpinner spnrFindTietBatDau;
    private JSpinner spnrFindSoTiet;
    private JComboBox<String> cbFindLoaiPhong;
    private JSpinner spnrFindMinSucChua;
    private JTable tableVacant;
    private DefaultTableModel modelVacant;
    private List<PhongHoc> currentVacantRooms;

    // Tab 3: Thống kê Giảng viên
    private JComboBox<String> cbGvHocKy;
    private JComboBox<String> cbGvNamHoc;
    private JComboBox<String> cbGvKhoa;
    private JTextField txtGvKeyword;
    private JTable tableGiangVien;
    private DefaultTableModel modelGiangVien;
    private JLabel lblGvSummary;

    // Tab 4: Thống kê Sinh viên
    private JComboBox<String> cbSvKhoaHoc;
    private JComboBox<String> cbSvHocKy;
    private JComboBox<String> cbSvNamHoc;
    private JTable tableKhoaHoc;
    private DefaultTableModel modelKhoaHoc;
    private JTable tableSinhVienLop;
    private DefaultTableModel modelSinhVienLop;
    private JLabel lblSvSummary;

    public ThongKePanel() {
        this.thongKeService = new ThongKeService();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();

        initComponents();
        loadAllData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // Title
        JLabel lblTitle = new JLabel("THỐNG KÊ & BÁO CÁO TOÀN DIỆN HỆ THỐNG ĐÀO TẠO (CNJ56)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        add(lblTitle, BorderLayout.NORTH);

        // JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.FONT_BOLD);

        tabbedPane.addTab("Tỷ Lệ Sử Dụng Phòng Học", createStatPanel());
        tabbedPane.addTab("Tra Cứu Phòng Trống Theo Khung Giờ", createVacantRoomPanel());
        tabbedPane.addTab("Thống Kê Giảng Viên", createGiangVienStatPanel());
        tabbedPane.addTab("Thống Kê Sinh Viên", createSinhVienStatPanel());

        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) loadStatData();
            else if (idx == 2) loadGiangVienStatData();
            else if (idx == 3) loadSinhVienStatData();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void loadAllData() {
        loadStatData();
        loadGiangVienStatData();
        loadSinhVienStatData();
    }

    private JPanel createStatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlFilter.setOpaque(false);

        pnlFilter.add(new JLabel("Học kỳ:"));
        cbStatHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3"});
        pnlFilter.add(cbStatHocKy);

        pnlFilter.add(new JLabel("Năm học:"));
        cbStatNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025"});
        pnlFilter.add(cbStatNamHoc);

        JButton btnStat = UIUtil.createPrimaryButton("Thống Kê");
        btnStat.addActionListener(e -> loadStatData());
        pnlFilter.add(btnStat);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Excel Báo Cáo");
        btnExport.addActionListener(e -> exportStatCSV());
        pnlFilter.add(btnExport);

        panel.add(pnlFilter, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã Phòng", "Tên Phòng Học", "Tòa Nhà", "Sức Chứa", "Loại Phòng", "Số Tiết Đã Xếp / Tuần", "Số Tiết Khả Dụng / Tuần", "Tỷ Lệ Sử Dụng (%)", "Đánh Giá"};
        modelStats = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableStats = new JTable(modelStats);
        UIUtil.formatTable(tableStats);
        tableStats.setRowHeight(34);
        UIUtil.centerAllColumns(tableStats);
        tableStats.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                setFont(UIUtil.FONT_BOLD);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableStats);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary Bar
        lblStatSummary = new JLabel("Đang tải dữ liệu thống kê...");
        lblStatSummary.setFont(UIUtil.FONT_BOLD);
        lblStatSummary.setForeground(UIUtil.PRIMARY_DARK);
        lblStatSummary.setBorder(new EmptyBorder(6, 4, 4, 4));
        panel.add(lblStatSummary, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStatData() {
        String hocKy = (String) cbStatHocKy.getSelectedItem();
        String namHoc = (String) cbStatNamHoc.getSelectedItem();

        List<ThongKeService.PhongSuDungDTO> list = thongKeService.thongKeTyLeSuDungPhong(hocKy, namHoc);
        modelStats.setRowCount(0);

        double totalRate = 0;
        int activeRooms = 0;
        PhongHoc maxRoom = null;
        double maxRate = -1;

        for (ThongKeService.PhongSuDungDTO dto : list) {
            PhongHoc p = dto.getPhongHoc();
            String evaluation;
            if (dto.getTyLeSuDung() >= 50) evaluation = "Sử dụng cao (>= 50%)";
            else if (dto.getTyLeSuDung() >= 20) evaluation = "Vừa phải (20-50%)";
            else evaluation = "Trống nhiều (< 20%)";

            modelStats.addRow(new Object[]{
                    p.getMaPhong(),
                    p.getTenPhong(),
                    p.getToaNha(),
                    p.getSucChua() + " chỗ",
                    p.getLoaiPhongDisplay(),
                    dto.getTongSoTietDaXep() + " tiết",
                    dto.getTongSoTietKhaDung() + " tiết",
                    dto.getTyLeFormatted(),
                    evaluation
            });

            totalRate += dto.getTyLeSuDung();
            activeRooms++;
            if (dto.getTyLeSuDung() > maxRate) {
                maxRate = dto.getTyLeSuDung();
                maxRoom = p;
            }
        }

        double avgRate = activeRooms > 0 ? (totalRate / activeRooms) : 0;
        String maxInfo = maxRoom != null ? (maxRoom.getMaPhong() + " (" + String.format("%.1f%%", maxRate) + ")") : "Chưa có";
        lblStatSummary.setText(String.format("Tổng số phòng: %d | Tỷ lệ sử dụng trung bình: %.1f%% | Phòng có hiệu suất cao nhất: %s",
                activeRooms, avgRate, maxInfo));
    }

    private void exportStatCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCao_TyLeSuDungPhong_" + System.currentTimeMillis() + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            boolean ok = ExportUtil.exportTableToCSV(tableStats, f, "BÁO CÁO TỶ LỆ SỬ DỤNG PHÒNG HỌC");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JPanel createVacantRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Search Form Box
        JPanel pnlSearch = new JPanel(new GridBagLayout());
        pnlSearch.setBackground(UIUtil.BG_LIGHT);
        pnlSearch.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(10, 12, 10, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbFindHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3"});
        cbFindNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025"});
        spnrFindTuan = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        cbFindThu = new JComboBox<>(new String[]{"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"});
        spnrFindTietBatDau = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnrFindSoTiet = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        cbFindLoaiPhong = new JComboBox<>(new String[]{"TẤT CẢ", "LY_THUYET", "THUC_HANH", "HOI_TRUONG"});
        spnrFindMinSucChua = new JSpinner(new SpinnerNumberModel(0, 0, 500, 10));

        // Line 1
        gbc.gridx = 0; gbc.gridy = 0; pnlSearch.add(new JLabel("Học kỳ:"), gbc);
        gbc.gridx = 1; pnlSearch.add(cbFindHocKy, gbc);
        gbc.gridx = 2; pnlSearch.add(new JLabel("Năm học:"), gbc);
        gbc.gridx = 3; pnlSearch.add(cbFindNamHoc, gbc);
        gbc.gridx = 4; pnlSearch.add(new JLabel("Tuần:"), gbc);
        gbc.gridx = 5; pnlSearch.add(spnrFindTuan, gbc);
        gbc.gridx = 6; pnlSearch.add(new JLabel("Thứ:"), gbc);
        gbc.gridx = 7; pnlSearch.add(cbFindThu, gbc);

        // Line 2
        gbc.gridx = 0; gbc.gridy = 1; pnlSearch.add(new JLabel("Tiết bắt đầu:"), gbc);
        gbc.gridx = 1; pnlSearch.add(spnrFindTietBatDau, gbc);
        gbc.gridx = 2; pnlSearch.add(new JLabel("Số tiết:"), gbc);
        gbc.gridx = 3; pnlSearch.add(spnrFindSoTiet, gbc);
        gbc.gridx = 4; pnlSearch.add(new JLabel("Loại phòng:"), gbc);
        gbc.gridx = 5; pnlSearch.add(cbFindLoaiPhong, gbc);
        gbc.gridx = 6; pnlSearch.add(new JLabel("Sức chứa tối thiểu:"), gbc);
        gbc.gridx = 7; pnlSearch.add(spnrFindMinSucChua, gbc);

        // Button Search
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnFind = UIUtil.createPrimaryButton("Tìm Kiếm Phòng Trống");
        btnFind.addActionListener(e -> findVacantRooms());
        pnlSearch.add(btnFind, gbc);

        panel.add(pnlSearch, BorderLayout.NORTH);

        // Result Table
        String[] columns = {"Mã Phòng", "Tên Phòng Học", "Tòa Nhà", "Sức Chứa", "Loại Phòng", "Trang Thiết Bị Sẵn Có", "Trạng Thái"};
        modelVacant = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableVacant = new JTable(modelVacant);
        UIUtil.formatTable(tableVacant);
        tableVacant.setRowHeight(34);
        UIUtil.centerAllColumns(tableVacant);

        JScrollPane scrollPane = new JScrollPane(tableVacant);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom action: Quick schedule into this room
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        JButton btnQuickSchedule = UIUtil.createPrimaryButton("+ Xếp Lịch Vào Phòng Này");
        btnQuickSchedule.addActionListener(e -> onQuickSchedule());
        pnlBottom.add(btnQuickSchedule);

        panel.add(pnlBottom, BorderLayout.SOUTH);

        return panel;
    }

    private void findVacantRooms() {
        String hocKy = (String) cbFindHocKy.getSelectedItem();
        String namHoc = (String) cbFindNamHoc.getSelectedItem();
        int tuan = (int) spnrFindTuan.getValue();
        int thu = cbFindThu.getSelectedIndex() + 2;
        int start = (int) spnrFindTietBatDau.getValue();
        int count = (int) spnrFindSoTiet.getValue();
        String loaiPhong = (String) cbFindLoaiPhong.getSelectedItem();
        int minSucChua = (int) spnrFindMinSucChua.getValue();

        currentVacantRooms = thongKeService.timPhongTrong(hocKy, namHoc, thu, start, count, tuan, loaiPhong, minSucChua);
        modelVacant.setRowCount(0);

        for (PhongHoc p : currentVacantRooms) {
            modelVacant.addRow(new Object[]{
                    p.getMaPhong(),
                    p.getTenPhong(),
                    p.getToaNha(),
                    p.getSucChua() + " chỗ ngồi",
                    p.getLoaiPhongDisplay(),
                    p.getTrangThietBi(),
                    "Còn trống / Khả dụng"
            });
        }

        if (currentVacantRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng nào còn trống trong khung giờ này!", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onQuickSchedule() {
        int selectedRow = tableVacant.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng trống trong danh sách kết quả!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        XepLichDialog dialog = new XepLichDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            findVacantRooms();
        }
    }

    // ==========================================
    // TAB 3: THỐNG KÊ GIẢNG VIÊN & TẢI GIẢNG DẠY
    // ==========================================
    private JPanel createGiangVienStatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlFilter.setOpaque(false);

        pnlFilter.add(new JLabel("Học kỳ:"));
        cbGvHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3"});
        pnlFilter.add(cbGvHocKy);

        pnlFilter.add(new JLabel("Năm học:"));
        cbGvNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025"});
        pnlFilter.add(cbGvNamHoc);

        pnlFilter.add(new JLabel("Khoa / Bộ môn:"));
        cbGvKhoa = new JComboBox<>();
        cbGvKhoa.addItem("TẤT CẢ");
        List<String> depts = giangVienDAO.getAllDepartments();
        for (String d : depts) {
            cbGvKhoa.addItem(d);
        }
        pnlFilter.add(cbGvKhoa);

        pnlFilter.add(new JLabel("Tìm GV:"));
        txtGvKeyword = new JTextField(12);
        pnlFilter.add(txtGvKeyword);

        JButton btnFilter = UIUtil.createPrimaryButton("Thống Kê");
        btnFilter.addActionListener(e -> loadGiangVienStatData());
        pnlFilter.add(btnFilter);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Excel Báo Cáo");
        btnExport.addActionListener(e -> exportGiangVienCSV());
        pnlFilter.add(btnExport);

        panel.add(pnlFilter, BorderLayout.NORTH);

        // Table
        String[] columns = {"STT", "Mã Giảng Viên", "Họ và Tên", "Khoa / Bộ Môn", "Số Lớp Dạy", "Số Tiết / Tuần", "Môn Đang Dạy", "Tải Giảng Dạy"};
        modelGiangVien = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableGiangVien = new JTable(modelGiangVien);
        UIUtil.formatTable(tableGiangVien);
        tableGiangVien.setRowHeight(34);
        UIUtil.centerAllColumns(tableGiangVien);

        tableGiangVien.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableGiangVien.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableGiangVien.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableGiangVien.getColumnModel().getColumn(3).setPreferredWidth(180);
        tableGiangVien.getColumnModel().getColumn(4).setPreferredWidth(90);
        tableGiangVien.getColumnModel().getColumn(5).setPreferredWidth(110);
        tableGiangVien.getColumnModel().getColumn(6).setPreferredWidth(220);
        tableGiangVien.getColumnModel().getColumn(7).setPreferredWidth(160);

        tableGiangVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setFont(UIUtil.FONT_BOLD);
                String text = value != null ? value.toString() : "";
                if (!isSelected) {
                    if (text.contains("Vượt tải")) {
                        lbl.setForeground(new Color(220, 38, 38));
                    } else if (text.contains("Đủ định mức")) {
                        lbl.setForeground(new Color(22, 163, 74));
                    } else if (text.contains("Tải thấp")) {
                        lbl.setForeground(new Color(217, 119, 6));
                    } else {
                        lbl.setForeground(new Color(100, 116, 139));
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableGiangVien);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary Bar
        lblGvSummary = new JLabel("Đang tải dữ liệu thống kê giảng viên...");
        lblGvSummary.setFont(UIUtil.FONT_BOLD);
        lblGvSummary.setForeground(UIUtil.PRIMARY_DARK);
        lblGvSummary.setBorder(new EmptyBorder(6, 4, 4, 4));
        panel.add(lblGvSummary, BorderLayout.SOUTH);

        return panel;
    }

    private void loadGiangVienStatData() {
        String hocKy = (String) cbGvHocKy.getSelectedItem();
        String namHoc = (String) cbGvNamHoc.getSelectedItem();
        String khoa = (String) cbGvKhoa.getSelectedItem();
        String keyword = txtGvKeyword.getText().trim();

        List<ThongKeService.ThongKeGiangVienDTO> list = thongKeService.thongKeGiangVien(hocKy, namHoc, khoa, keyword);
        modelGiangVien.setRowCount(0);

        int totalTeachers = list.size();
        int activeTeachers = 0;
        int totalPeriods = 0;
        ThongKeService.ThongKeGiangVienDTO topTeacher = null;
        int maxPeriods = -1;

        int stt = 1;
        for (ThongKeService.ThongKeGiangVienDTO dto : list) {
            GiangVien gv = dto.getGiangVien();
            modelGiangVien.addRow(new Object[]{
                    stt++,
                    gv.getMaGv(),
                    gv.getHoTen(),
                    gv.getKhoaBoMon(),
                    dto.getSoLopPhuTrach() > 0 ? (dto.getSoLopPhuTrach() + " lớp") : "0",
                    dto.getSoTietDayTuan() > 0 ? (dto.getSoTietDayTuan() + " tiết") : "0",
                    dto.getDanhSachMon(),
                    dto.getDanhGiaTai()
            });

            if (dto.getSoTietDayTuan() > 0) {
                activeTeachers++;
                totalPeriods += dto.getSoTietDayTuan();
                if (dto.getSoTietDayTuan() > maxPeriods) {
                    maxPeriods = dto.getSoTietDayTuan();
                    topTeacher = dto;
                }
            }
        }

        double avgPeriods = activeTeachers > 0 ? ((double) totalPeriods / activeTeachers) : 0;
        String topInfo = topTeacher != null ? (topTeacher.getGiangVien().getHoTen() + " (" + topTeacher.getSoTietDayTuan() + " tiết/tuần)") : "Chưa có";

        lblGvSummary.setText(String.format("Tổng số GV trong bộ lọc: %d | Có lịch dạy: %d GV | Tổng giờ dạy: %d tiết/tuần | Tải trung bình: %.1f tiết/tuần | Giảng viên dạy nhiều nhất: %s",
                totalTeachers, activeTeachers, totalPeriods, avgPeriods, topInfo));
    }

    private void exportGiangVienCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCao_ThongKeGiangVien_" + System.currentTimeMillis() + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            boolean ok = ExportUtil.exportTableToCSV(tableGiangVien, f, "BÁO CÁO THỐNG KÊ TẢI GIẢNG DẠY CỦA GIẢNG VIÊN");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thống kê giảng viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 4: THỐNG KÊ SINH VIÊN & LỚP HỌC
    // ==========================================
    private JPanel createSinhVienStatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlFilter.setOpaque(false);

        pnlFilter.add(new JLabel("Khóa học:"));
        cbSvKhoaHoc = new JComboBox<>(new String[]{"TẤT CẢ KHÓA", "K21", "K22", "K23", "K24"});
        pnlFilter.add(cbSvKhoaHoc);

        pnlFilter.add(new JLabel("Học kỳ:"));
        cbSvHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3"});
        pnlFilter.add(cbSvHocKy);

        pnlFilter.add(new JLabel("Năm học:"));
        cbSvNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025"});
        pnlFilter.add(cbSvNamHoc);

        JButton btnFilter = UIUtil.createPrimaryButton("Thống Kê");
        btnFilter.addActionListener(e -> loadSinhVienStatData());
        pnlFilter.add(btnFilter);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Excel Báo Cáo Lớp");
        btnExport.addActionListener(e -> exportSinhVienCSV());
        pnlFilter.add(btnExport);

        panel.add(pnlFilter, BorderLayout.NORTH);

        // Center: Split layout with 2 tables
        JPanel pnlTables = new JPanel(new BorderLayout(0, 10));
        pnlTables.setOpaque(false);

        JPanel pnlTopTable = new JPanel(new BorderLayout(0, 4));
        pnlTopTable.setOpaque(false);
        JLabel lblTable1Title = new JLabel("1. BẢNG TỔNG HỢP SINH VIÊN THEO CÁC KHÓA ĐÀO TẠO (CHƯƠNG TRÌNH 4 NĂM)");
        lblTable1Title.setFont(UIUtil.FONT_BOLD);
        lblTable1Title.setForeground(UIUtil.PRIMARY_DARK);
        pnlTopTable.add(lblTable1Title, BorderLayout.NORTH);

        String[] colsKhoa = {"Khóa Đào Tạo", "Số Lớp Học", "Tổng Sĩ Số (SV)", "Số Sinh Viên Nam", "Số Sinh Viên Nữ", "Tỷ Lệ Giới Tính", "Trạng Thái Đào Tạo"};
        modelKhoaHoc = new DefaultTableModel(colsKhoa, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableKhoaHoc = new JTable(modelKhoaHoc);
        UIUtil.formatTable(tableKhoaHoc);
        tableKhoaHoc.setRowHeight(32);
        UIUtil.centerAllColumns(tableKhoaHoc);

        JScrollPane spKhoa = new JScrollPane(tableKhoaHoc);
        spKhoa.setPreferredSize(new Dimension(800, 145));
        spKhoa.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        pnlTopTable.add(spKhoa, BorderLayout.CENTER);

        pnlTables.add(pnlTopTable, BorderLayout.NORTH);

        // Bottom: Chi tiết 40 Lớp học & Tải học tập
        JPanel pnlBottomTable = new JPanel(new BorderLayout(0, 4));
        pnlBottomTable.setOpaque(false);
        JLabel lblTable2Title = new JLabel("2. BẢNG CHI TIẾT SĨ SỐ, GIỚI TÍNH & TẢI HỌC TẬP THEO TỪNG LỚP HỌC (40 LỚP)");
        lblTable2Title.setFont(UIUtil.FONT_BOLD);
        lblTable2Title.setForeground(UIUtil.PRIMARY_DARK);
        pnlBottomTable.add(lblTable2Title, BorderLayout.NORTH);

        String[] colsLop = {"STT", "Mã Lớp", "Tên Lớp Học", "Khóa", "Sĩ Số (SV)", "Nam", "Nữ", "Tỷ Lệ Nam / Nữ", "Đang Học", "Môn Học / Tuần", "Tiết Học / Tuần"};
        modelSinhVienLop = new DefaultTableModel(colsLop, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSinhVienLop = new JTable(modelSinhVienLop);
        UIUtil.formatTable(tableSinhVienLop);
        tableSinhVienLop.setRowHeight(34);
        UIUtil.centerAllColumns(tableSinhVienLop);

        JScrollPane spLop = new JScrollPane(tableSinhVienLop);
        spLop.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        pnlBottomTable.add(spLop, BorderLayout.CENTER);

        pnlTables.add(pnlBottomTable, BorderLayout.CENTER);
        panel.add(pnlTables, BorderLayout.CENTER);

        // Summary Bar
        lblSvSummary = new JLabel("Đang tải dữ liệu thống kê sinh viên...");
        lblSvSummary.setFont(UIUtil.FONT_BOLD);
        lblSvSummary.setForeground(UIUtil.PRIMARY_DARK);
        lblSvSummary.setBorder(new EmptyBorder(6, 4, 4, 4));
        panel.add(lblSvSummary, BorderLayout.SOUTH);

        return panel;
    }

    private void loadSinhVienStatData() {
        String hocKy = (String) cbSvHocKy.getSelectedItem();
        String namHoc = (String) cbSvNamHoc.getSelectedItem();
        String khoaHoc = (String) cbSvKhoaHoc.getSelectedItem();

        // 1. Load Bảng Tổng Hợp Khóa Học
        List<ThongKeService.ThongKeKhoaHocDTO> listKhoa = thongKeService.thongKeTheoKhoaHoc();
        modelKhoaHoc.setRowCount(0);

        int totalAllSv = 0;
        int totalAllNam = 0;
        int totalAllNu = 0;
        int totalAllLop = 0;

        for (ThongKeService.ThongKeKhoaHocDTO k : listKhoa) {
            modelKhoaHoc.addRow(new Object[]{
                    "Khóa " + k.getKhoaHoc(),
                    k.getSoLop() + " lớp",
                    k.getTongSv() + " SV",
                    k.getSoNam() + " SV",
                    k.getSoNu() + " SV",
                    k.getTyLeNamNu(),
                    "Đang đào tạo chính quy"
            });
            totalAllSv += k.getTongSv();
            totalAllNam += k.getSoNam();
            totalAllNu += k.getSoNu();
            totalAllLop += k.getSoLop();
        }

        // Dòng tổng cộng
        if (totalAllSv > 0) {
            double pNam = ((double) totalAllNam / totalAllSv) * 100.0;
            double pNu = ((double) totalAllNu / totalAllSv) * 100.0;
            modelKhoaHoc.addRow(new Object[]{
                    "TOÀN TRƯỜNG",
                    totalAllLop + " lớp",
                    totalAllSv + " SV",
                    totalAllNam + " SV",
                    totalAllNu + " SV",
                    String.format("%.1f%% Nam - %.1f%% Nữ", pNam, pNu),
                    "Đang học tập tích cực"
            });
        }

        // 2. Load Bảng Chi Tiết Theo Lớp Học
        List<ThongKeService.ThongKeSinhVienLopDTO> listLop = thongKeService.thongKeSinhVienTheoLop(hocKy, namHoc, khoaHoc);
        modelSinhVienLop.setRowCount(0);

        int filteredSv = 0;
        int filteredLop = 0;
        int stt = 1;

        for (ThongKeService.ThongKeSinhVienLopDTO dto : listLop) {
            LopHoc lh = dto.getLopHoc();
            modelSinhVienLop.addRow(new Object[]{
                    stt++,
                    lh.getMaLop(),
                    lh.getTenLop(),
                    lh.getKhoaHoc(),
                    dto.getTongSv() + " SV",
                    dto.getSoNam(),
                    dto.getSoNu(),
                    dto.getTyLeNamNu(),
                    dto.getSoDangHoc() + " SV",
                    dto.getSoMonHocTuan() > 0 ? (dto.getSoMonHocTuan() + " môn") : "0 môn",
                    dto.getSoTietHocTuan() > 0 ? (dto.getSoTietHocTuan() + " tiết") : "0 tiết"
            });
            filteredSv += dto.getTongSv();
            filteredLop++;
        }

        double avgSiSo = filteredLop > 0 ? ((double) filteredSv / filteredLop) : 0;
        lblSvSummary.setText(String.format("Tổng số sinh viên: %,d SV | Số lớp trong danh sách: %d lớp | Sĩ số trung bình: %.0f SV/lớp | Đang học: %,d SV (100%%)",
                filteredSv, filteredLop, avgSiSo, filteredSv));
    }

    private void exportSinhVienCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCao_ThongKeSinhVien_" + System.currentTimeMillis() + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            boolean ok = ExportUtil.exportTableToCSV(tableSinhVienLop, f, "BÁO CÁO THỐNG KÊ SINH VIÊN VÀ TẢI HỌC TẬP THEO LỚP");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thống kê sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
