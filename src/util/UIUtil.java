package util;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Tiện ích định dạng giao diện Swing theo phong cách Flat UI hiện đại.
 * Sử dụng FlatButton tự vẽ để hoàn toàn miễn nhiễm với lỗi Windows Look & Feel.
 */
public class UIUtil {

    // Bảng màu chính
    public static final Color PRIMARY = new Color(37, 99, 235);       // Royal Blue (#2563EB)
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);  // Deep Blue
    public static final Color PRIMARY_HOVER = new Color(59, 130, 246);// Light Blue Hover
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255);// Light Blue Tint
    
    public static final Color SUCCESS = new Color(22, 163, 74);       // Emerald Green (#16A34A)
    public static final Color SUCCESS_HOVER = new Color(34, 197, 94);
    
    public static final Color WARNING = new Color(217, 119, 6);       // Amber Orange (#D97706)
    public static final Color WARNING_HOVER = new Color(245, 158, 11);
    
    public static final Color DANGER = new Color(220, 38, 38);        // Crimson Red (#DC2626)
    public static final Color DANGER_HOVER = new Color(239, 68, 68);
    
    public static final Color SECONDARY_BG = new Color(241, 245, 249);
    public static final Color SECONDARY_HOVER = new Color(226, 232, 240);
    
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);     // Slate 900 (#0F172A)
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);  // Slate 800 (#1E293B)
    public static final Color HEADER_BG = new Color(30, 41, 59);      // Slate 800
    public static final Color BG_LIGHT = new Color(248, 250, 252);    // Slate 50 (#F8FAFC)
    public static final Color TEXT_DARK = new Color(30, 41, 59);      // Slate 800
    public static final Color TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    public static final Color BORDER_COLOR = new Color(226, 232, 240);// Slate 200

    // Font chữ chuẩn tương thích 100% tiếng Việt
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Nút bấm tùy biến giao diện phẳng (Flat Button) tự vẽ màu nền,
     * đảm bảo hiển thị đúng màu 100% trên mọi Look & Feel và hệ điều hành.
     */
    public static class FlatButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color borderColor;

        public FlatButton(String text, Color bg, Color fg, Color hover, Color border) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = hover;
            this.pressedBg = hover.darker();
            this.borderColor = border;
            setForeground(fg);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(7, 14, 7, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            if (!isEnabled()) {
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(148, 163, 184));
                super.paintComponent(g);
                g2.dispose();
                return;
            }

            ButtonModel model = getModel();
            if (model.isPressed()) {
                g2.setColor(pressedBg);
            } else if (model.isRollover()) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(normalBg);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Tạo nút bấm chính (Primary Blue Button).
     */
    public static JButton createPrimaryButton(String text) {
        return new FlatButton(text, PRIMARY, Color.WHITE, PRIMARY_HOVER, PRIMARY_DARK);
    }

    /**
     * Tạo nút bấm phụ (Secondary Button).
     */
    public static JButton createSecondaryButton(String text) {
        return new FlatButton(text, SECONDARY_BG, TEXT_DARK, SECONDARY_HOVER, BORDER_COLOR);
    }

    /**
     * Tạo nút bấm cảnh báo/xóa (Danger Red Button).
     */
    public static JButton createDangerButton(String text) {
        return new FlatButton(text, DANGER, Color.WHITE, DANGER_HOVER, new Color(185, 28, 28));
    }

    /**
     * Tạo nút bấm thành công/xuất file (Success Green Button).
     */
    public static JButton createSuccessButton(String text) {
        return new FlatButton(text, SUCCESS, Color.WHITE, SUCCESS_HOVER, new Color(21, 128, 61));
    }

    /**
     * Tùy biến định dạng bảng JTable chuẩn đẹp.
     */
    public static void formatTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(28);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(PRIMARY_DARK);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Integer.class, centerRenderer);
        table.setDefaultRenderer(Double.class, centerRenderer);
    }

    /**
     * Thiết lập Look and Feel của hệ điều hành với font Segoe UI.
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.font", FONT_REGULAR);
            UIManager.put("Button.font", FONT_BOLD);
            UIManager.put("Table.font", FONT_REGULAR);
            UIManager.put("TableHeader.font", FONT_BOLD);
            UIManager.put("TextField.font", FONT_REGULAR);
            UIManager.put("ComboBox.font", FONT_REGULAR);
            UIManager.put("TabbedPane.font", FONT_BOLD);
        } catch (Exception ignored) {
        }
    }
}
