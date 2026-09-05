package model;

/**
 * Đại diện cho thông tin lớp sinh viên.
 */
public class LopHoc {
    private String maLop;
    private String tenLop;
    private int siSo;
    private String khoaHoc;

    public LopHoc() {
    }

    public LopHoc(String maLop, String tenLop, int siSo, String khoaHoc) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.siSo = siSo;
        this.khoaHoc = khoaHoc;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public int getSiSo() {
        return siSo;
    }

    public void setSiSo(int siSo) {
        this.siSo = siSo;
    }

    public String getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(String khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    @Override
    public String toString() {
        return maLop + " - " + tenLop + " (Sĩ số: " + siSo + ")";
    }
}
