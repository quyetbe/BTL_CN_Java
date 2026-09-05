package model;

/**
 * Đại diện cho thông tin môn học.
 */
public class MonHoc {
    private String maMon;
    private String tenMon;
    private int soTinChi;
    private String loaiMon; // LY_THUYET hoặc THUC_HANH

    public MonHoc() {
    }

    public MonHoc(String maMon, String tenMon, int soTinChi, String loaiMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.loaiMon = loaiMon;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(String loaiMon) {
        this.loaiMon = loaiMon;
    }

    public String getLoaiMonDisplay() {
        return "THUC_HANH".equalsIgnoreCase(loaiMon) ? "Thực hành" : "Lý thuyết";
    }

    public boolean isThucHanh() {
        return "THUC_HANH".equalsIgnoreCase(loaiMon);
    }

    @Override
    public String toString() {
        return maMon + " - " + tenMon + " (" + getLoaiMonDisplay() + " - " + soTinChi + " TC)";
    }
}
