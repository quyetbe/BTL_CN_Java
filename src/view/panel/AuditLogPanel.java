package view.panel;

import dao.AuditLogDAO;
import model.AuditLog;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Xem và Tra cứu Nhật ký hệ thống (Audit Log).
 * Hỗ trợ lọc theo hành động, tìm kiếm từ khóa, phân trang 20 STT/trang và xem chi tiết.
 */
public class AuditLogPanel extends JPanel {

    private final AuditLogDAO auditLogDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbFilterAction;
    private JLabel lblTotal;
    private PaginationBar paginationBar;

    private List<AuditLog> currentList = new ArrayList<>();

    public AuditLogPanel() {
        this.auditLogDAO = new AuditLogDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIUtil.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Top Panel: Header & Filter
        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("NHẬT KÝ HOẠT ĐỘNG HỆ THỐNG (AUDIT LOG)");
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(UIUtil.TEXT_DARK);

        lblTotal = new JLabel("Tổng số: 0 nhật ký");
        lblTotal.setFont(UIUtil.FONT_BOLD);
        lblTotal.setForeground(UIUtil.PRIMARY);

        pnlTitle.add(lblTitle, BorderLayout.WEST);
        pnlTitle.add(lblTotal, BorderLayout.EAST);
        pnlTop.add(pnlTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER_COLOR, 1), new EmptyBorder(4, 8, 4, 8)));

        pnlFilter.add(new JLabel("Tìm kiếm (User/Đối tượng/IP/Nội dung):"));
        txtSearch = new JTextField(18);
        txtSearch.addActionListener(e -> searchData());
        pnlFilter.add(txtSearch);

        pnlFilter.add(new JLabel("Hành động:"));
        cbFilterAction = new JComboBox<>(new String[]{"TẤT CẢ HÀNH ĐỘNG"});
        cbFilterAction.addActionListener(e -> searchData());
        pnlFilter.add(cbFilterAction);

        JButton btnSearch = UIUtil.createPrimaryButton("Tìm Kiếm");
        btnSearch.addActionListener(e -> searchData());
        pnlFilter.add(btnSearch);

        JButton btnRefresh = UIUtil.createSecondaryButton("Làm Mới");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            if (cbFilterAction.getItemCount() > 0) cbFilterAction.setSelectedIndex(0);
            loadData();
        });
        pnlFilter.add(btnRefresh);

        JButton btnExport = UIUtil.createSuccessButton("Xuất Bảng CSV");
        btnExport.addActionListener(e -> exportToCSV());
        pnlFilter.add(btnExport);

        pnlTop.add(pnlFilter, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Center Table
        String[] columns = {
                "STT", "ID", "Thời Gian", "Tài Khoản", "Hành Động",
                "Đối Tượng", "ID Bản Ghi", "Dữ Liệu Cũ / Chi Tiết", "Dữ Liệu Mới", "Địa Chỉ IP"
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
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(220);
        table.getColumnModel().getColumn(8).setPreferredWidth(220);
        table.getColumnModel().getColumn(9).setPreferredWidth(100);

        // Căn giữa TOÀN BỘ tất cả các cột trong bảng
        UIUtil.centerAllColumns(table);

        // Custom action renderer cho cột 4 (Hành động): Căn giữa, chữ tiếng Việt, badge màu nổi bật
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(UIUtil.FONT_BOLD);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

                String act = value != null ? value.toString() : "";
                String vnAct = toVietnameseAction(act);
                setText(vnAct);

                if (vnAct.contains("Đăng nhập") || vnAct.contains("Khởi tạo") || vnAct.contains("Phê duyệt")) {
                    setForeground(new Color(22, 101, 52)); // Xanh lá
                } else if (vnAct.contains("Xóa") || vnAct.contains("Từ chối")) {
                    setForeground(new Color(185, 28, 28)); // Đỏ
                } else if (vnAct.contains("Cập nhật") || vnAct.contains("Sửa") || vnAct.contains("Đổi lịch") || vnAct.contains("Đề xuất")) {
                    setForeground(new Color(180, 83, 9));  // Cam đậm
                } else {
                    setForeground(new Color(30, 64, 175)); // Xanh dương
                }
                return c;
            }
        });

        // Double click to view full detail popup
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showLogDetail();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(UIUtil.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Actions & Pagination
        JPanel pnlSouth = new JPanel(new BorderLayout(0, 4));
        pnlSouth.setOpaque(false);

        paginationBar = new PaginationBar(20);
        paginationBar.setPageChangeListener(newPage -> renderCurrentPage());
        pnlSouth.add(paginationBar, BorderLayout.NORTH);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottom.setOpaque(false);

        JButton btnViewDetail = UIUtil.createPrimaryButton("Xem Chi Tiết Nhật Ký");
        btnViewDetail.addActionListener(e -> showLogDetail());
        pnlBottom.add(btnViewDetail);

        pnlSouth.add(pnlBottom, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    public static String toVietnameseAction(String act) {
        if (act == null || act.trim().isEmpty()) return "";
        String upper = act.toUpperCase().trim();
        switch (upper) {
            case "LOGIN": return "Đăng nhập";
            case "LOGOUT": return "Đăng xuất";
            case "SYSTEM_INIT": return "Khởi tạo hệ thống";
            case "SEED_CURRICULUM": return "Nạp khung CTĐT";
            case "SEED_ROOMS": return "Nạp phòng học";
            case "SEED_LECTURERS": return "Nạp giảng viên";
            case "SEED_STUDENTS": return "Nạp sinh viên";
            case "AUTO_SCHEDULE": return "Xếp lịch tự động";
            case "CREATE_SCHEDULE": return "Thêm lịch TKB";
            case "UPDATE_SCHEDULE": return "Cập nhật TKB";
            case "DELETE_SCHEDULE": return "Xóa lịch TKB";
            case "UPDATE_STUDENT": return "Cập nhật sinh viên";
            case "UPDATE_LECTURER": return "Cập nhật giảng viên";
            case "EXPORT_EXCEL": return "Xuất file Excel";
            case "CREATE_ROOM": return "Thêm phòng học";
            case "UPDATE_ROOM": return "Cập nhật phòng học";
            case "DELETE_ROOM": return "Xóa phòng học";
            case "RESET_PASSWORD": return "Đặt lại mật khẩu";
            case "RESOLVE_CONFLICT": return "Xử lý xung đột";
            case "UPDATE_CLASS": return "Cập nhật lớp học";
            case "UPDATE_ROLE": return "Cập nhật phân quyền";
            case "IMPORT_STUDENTS": return "Nhập sinh viên";
            case "SECURITY_CHECK": return "Kiểm tra an toàn";
            case "CHECK_ROOM_STATUS": return "Kiểm tra phòng học";
            case "EXPORT_AUDIT_LOG": return "Xuất nhật ký";
            case "SUBMIT_RESCHEDULE": return "Đề xuất đổi lịch";
            case "APPROVE_LEVEL_1": return "Duyệt cấp 1 (Khoa)";
            case "REJECT_LEVEL_1": return "Từ chối cấp 1";
            case "REJECT_LEVEL_2": return "Từ chối cấp 2";
            case "FINAL_APPROVE_AND_APPLY": return "Phê duyệt & Áp dụng TKB";
            default: return act;
        }
    }

    public void loadData() {
        cbFilterAction.removeAllItems();
        cbFilterAction.addItem("TẤT CẢ HÀNH ĐỘNG");
        java.util.Set<String> actionSet = new java.util.LinkedHashSet<>();
        for (String act : auditLogDAO.getDistinctActions()) {
            actionSet.add(toVietnameseAction(act));
        }
        for (String vnAct : actionSet) {
            cbFilterAction.addItem(vnAct);
        }

        searchData();
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String action = (String) cbFilterAction.getSelectedItem();

        currentList = auditLogDAO.search(keyword, action);
        lblTotal.setText(String.format("Tổng số: %,d nhật ký", currentList.size()));
        paginationBar.update(1, 20, currentList.size());
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        if (currentList == null || currentList.isEmpty()) return;

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        List<AuditLog> pageList = PaginationBar.getPageSlice(currentList, page, pageSize);

        int startStt = (page - 1) * pageSize + 1;
        for (int i = 0; i < pageList.size(); i++) {
            AuditLog log = pageList.get(i);
            tableModel.addRow(new Object[]{
                    startStt + i,
                    log.getId(),
                    log.getNgayTao() != null ? log.getNgayTao().toString() : "",
                    log.getTenDangNhap(),
                    toVietnameseAction(log.getHanhDong()),
                    log.getDoiTuong(),
                    log.getDoiTuongId() != null ? log.getDoiTuongId() : "-",
                    log.getDuLieuCu() != null ? log.getDuLieuCu() : "",
                    log.getDuLieuMoi() != null ? log.getDuLieuMoi() : "",
                    log.getIpAddress() != null ? log.getIpAddress() : "127.0.0.1"
            });
        }
    }

    private void showLogDetail() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng nhật ký để xem chi tiết!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int page = paginationBar.getCurrentPage();
        int pageSize = paginationBar.getPageSize();
        int indexInCurrentList = (page - 1) * pageSize + selectedRow;

        if (indexInCurrentList >= currentList.size()) return;
        AuditLog log = currentList.get(indexInCurrentList);

        StringBuilder sb = new StringBuilder();
        sb.append("=== CHI TIẾT BẢN GHI NHẬT KÝ HỆ THỐNG ===\n\n");
        sb.append("• Mã bản ghi: ").append(log.getId()).append("\n");
        sb.append("• Thời gian: ").append(log.getNgayTao()).append("\n");
        sb.append("• Tài khoản thực hiện: ").append(log.getTenDangNhap()).append(" (User ID: ").append(log.getUserId()).append(")\n");
        sb.append("• Hành động: ").append(toVietnameseAction(log.getHanhDong())).append(" [").append(log.getHanhDong()).append("]\n");
        sb.append("• Phân hệ / Đối tượng: ").append(log.getDoiTuong()).append("\n");
        sb.append("• ID Đối tượng: ").append(log.getDoiTuongId() != null ? log.getDoiTuongId() : "Không có").append("\n");
        sb.append("• Địa chỉ IP: ").append(log.getIpAddress()).append("\n\n");
        sb.append("--- DỮ LIỆU CŨ / THÔNG ĐIỆP GỐC ---\n");
        sb.append(log.getDuLieuCu() != null ? log.getDuLieuCu() : "(Trống)").append("\n\n");
        sb.append("--- DỮ LIỆU MỚI / THÔNG TIN CẬP NHẬT ---\n");
        sb.append(log.getDuLieuMoi() != null ? log.getDuLieuMoi() : "(Trống)").append("\n");

        JTextArea ta = new JTextArea(sb.toString(), 18, 50);
        ta.setFont(UIUtil.FONT_REGULAR);
        ta.setEditable(false);
        ta.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Chi Tiết Nhật Ký Hệ Thống #" + log.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToCSV() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu nhật ký để xuất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất Nhật Ký Hệ Thống Ra File CSV");
        fileChooser.setSelectedFile(new File("Nhat_Ky_He_Thong_" + System.currentTimeMillis() + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }
            boolean ok = ExportUtil.exportTableToCSV(table, selectedFile, "NHẬT KÝ HOẠT ĐỘNG HỆ THỐNG");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xuất file thành công tới:\n" + selectedFile.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
