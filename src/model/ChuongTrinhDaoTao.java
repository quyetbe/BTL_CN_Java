package model;

/**
 * Thực thể Môn học trong Khung Chương trình Đào tạo 4 năm (8 học kỳ).
 */
public class ChuongTrinhDaoTao {

    private int id;
    private String khoa;
    private int hocKy;
    private String maMon;
    private String tenMon;
    private int soTinChi;
    private String loaiMon;
    private String khoaHoc;

    public ChuongTrinhDaoTao() {
    }

    public ChuongTrinhDaoTao(int id, String khoa, int hocKy, String maMon, String tenMon, int soTinChi, String loaiMon, String khoaHoc) {
        this.id = id;
        this.khoa = khoa;
        this.hocKy = hocKy;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.loaiMon = loaiMon;
        this.khoaHoc = khoaHoc;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }

    public String getLoaiMon() { return loaiMon; }
    public void setLoaiMon(String loaiMon) { this.loaiMon = loaiMon; }

    public String getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(String khoaHoc) { this.khoaHoc = khoaHoc; }

    public boolean isThucHanh() {
        return "THUC_HANH".equalsIgnoreCase(loaiMon);
    }
}
