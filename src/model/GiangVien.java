package model;

/**
 * Đại diện cho thông tin giảng viên.
 */
public class GiangVien {
    private String maGv;
    private String hoTen;
    private String khoaBoMon;
    private String email;
    private String soDienThoai;

    public GiangVien() {
    }

    public GiangVien(String maGv, String hoTen, String khoaBoMon, String email, String soDienThoai) {
        this.maGv = maGv;
        this.hoTen = hoTen;
        this.khoaBoMon = khoaBoMon;
        this.email = email;
        this.soDienThoai = soDienThoai;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getKhoaBoMon() {
        return khoaBoMon;
    }

    public void setKhoaBoMon(String khoaBoMon) {
        this.khoaBoMon = khoaBoMon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    @Override
    public String toString() {
        return maGv + " - " + hoTen;
    }
}
