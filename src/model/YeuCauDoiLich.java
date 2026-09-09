package model;

import java.sql.Timestamp;

/**
 * Thực thể Yêu cầu đổi lịch giảng dạy (Quy trình duyệt 2 cấp).
 */
public class YeuCauDoiLich {

    private int id;
    private int maTkb;
    private String maGv;
    private String maPhongMoi;
    private int thuMoi;
    private int tietBatDauMoi;
    private int soTiet;
    private int tuanBatDauMoi;
    private int tuanKetThucMoi;
    private String lyDo;
    private String trangThai; // CHO_KHOA_DUYET, CHO_BGH_DUYET, DA_PHE_DUYET, TU_CHOI
    private int capPheDuyet;  // 1: Khoa, 2: BGH/Đào tạo
    private Timestamp ngayTao;

    // Thuộc tính JOIN để hiển thị
    private String hoTenGv;
    private String tenMon;
    private String tenLop;
    private String maPhongCu;
    private int thuCu;
    private int tietBatDauCu;

    public YeuCauDoiLich() {
    }

    public YeuCauDoiLich(int id, int maTkb, String maGv, String maPhongMoi, int thuMoi, int tietBatDauMoi, int soTiet, int tuanBatDauMoi, int tuanKetThucMoi, String lyDo, String trangThai, int capPheDuyet, Timestamp ngayTao) {
        this.id = id;
        this.maTkb = maTkb;
        this.maGv = maGv;
        this.maPhongMoi = maPhongMoi;
        this.thuMoi = thuMoi;
        this.tietBatDauMoi = tietBatDauMoi;
        this.soTiet = soTiet;
        this.tuanBatDauMoi = tuanBatDauMoi;
        this.tuanKetThucMoi = tuanKetThucMoi;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.capPheDuyet = capPheDuyet;
        this.ngayTao = ngayTao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMaTkb() { return maTkb; }
    public void setMaTkb(int maTkb) { this.maTkb = maTkb; }

    public String getMaGv() { return maGv; }
    public void setMaGv(String maGv) { this.maGv = maGv; }

    public String getMaPhongMoi() { return maPhongMoi; }
    public void setMaPhongMoi(String maPhongMoi) { this.maPhongMoi = maPhongMoi; }

    public int getThuMoi() { return thuMoi; }
    public void setThuMoi(int thuMoi) { this.thuMoi = thuMoi; }

    public int getTietBatDauMoi() { return tietBatDauMoi; }
    public void setTietBatDauMoi(int tietBatDauMoi) { this.tietBatDauMoi = tietBatDauMoi; }

    public int getSoTiet() { return soTiet; }
    public void setSoTiet(int soTiet) { this.soTiet = soTiet; }

    public int getTuanBatDauMoi() { return tuanBatDauMoi; }
    public void setTuanBatDauMoi(int tuanBatDauMoi) { this.tuanBatDauMoi = tuanBatDauMoi; }

    public int getTuanKetThucMoi() { return tuanKetThucMoi; }
    public void setTuanKetThucMoi(int tuanKetThucMoi) { this.tuanKetThucMoi = tuanKetThucMoi; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getCapPheDuyet() { return capPheDuyet; }
    public void setCapPheDuyet(int capPheDuyet) { this.capPheDuyet = capPheDuyet; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    public String getHoTenGv() { return hoTenGv; }
    public void setHoTenGv(String hoTenGv) { this.hoTenGv = hoTenGv; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getMaPhongCu() { return maPhongCu; }
    public void setMaPhongCu(String maPhongCu) { this.maPhongCu = maPhongCu; }

    public int getThuCu() { return thuCu; }
    public void setThuCu(int thuCu) { this.thuCu = thuCu; }

    public int getTietBatDauCu() { return tietBatDauCu; }
    public void setTietBatDauCu(int tietBatDauCu) { this.tietBatDauCu = tietBatDauCu; }
}
