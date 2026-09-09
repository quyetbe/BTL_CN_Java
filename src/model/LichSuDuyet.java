package model;

import java.sql.Timestamp;

/**
 * Lưu lịch sử các bước phê duyệt đổi lịch (Khoa, Ban Giám Hiệu).
 */
public class LichSuDuyet {

    private int id;
    private int yeuCauId;
    private int nguoiDuyetId;
    private String capDuyet;
    private String hanhDong; // DONG_Y, TU_CHOI
    private String yKien;
    private Timestamp ngayDuyet;

    private String tenNguoiDuyet;

    public LichSuDuyet() {
    }

    public LichSuDuyet(int id, int yeuCauId, int nguoiDuyetId, String capDuyet, String hanhDong, String yKien, Timestamp ngayDuyet) {
        this.id = id;
        this.yeuCauId = yeuCauId;
        this.nguoiDuyetId = nguoiDuyetId;
        this.capDuyet = capDuyet;
        this.hanhDong = hanhDong;
        this.yKien = yKien;
        this.ngayDuyet = ngayDuyet;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getYeuCauId() { return yeuCauId; }
    public void setYeuCauId(int yeuCauId) { this.yeuCauId = yeuCauId; }

    public int getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(int nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }

    public String getCapDuyet() { return capDuyet; }
    public void setCapDuyet(String capDuyet) { this.capDuyet = capDuyet; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public String getyKien() { return yKien; }
    public String getYKien() { return yKien; }
    public void setyKien(String yKien) { this.yKien = yKien; }

    public Timestamp getNgayDuyet() { return ngayDuyet; }
    public Timestamp getThoiGianTao() { return ngayDuyet; }
    public void setNgayDuyet(Timestamp ngayDuyet) { this.ngayDuyet = ngayDuyet; }

    public String getQuyetDinh() { return hanhDong; }

    public String getTenNguoiDuyet() { return tenNguoiDuyet; }
    public void setTenNguoiDuyet(String tenNguoiDuyet) { this.tenNguoiDuyet = tenNguoiDuyet; }
}
