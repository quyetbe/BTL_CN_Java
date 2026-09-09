package view.panel;

import controller.CurriculumController;
import model.ChuongTrinhDaoTao;
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
import java.io.File;
import java.util.List;

/**
 * Phân hệ Khung Chương Trình Đào Tạo 4 Năm (8 Học Kỳ - 130 Tín Chỉ).
 * Trực quan hóa lộ trình đào tạo, môn học và phân bổ tín chỉ theo khóa K21-K24.
 */
public class CurriculumPanel extends JPanel {

    private final CurriculumController curriculumController;

    private JComboBox<String> cbKhoaHoc;
    private JComboBox<String> cbHocKy;
    private JTable tblCurriculum;
    private DefaultTableModel tableModel;

    private JLabel lblTotalCourses;
    private JLabel lblTotalCredits;
    private JLabel lblTheoryCourses;
    private JLabel lblLabCourses;

    private List<ChuongTrinhDaoTao> fullList;
    private List<ChuongTrinhDaoTao> filteredList = new java.util.ArrayList<>();
    private PaginationBar paginationBar;

    public CurriculumPanel() {
        this.curriculumController = new CurriculumController();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // 1. Top Panel
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel("KHUNG CHƯƠNG TRÌNH ĐÀO TẠO 4 NĂM (8 HỌC KỲ - 130 TÍN CHỈ)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        // 2. Summary KPI Cards
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 12, 0));
        pnlKPI.setOpaque(false);

        lblTotalCourses = new JLabel("42 môn");
        lblTotalCredits = new JLabel("130 tín chỉ");
        lblTheoryCourses = new JLabel("22 môn (52 TC)");
        lblLabCourses = new JLabel("20 môn (78 TC)");

        pnlKPI.add(createKPICard("Tổng Học Phần", lblTotalCourses, new Color(37, 99, 235)));
        pnlKPI.add(createKPICard("Tổng Số Tín Chỉ", lblTotalCredits, new Color(22, 163, 74)));
        pnlKPI.add(createKPICard("Khối Lý Thuyết", lblTheoryCourses, new Color(202, 138, 4)));
        pnlKPI.add(createKPICard("Thực Hành & Khóa Luận", lblLabCourses, new Color(147, 51, 234)));

        pnlTop.add(pnlKPI, BorderLayout.CENTER);

        // 3. Toolbar
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlToolbar.setBackground(Color.WHITE);
        pnlToolbar.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlToolbar.add(new JLabel("Khóa tuyển sinh:"));
        cbKhoaHoc = new JComboBox<>(new String[]{"Tất cả khóa", "K21", "K22", "K23", "K24"});
        cbKhoaHoc.addActionListener(e -> filterData());
        pnlToolbar.add(cbKhoaHoc);

        pnlToolbar.add(new JLabel("Học kỳ:"));
        cbHocKy = new JComboBox<>(new String[]{
            "Tất cả các kỳ", "Học kỳ 1", "Học kỳ 2", "Học kỳ 3", "Học kỳ 4",
            "Học kỳ 5", "Học kỳ 6", "Học kỳ 7", "Học kỳ 8"
        });
        cbHocKy.addActionListener(e -> filterData());
        pnlToolbar.add(cbHocKy);

        JButton btnExport = UIUtil.createSuccessButton("Xuất File CSV");
        btnExport.addActionListener(e -> exportCurriculumCSV());
        pnlToolbar.add(btnExport);

        JButton btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnRefresh.addActionListener(e -> loadData());
        pnlToolbar.add(btnRefresh);

        pnlTop.add(pnlToolbar, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // 4. Curriculum Table
        String[] headers = {
            "STT", "Năm Học", "Học Kỳ", "Mã Học Phần", "Tên Học Phần Đào Tạo",
            "Số TC", "Hình Thức Giảng Dạy", "Khối Kiến Thức", "Khóa Áp Dụng"
        };

        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCurriculum = new JTable(tableModel);
        UIUtil.formatTable(tblCurriculum);
        tblCurriculum.setRowHeight(34);
        tblCurriculum.setFont(UIUtil.FONT_REGULAR);
        tblCurriculum.setGridColor(UIUtil.BORDER_COLOR);
        tblCurriculum.setShowGrid(true);
        tblCurriculum.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCurriculum.getColumnModel().getColumn(0).setMaxWidth(55);
        tblCurriculum.getColumnModel().getColumn(1).setMaxWidth(90);
        tblCurriculum.getColumnModel().getColumn(2).setMaxWidth(90);
        tblCurriculum.getColumnModel().getColumn(5).setMaxWidth(70);

        tblCurriculum.setDefaultRenderer(Object.class, new CurriculumCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tblCurriculum);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));

        JPanel pnlCenterContainer = new JPanel(new BorderLayout());
        pnlCenterContainer.setOpaque(false);
        pnlCenterContainer.add(scrollPane, BorderLayout.CENTER);

        paginationBar = new PaginationBar(20);
        paginationBar.setPageChangeListener(page -> renderPage(page));
        pnlCenterContainer.add(paginationBar, BorderLayout.SOUTH);

        add(pnlCenterContainer, BorderLayout.CENTER);
    }

    private JPanel createKPICard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(UIUtil.FONT_SMALL);
        lblT.setForeground(UIUtil.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);

        card.add(lblT, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadData() {
        fullList = curriculumController.getAllCurriculum();
        filterData();
    }

    private void filterData() {
        if (fullList == null) return;
        filteredList.clear();

        String selKhoa = (String) cbKhoaHoc.getSelectedItem();
        int selHkIdx = cbHocKy.getSelectedIndex(); // 0 = Tất cả, 1 = HK1, 2 = HK2...

        int count = 0;
        int credits = 0;
        int theory = 0;
        int lab = 0;

        for (ChuongTrinhDaoTao c : fullList) {
            if (selKhoa != null && !selKhoa.startsWith("Tất cả") && !selKhoa.equalsIgnoreCase(c.getKhoaHoc())) {
                continue;
            }
            if (selHkIdx > 0 && c.getHocKy() != selHkIdx) {
                continue;
            }

            filteredList.add(c);
            count++;
            credits += c.getSoTinChi();
            if ("LY_THUYET".equalsIgnoreCase(c.getLoaiMon())) {
                theory++;
            } else {
                lab++;
            }
        }

        if (selKhoa != null && selKhoa.startsWith("Tất cả") && selHkIdx == 0) {
            lblTotalCourses.setText("42 môn / khóa (Tổng " + count + ")");
            lblTotalCredits.setText("130 tín chỉ / khóa (Tổng " + credits + " TC)");
            lblTheoryCourses.setText("22 môn lý thuyết / khóa");
            lblLabCourses.setText("20 môn thực hành / khóa");
        } else {
            lblTotalCourses.setText(count + " môn");
            lblTotalCredits.setText(credits + " tín chỉ");
            lblTheoryCourses.setText(theory + " môn lý thuyết");
            lblLabCourses.setText(lab + " môn thực hành");
        }

        renderPage(1);
    }

    private void renderPage(int page) {
        tableModel.setRowCount(0);
        if (paginationBar != null) {
            paginationBar.update(page, 20, filteredList.size());
        }

        List<ChuongTrinhDaoTao> pageItems = PaginationBar.getPageSlice(filteredList, page, 20);
        int startStt = (page - 1) * 20 + 1;

        for (int i = 0; i < pageItems.size(); i++) {
            ChuongTrinhDaoTao c = pageItems.get(i);
            int year = ((c.getHocKy() - 1) / 2) + 1;
            String namHoc = "Năm " + year;
            String hinhThuc = "LY_THUYET".equalsIgnoreCase(c.getLoaiMon()) ? "Lý Thuyết" : "Thực Hành / Đồ Án";
            String khoi = c.getHocKy() <= 2 ? "Kiến thức đại cương" : (c.getHocKy() <= 6 ? "Kiến thức chuyên ngành" : "Nâng cao & Khóa luận");

            tableModel.addRow(new Object[]{
                startStt + i,
                namHoc,
                "Học Kỳ " + c.getHocKy(),
                c.getMaMon(),
                c.getTenMon(),
                c.getSoTinChi() + " TC",
                hinhThuc,
                khoi,
                c.getKhoaHoc()
            });
        }
    }

    private void exportCurriculumCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Khung Chương Trình Đào Tạo");
        fileChooser.setSelectedFile(new File("Khung_CTDT_4_Nam.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            boolean ok = ExportUtil.exportTableToCSV(tblCurriculum, file, "KHUNG CHƯƠNG TRÌNH ĐÀO TẠO 4 NĂM (130 TÍN CHỈ)");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất dữ liệu CTĐT thành công!\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class CurriculumCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (column == 3) {
                setFont(UIUtil.FONT_BOLD);
                setForeground(new Color(22, 163, 74));
            } else if (column == 4 && value != null) {
                setFont(UIUtil.FONT_BOLD);
                if (value.toString().contains("Thực Hành")) {
                    setForeground(new Color(147, 51, 234));
                } else {
                    setForeground(new Color(37, 99, 235));
                }
            } else {
                setForeground(UIUtil.TEXT_DARK);
            }

            return c;
        }
    }
}
