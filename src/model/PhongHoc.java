package model;

/**
 * Đại diện cho thông tin phòng học và tài nguyên cơ sở vật chất.
 */
public class PhongHoc {
    private String maPhong;
    private String tenPhong;
    private String toaNha;
    private int sucChua;
    private String loaiPhong; // LY_THUYET, THUC_HANH, HOI_TRUONG
    private String trangThietBi;
    private String trangThai; // DANG_SU_DUNG, BAO_TRI, NGUNG_SU_DUNG

    public PhongHoc() {
    }

    public PhongHoc(String maPhong, String tenPhong, String toaNha, int sucChua, String loaiPhong, String trangThietBi, String trangThai) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.toaNha = toaNha;
        this.sucChua = sucChua;
        this.loaiPhong = loaiPhong;
        this.trangThietBi = trangThietBi;
        this.trangThai = trangThai;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getToaNha() {
        return toaNha;
    }

    public void setToaNha(String toaNha) {
        this.toaNha = toaNha;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public String getTrangThietBi() {
        return trangThietBi;
    }

    public void setTrangThietBi(String trangThietBi) {
        this.trangThietBi = trangThietBi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLoaiPhongDisplay() {
        if ("LY_THUYET".equalsIgnoreCase(loaiPhong)) return "Lý thuyết";
        if ("THUC_HANH".equalsIgnoreCase(loaiPhong)) return "Thực hành / Máy tính";
        if ("HOI_TRUONG".equalsIgnoreCase(loaiPhong)) return "Hội trường";
        return loaiPhong;
    }

    public String getTrangThaiDisplay() {
        if ("DANG_SU_DUNG".equalsIgnoreCase(trangThai)) return "Đang sử dụng";
        if ("BAO_TRI".equalsIgnoreCase(trangThai)) return "Đang bảo trì";
        if ("NGUNG_SU_DUNG".equalsIgnoreCase(trangThai)) return "Ngừng sử dụng";
        return trangThai;
    }

    public boolean isKhaDung() {
        return "DANG_SU_DUNG".equalsIgnoreCase(trangThai);
    }

    @Override
    public String toString() {
        return maPhong + " - " + tenPhong + " (Sức chứa: " + sucChua + ")";
    }
}
