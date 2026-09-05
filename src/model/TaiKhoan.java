package model;

import java.sql.Timestamp;

/**
 * Đại diện cho thông tin tài khoản người dùng và quyền truy cập.
 */
public class TaiKhoan {
    private int id;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String vaiTro; // ADMIN hoặc NHANVIEN
    private boolean trangThai; // true: Hoạt động, false: Đã khóa
    private Timestamp ngayTao;

    public TaiKhoan() {
    }

    public TaiKhoan(int id, String tenDangNhap, String matKhau, String hoTen, String email, String vaiTro, boolean trangThai, Timestamp ngayTao) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String hoTen, String email, String vaiTro, boolean trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.vaiTro);
    }

    @Override
    public String toString() {
        return hoTen + " (" + tenDangNhap + " - " + vaiTro + ")";
    }
}
