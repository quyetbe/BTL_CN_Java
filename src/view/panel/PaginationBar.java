package view.panel;

import util.UIUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Component thanh phân trang trực quan, hiện đại dùng chung cho toàn bộ các danh mục.
 * Mặc định hỗ trợ phân trang 20 STT/trang, đánh số liên tục theo trang.
 */
public class PaginationBar extends JPanel {

    public interface PageChangeListener {
        void onPageChanged(int newPage);
    }

    private final JLabel lblInfo;
    private final JButton btnFirst;
    private final JButton btnPrev;
    private final JLabel lblPage;
    private final JButton btnNext;
    private final JButton btnLast;

    private int currentPage = 1;
    private int totalPages = 1;
    private int pageSize = 20;
    private int totalItems = 0;
    private PageChangeListener listener;

    public PaginationBar() {
        this(20);
    }

    public PaginationBar(int pageSize) {
        this.pageSize = (pageSize > 0) ? pageSize : 20;

        setLayout(new BorderLayout(12, 0));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(UIUtil.BORDER_COLOR, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        // Left info label
        lblInfo = new JLabel("Hiển thị STT 0 - 0 trên tổng số 0 bản ghi");
        lblInfo.setFont(UIUtil.FONT_REGULAR);
        lblInfo.setForeground(UIUtil.TEXT_MUTED);
        add(lblInfo, BorderLayout.WEST);

        // Right navigation buttons
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlNav.setOpaque(false);

        btnFirst = UIUtil.createSecondaryButton("<< Đầu");
        btnPrev = UIUtil.createSecondaryButton("< Trước");
        lblPage = new JLabel(" Trang 1 / 1 ");
        lblPage.setFont(UIUtil.FONT_BOLD);
        lblPage.setForeground(UIUtil.TEXT_DARK);
        btnNext = UIUtil.createSecondaryButton("Tiếp >");
        btnLast = UIUtil.createSecondaryButton("Cuối >>");

        btnFirst.setPreferredSize(new Dimension(84, 28));
        btnPrev.setPreferredSize(new Dimension(80, 28));
        btnNext.setPreferredSize(new Dimension(80, 28));
        btnLast.setPreferredSize(new Dimension(84, 28));

        btnFirst.setToolTipText("Về trang đầu tiên (Trang 1)");
        btnPrev.setToolTipText("Về trang trước đó");
        btnNext.setToolTipText("Đến trang tiếp theo");
        btnLast.setToolTipText("Đến trang cuối cùng");

        btnFirst.addActionListener(e -> goToPage(1));
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        btnLast.addActionListener(e -> goToPage(totalPages));

        pnlNav.add(btnFirst);
        pnlNav.add(btnPrev);
        pnlNav.add(lblPage);
        pnlNav.add(btnNext);
        pnlNav.add(btnLast);

        add(pnlNav, BorderLayout.EAST);
    }

    public void setPageChangeListener(PageChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Cập nhật trạng thái hiển thị phân trang.
     */
    public void update(int currentPage, int pageSize, int totalItems) {
        this.pageSize = (pageSize > 0) ? pageSize : 20;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / this.pageSize);
        if (this.totalPages < 1) this.totalPages = 1;

        if (currentPage < 1) currentPage = 1;
        if (currentPage > this.totalPages) currentPage = this.totalPages;
        this.currentPage = currentPage;

        refreshUI();
    }

    private void refreshUI() {
        if (totalItems == 0) {
            lblInfo.setText("Không có bản ghi nào phù hợp");
            lblPage.setText(" Trang 1 / 1 ");
            btnFirst.setEnabled(false);
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
            btnLast.setEnabled(false);
            return;
        }

        int fromIndex = (this.currentPage - 1) * this.pageSize + 1;
        int toIndex = Math.min(this.currentPage * this.pageSize, totalItems);

        lblInfo.setText(String.format("Hiển thị STT %d - %d trên tổng số %,d bản ghi (%d mục/trang)",
                fromIndex, toIndex, totalItems, pageSize));
        lblPage.setText(String.format(" Trang %d / %d ", this.currentPage, this.totalPages));

        btnFirst.setEnabled(this.currentPage > 1);
        btnPrev.setEnabled(this.currentPage > 1);
        btnNext.setEnabled(this.currentPage < this.totalPages);
        btnLast.setEnabled(this.currentPage < this.totalPages);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void resetToFirstPage() {
        this.currentPage = 1;
        refreshUI();
    }

    private void goToPage(int page) {
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        if (page != currentPage) {
            this.currentPage = page;
            refreshUI();
            if (listener != null) {
                listener.onPageChanged(this.currentPage);
            }
        }
    }

    /**
     * Tiện ích lấy phân đoạn dữ liệu (slice) của trang hiện tại từ danh sách đầy đủ.
     */
    public static <T> List<T> getPageSlice(List<T> list, int page, int pageSize) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int from = (safePage - 1) * safePageSize;
        if (from >= list.size()) return Collections.emptyList();
        int to = Math.min(from + safePageSize, list.size());
        return list.subList(from, to);
    }
}
