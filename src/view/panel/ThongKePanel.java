package view.panel;

import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
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
 * Giao diện Thống kê Tỷ lệ Sử dụng Phòng học & Tra cứu Phòng trống theo khung giờ.
 */
public class ThongKePanel extends JPanel {

    private final ThongKeService thongKeService;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;

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

    public ThongKePanel() {
        this.thongKeService = new ThongKeService();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();

        initComponents();
        loadStatData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // Title
        JLabel lblTitle = new JLabel("THỐNG KÊ & TRA CỨU TÀI NGUYÊN PHÒNG HỌC");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        add(lblTitle, BorderLayout.NORTH);

        // JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.FONT_BOLD);

        tabbedPane.addTab("Tỷ Lệ Sử Dụng Phòng Học", createStatPanel());
        tabbedPane.addTab("Tra Cứu Phòng Trống Theo Khung Giờ", createVacantRoomPanel());

        add(tabbedPane, BorderLayout.CENTER);
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
}
