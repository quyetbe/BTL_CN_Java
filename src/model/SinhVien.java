package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Đại diện cho thực thể Sinh viên trong hệ thống.
 */
public class SinhVien {

    private String maSv;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String gioiTinh;
    private Date ngaySinh;
    private String maLop;
    private String khoaHoc;
    private String trangThai; // DANG_HOC, DA_TOT_NGHIEP, BAO_LUU, THOI_HOC
    private Timestamp ngayTao;

    public SinhVien() {
    }

    public SinhVien(String maSv, String hoTen, String email, String soDienThoai, String gioiTinh, Date ngaySinh, String maLop, String khoaHoc, String trangThai, Timestamp ngayTao) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.maLop = maLop;
        this.khoaHoc = khoaHoc;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(String khoaHoc) { this.khoaHoc = khoaHoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}
