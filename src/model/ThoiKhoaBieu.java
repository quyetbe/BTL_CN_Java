package model;

/**
 * Đại diện cho một lịch giảng dạy trong thời khóa biểu.
 * Chứa cả các trường khóa ngoại và dữ liệu quan hệ kết nối (JOIN)
 * phục vụ hiển thị trực quan và kiểm tra logic nghiệp vụ.
 */
public class ThoiKhoaBieu {
    private int id;
    private String maMon;
    private String maLop;
    private String maGv;
    private String maPhong;
    private int thuTrongTuan; // 2: Thứ Hai -> 8: Chủ Nhật
    private int tietBatDau;   // 1 -> 12
    private int soTiet;       // 1 -> 6
    private int tietKetThuc;  // tietBatDau + soTiet - 1
    private int tuanBatDau;   // VD: 1
    private int tuanKetThuc;  // VD: 15
    private String hocKy;     // HK1, HK2, HK3
    private String namHoc;    // VD: 2025-2026
    private String ghiChu;

    // Các trường JOIN để hiển thị
    private String tenMon;
    private String loaiMon;
    private String tenLop;
    private int siSoLop;
    private String hoTenGv;
    private String tenPhong;
    private int sucChuaPhong;
    private String loaiPhong;

    public ThoiKhoaBieu() {
    }

    public ThoiKhoaBieu(int id, String maMon, String maLop, String maGv, String maPhong,
                        int thuTrongTuan, int tietBatDau, int soTiet, int tietKetThuc,
                        int tuanBatDau, int tuanKetThuc, String hocKy, String namHoc, String ghiChu) {
        this.id = id;
        this.maMon = maMon;
        this.maLop = maLop;
        this.maGv = maGv;
        this.maPhong = maPhong;
        this.thuTrongTuan = thuTrongTuan;
        this.tietBatDau = tietBatDau;
        this.soTiet = soTiet;
        this.tietKetThuc = tietKetThuc;
        this.tuanBatDau = tuanBatDau;
        this.tuanKetThuc = tuanKetThuc;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public int getThuTrongTuan() {
        return thuTrongTuan;
    }

    public void setThuTrongTuan(int thuTrongTuan) {
        this.thuTrongTuan = thuTrongTuan;
    }

    public int getTietBatDau() {
        return tietBatDau;
    }

    public void setTietBatDau(int tietBatDau) {
        this.tietBatDau = tietBatDau;
    }

    public int getSoTiet() {
        return soTiet;
    }

    public void setSoTiet(int soTiet) {
        this.soTiet = soTiet;
        this.tietKetThuc = this.tietBatDau + soTiet - 1;
    }

    public int getTietKetThuc() {
        return tietKetThuc;
    }

    public void setTietKetThuc(int tietKetThuc) {
        this.tietKetThuc = tietKetThuc;
    }

    public int getTuanBatDau() {
        return tuanBatDau;
    }

    public void setTuanBatDau(int tuanBatDau) {
        this.tuanBatDau = tuanBatDau;
    }

    public int getTuanKetThuc() {
        return tuanKetThuc;
    }

    public void setTuanKetThuc(int tuanKetThuc) {
        this.tuanKetThuc = tuanKetThuc;
    }

    public String getHocKy() {
        return hocKy;
    }

    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Getters & Setters cho các trường JOIN
    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(String loaiMon) {
        this.loaiMon = loaiMon;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public int getSiSoLop() {
        return siSoLop;
    }

    public void setSiSoLop(int siSoLop) {
        this.siSoLop = siSoLop;
    }

    public String getHoTenGv() {
        return hoTenGv;
    }

    public void setHoTenGv(String hoTenGv) {
        this.hoTenGv = hoTenGv;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public int getSucChuaPhong() {
        return sucChuaPhong;
    }

    public void setSucChuaPhong(int sucChuaPhong) {
        this.sucChuaPhong = sucChuaPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public String getThuText() {
        if (thuTrongTuan == 8) return "Chủ Nhật";
        return "Thứ " + thuTrongTuan;
    }

    public String getTietText() {
        return "Tiết " + tietBatDau + " - " + tietKetThuc + " (" + soTiet + " tiết)";
    }

    public String getTuanText() {
        return "Tuần " + tuanBatDau + " - " + tuanKetThuc;
    }
}
