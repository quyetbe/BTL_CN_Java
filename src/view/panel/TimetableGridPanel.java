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

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Giao diện LƯỚI THỜI KHÓA BIỂU TUẦN trực quan.
 * Trục ngang: Thứ 2 -> Chủ Nhật (7 cột).
 * Trục dọc: 12 Tiết học (Tiết 1 -> 12).
 * Hỗ trợ lọc theo: Học kỳ, Năm học, Tuần, và Đối tượng (Theo Phòng / Theo Giảng viên / Theo Lớp).
 */
public class TimetableGridPanel extends JPanel {

    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final LopHocDAO lopHocDAO;

    private JComboBox<String> cbHocKy;
    private JComboBox<String> cbNamHoc;
    private JSpinner spnrTuan;
    private JComboBox<String> cbViewMode;
    private JComboBox<String> cbTargetObject;

    private JTable gridTable;
    private DefaultTableModel gridModel;

    // Ánh xạ tọa độ ô (row = tiet-1, col = thu-1) -> List<ThoiKhoaBieu>
    private final Map<String, List<ThoiKhoaBieu>> cellScheduleMap = new HashMap<>();

    public TimetableGridPanel() {
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.lopHocDAO = new LopHocDAO();

        initComponents();
        loadInitialData();
        renderGrid();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("LƯỚI THỜI KHÓA BIỂU TUẦN TRỰC QUAN");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // Control Toolbar
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlControls.setBackground(Color.WHITE);
        pnlControls.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlControls.add(new JLabel("Học kỳ:"));
        cbHocKy = new JComboBox<>(new String[]{"HK1", "HK2", "HK3"});
        pnlControls.add(cbHocKy);

        pnlControls.add(new JLabel("Năm học:"));
        cbNamHoc = new JComboBox<>(new String[]{"2025-2026", "2024-2025"});
        pnlControls.add(cbNamHoc);

        pnlControls.add(new JLabel("Tuần học:"));
        spnrTuan = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        spnrTuan.setPreferredSize(new Dimension(55, 24));
        pnlControls.add(spnrTuan);

        pnlControls.add(new JLabel("Chế độ xem:"));
        cbViewMode = new JComboBox<>(new String[]{
                "Xem theo Lớp học",
                "Xem theo Phòng học",
                "Xem theo Giảng viên",
                "Xem Toàn trường (Tất cả)"
        });
        pnlControls.add(cbViewMode);

        cbTargetObject = new JComboBox<>();
        cbTargetObject.setPreferredSize(new Dimension(220, 24));
        pnlControls.add(cbTargetObject);

        JButton btnApply = UIUtil.createPrimaryButton("Xem Lịch");
        btnApply.addActionListener(e -> renderGrid());
        pnlControls.add(btnApply);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Bảng CSV");
        btnExport.addActionListener(e -> exportGridToCSV());
        pnlControls.add(btnExport);

        cbViewMode.addActionListener(e -> updateTargetDropdown());

        pnlTop.add(pnlControls, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Grid Table
        String[] columns = {
                "Tiết / Ca", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"
        };

        gridModel = new DefaultTableModel(columns, 12) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gridTable = new JTable(gridModel);
        gridTable.setRowHeight(56);
        gridTable.setFont(UIUtil.FONT_REGULAR);
        gridTable.setGridColor(UIUtil.BORDER_COLOR);
        gridTable.setShowGrid(true);
        gridTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = gridTable.getTableHeader();
        header.setFont(UIUtil.FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(UIUtil.TEXT_DARK);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Column widths
        gridTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for (int i = 1; i <= 7; i++) {
            gridTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        // Custom Cell Renderer for rich HTML formatting in schedule cells
        gridTable.setDefaultRenderer(Object.class, new TimetableCellRenderer());

        // Mouse Listener for Cell Click -> Show full popup details
        gridTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = gridTable.getSelectedRow();
                    int col = gridTable.getSelectedColumn();
                    if (col > 0 && row >= 0) {
                        showCellDetail(row, col);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(gridTable);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Note
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        pnlBottom.setOpaque(false);
        JLabel lblNote = new JLabel("Lưu ý: Nhấp đúp chuột (Double click) vào ô có lịch để xem toàn bộ thông tin chi tiết.");
        lblNote.setFont(UIUtil.FONT_SMALL);
        lblNote.setForeground(UIUtil.TEXT_MUTED);
        pnlBottom.add(lblNote);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadInitialData() {
        cbNamHoc.removeAllItems();
        for (String y : thoiKhoaBieuDAO.getDistinctAcademicYears()) {
            cbNamHoc.addItem(y);
        }
        updateTargetDropdown();
    }

    private void updateTargetDropdown() {
        cbTargetObject.removeAllItems();
        int mode = cbViewMode.getSelectedIndex();

        if (mode == 0) { // Theo Lớp
            cbTargetObject.setEnabled(true);
            for (LopHoc l : lopHocDAO.getAll()) {
                cbTargetObject.addItem(l.getMaLop() + " - " + l.getTenLop());
            }
        } else if (mode == 1) { // Theo Phòng
            cbTargetObject.setEnabled(true);
            for (PhongHoc p : phongHocDAO.getAll()) {
                cbTargetObject.addItem(p.getMaPhong() + " - " + p.getTenPhong());
            }
        } else if (mode == 2) { // Theo Giảng viên
            cbTargetObject.setEnabled(true);
            for (GiangVien g : giangVienDAO.getAll()) {
                cbTargetObject.addItem(g.getMaGv() + " - " + g.getHoTen());
            }
        } else { // Toàn trường
            cbTargetObject.addItem("Tất cả các lớp / phòng");
            cbTargetObject.setEnabled(false);
        }
    }

    public void renderGrid() {
        String hocKy = (String) cbHocKy.getSelectedItem();
        String namHoc = (String) cbNamHoc.getSelectedItem();
        int tuan = (int) spnrTuan.getValue();

        int mode = cbViewMode.getSelectedIndex();
        String selectedTarget = (String) cbTargetObject.getSelectedItem();
        String targetCode = (selectedTarget != null && selectedTarget.contains(" - ")) ? selectedTarget.split(" - ")[0] : null;

        String maPhong = (mode == 1) ? targetCode : null;
        String maGv = (mode == 2) ? targetCode : null;
        String maLop = (mode == 0) ? targetCode : null;

        List<ThoiKhoaBieu> list = thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, tuan, maPhong, maGv, maLop, null);

        cellScheduleMap.clear();

        // Điền nhãn cột Tiết học
        for (int r = 0; r < 12; r++) {
            int tiet = r + 1;
            String session = (tiet <= 6) ? "Sáng" : "Chiều";
            gridModel.setValueAt("Tiết " + tiet + " (" + session + ")", r, 0);
            for (int c = 1; c <= 7; c++) {
                gridModel.setValueAt("", r, c);
            }
        }

        // Điền lịch vào các ô
        for (ThoiKhoaBieu tkb : list) {
            int col = tkb.getThuTrongTuan() - 1;
            if (col < 1 || col > 7) continue;

            for (int t = tkb.getTietBatDau(); t <= tkb.getTietKetThuc() && t <= 12; t++) {
                int row = t - 1;
                String key = row + "_" + col;
                cellScheduleMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tkb);

                StringBuilder sb = new StringBuilder("<html><div style='padding:2px;'>");
                List<ThoiKhoaBieu> schedulesInCell = cellScheduleMap.get(key);
                for (int i = 0; i < schedulesInCell.size(); i++) {
                    ThoiKhoaBieu s = schedulesInCell.get(i);
                    if (i > 0) sb.append("<hr style='border:0;border-top:1px dashed #ccc;margin:2px 0;'>");
                    sb.append("<b><font color='#1E40AF'>").append(s.getTenMon()).append("</font></b><br>");
                    sb.append("<font color='#047857'>Lớp: ").append(s.getMaLop()).append("</font> | ");
                    sb.append("<font color='#B45309'>Phòng: <b>").append(s.getMaPhong()).append("</b></font><br>");
                    sb.append("<font color='#4B5563'>GV: ").append(s.getHoTenGv()).append("</font>");
                }
                sb.append("</div></html>");
                gridModel.setValueAt(sb.toString(), row, col);
            }
        }
    }

    private void showCellDetail(int row, int col) {
        String key = row + "_" + col;
        List<ThoiKhoaBieu> list = cellScheduleMap.get(key);
        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có lịch học tại ô này (Tiết " + (row + 1) + ", " + gridTable.getColumnName(col) + ").", "Thông tin tiết học", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== CHI TIẾT LỊCH HỌC TẠI ").append(gridTable.getColumnName(col).toUpperCase())
          .append(" - TIẾT ").append(row + 1).append(" ===\n\n");

        for (int i = 0; i < list.size(); i++) {
            ThoiKhoaBieu tkb = list.get(i);
            sb.append("[").append(i + 1).append("] MÔN HỌC: ").append(tkb.getTenMon()).append(" (Mã: ").append(tkb.getMaMon()).append(")\n")
              .append("   • Loại môn: ").append(tkb.getLoaiMon()).append("\n")
              .append("   • Lớp học: ").append(tkb.getTenLop()).append(" (Mã: ").append(tkb.getMaLop()).append(" - Sĩ số: ").append(tkb.getSiSoLop()).append(")\n")
              .append("   • Giảng viên: ").append(tkb.getHoTenGv()).append(" (Mã: ").append(tkb.getMaGv()).append(")\n")
              .append("   • Phòng học: ").append(tkb.getTenPhong()).append(" (Mã: ").append(tkb.getMaPhong()).append(" - Sức chứa: ").append(tkb.getSucChuaPhong()).append(")\n")
              .append("   • Thời gian: ").append(tkb.getThuText()).append(", ").append(tkb.getTietText()).append("\n")
              .append("   • Tuần áp dụng: ").append(tkb.getTuanText()).append(" (").append(tkb.getHocKy()).append(" / ").append(tkb.getNamHoc()).append(")\n")
              .append("   • Ghi chú: ").append(tkb.getGhiChu() != null ? tkb.getGhiChu() : "").append("\n\n");
        }

        JTextArea ta = new JTextArea(sb.toString(), 14, 40);
        ta.setFont(UIUtil.FONT_REGULAR);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Chi Tiết Lịch Giảng Dạy", JOptionPane.PLAIN_MESSAGE);
    }

    private void exportGridToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Lưới Thời Khóa Biểu");
        fileChooser.setSelectedFile(new File("Luoi_TKB_Tuan_" + spnrTuan.getValue() + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            String title = "LƯỚI THỜI KHÓA BIỂU - " + cbHocKy.getSelectedItem() + " (" + cbNamHoc.getSelectedItem() + ") - TUẦN " + spnrTuan.getValue();
            boolean ok = ExportUtil.exportTableToCSV(gridTable, file, title);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất lưới TKB thành công tới:\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class TimetableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 0) {
                setBackground(new Color(241, 245, 249));
                setFont(UIUtil.FONT_BOLD);
                setForeground(UIUtil.TEXT_DARK);
                setHorizontalAlignment(CENTER);
            } else {
                setFont(UIUtil.FONT_SMALL);
                setHorizontalAlignment(LEFT);
                if (value != null && !value.toString().trim().isEmpty()) {
                    setBackground(new Color(239, 246, 255));
                    setForeground(UIUtil.TEXT_DARK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.GRAY);
                }

                if (isSelected) {
                    setBackground(new Color(191, 219, 254));
                }
            }
            return c;
        }
    }
}
