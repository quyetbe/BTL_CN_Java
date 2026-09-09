package view;

import model.TaiKhoan;
import service.AuthService;
import util.UIUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Màn hình Đăng nhập hệ thống & Xác thực người dùng.
 */
public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginForm() {
        super("Đăng Nhập - Quản Lý Thời Khóa Biểu & Phòng Học (CNJ56)");
        UIUtil.setSystemLookAndFeel();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setBackground(UIUtil.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THỜI KHÓA BIỂU", SwingConstants.CENTER);
        lblTitle.setFont(UIUtil.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("VÀ TÀI NGUYÊN PHÒNG HỌC — ĐỀ TÀI CNJ56", SwingConstants.CENTER);
        lblSub.setFont(UIUtil.FONT_BOLD);
        lblSub.setForeground(new Color(224, 231, 255));

        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // Body Form
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setBackground(Color.WHITE);
        pnlBody.setBorder(new EmptyBorder(16, 28, 16, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblUserTitle = new JLabel("Tên đăng nhập:");
        lblUserTitle.setFont(UIUtil.FONT_BOLD);
        lblUserTitle.setForeground(UIUtil.TEXT_DARK);

        txtUsername = new JTextField(15);
        txtUsername.setFont(UIUtil.FONT_REGULAR);
        txtUsername.setPreferredSize(new Dimension(txtUsername.getPreferredSize().width, 34));

        JLabel lblPassTitle = new JLabel("Mật khẩu:");
        lblPassTitle.setFont(UIUtil.FONT_BOLD);
        lblPassTitle.setForeground(UIUtil.TEXT_DARK);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UIUtil.FONT_REGULAR);
        txtPassword.setPreferredSize(new Dimension(txtPassword.getPreferredSize().width, 34));

        btnLogin = UIUtil.createPrimaryButton("ĐĂNG NHẬP HỆ THỐNG");
        btnLogin.setPreferredSize(new Dimension(btnLogin.getPreferredSize().width, 38));
        btnLogin.setFont(UIUtil.FONT_SUBTITLE);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UIUtil.FONT_SMALL);
        lblStatus.setForeground(UIUtil.DANGER);

        // Quick-selection roles (1 tài khoản duy nhất cho mỗi phân quyền)
        JPanel pnlQuickRoles = new JPanel(new GridLayout(2, 3, 5, 5));
        pnlQuickRoles.setOpaque(false);
        pnlQuickRoles.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                " Chọn nhanh tài khoản phân quyền demo: ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIUtil.FONT_SMALL,
                UIUtil.TEXT_MUTED));

        String[][] roles = {
            {"Admin", "admin", "admin123"},
            {"Ban Giám Hiệu", "bangiamhieu", "123456"},
            {"Trưởng Khoa", "truongkhoa", "123456"},
            {"Phòng Đào Tạo", "daotao", "123456"},
            {"Giảng Viên", "giangvien", "123456"},
            {"Sinh Viên", "sinhvien", "123456"}
        };
        for (String[] r : roles) {
            JButton btnRole = new JButton(r[0]);
            btnRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnRole.setBackground(new Color(241, 245, 249));
            btnRole.setForeground(UIUtil.TEXT_DARK);
            btnRole.setFocusPainted(false);
            btnRole.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnRole.addActionListener(e -> {
                txtUsername.setText(r[1]);
                txtPassword.setText(r[2]);
                lblStatus.setText("Đã chọn vai trò: " + r[0]);
                lblStatus.setForeground(UIUtil.PRIMARY);
            });
            pnlQuickRoles.add(btnRole);
        }

        int row = 0;
        gbc.gridy = row++; pnlBody.add(lblUserTitle, gbc);
        gbc.gridy = row++; pnlBody.add(txtUsername, gbc);
        gbc.gridy = row++; pnlBody.add(lblPassTitle, gbc);
        gbc.gridy = row++; pnlBody.add(txtPassword, gbc);
        gbc.gridy = row++; gbc.insets = new Insets(10, 0, 4, 0); pnlBody.add(btnLogin, gbc);
        gbc.gridy = row++; gbc.insets = new Insets(6, 0, 4, 0); pnlBody.add(pnlQuickRoles, gbc);
        gbc.gridy = row++; gbc.insets = new Insets(4, 0, 0, 0); pnlBody.add(lblStatus, gbc);

        add(pnlBody, BorderLayout.CENTER);

        // Actions
        btnLogin.addActionListener(e -> performLogin());

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (ValidationUtil.isNullOrEmpty(username)) {
            lblStatus.setText("Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return;
        }

        if (ValidationUtil.isNullOrEmpty(password)) {
            lblStatus.setText("Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return;
        }

        lblStatus.setText("Đang kiểm tra thông tin đăng nhập...");
        lblStatus.setForeground(UIUtil.PRIMARY);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan tk = AuthService.getInstance().login(username, password);
            if (tk != null) {
                lblStatus.setText("Đăng nhập thành công!");
                lblStatus.setForeground(UIUtil.SUCCESS);

                // Mở MainForm và đóng LoginForm
                dispose();
                MainForm mainForm = new MainForm();
                mainForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainForm.setVisible(true);
            } else {
                lblStatus.setForeground(UIUtil.DANGER);
                lblStatus.setText("Sai tên đăng nhập, mật khẩu hoặc tài khoản đã bị khóa!");
                JOptionPane.showMessageDialog(this,
                        "Đăng nhập thất bại!\n\n"
                      + "- Vui lòng kiểm tra lại Tên đăng nhập và Mật khẩu.\n"
                      + "- Đảm bảo XAMPP MySQL đã được khởi động (Cổng 3306).\n"
                      + "- Nếu tài khoản bị khóa, vui lòng liên hệ Quản trị viên.",
                        "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
