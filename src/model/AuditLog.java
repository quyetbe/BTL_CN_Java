package model;

import java.sql.Timestamp;

/**
 * Thực thể Nhật ký hệ thống (Audit Log).
 */
public class AuditLog {

    private int id;
    private Integer userId;
    private String tenDangNhap;
    private String hanhDong;
    private String doiTuong;
    private Integer doiTuongId;
    private String duLieuCu;
    private String duLieuMoi;
    private String ipAddress;
    private Timestamp ngayTao;

    public AuditLog() {
    }

    public AuditLog(int id, Integer userId, String tenDangNhap, String hanhDong, String doiTuong, Integer doiTuongId, String duLieuCu, String duLieuMoi, String ipAddress, Timestamp ngayTao) {
        this.id = id;
        this.userId = userId;
        this.tenDangNhap = tenDangNhap;
        this.hanhDong = hanhDong;
        this.doiTuong = doiTuong;
        this.doiTuongId = doiTuongId;
        this.duLieuCu = duLieuCu;
        this.duLieuMoi = duLieuMoi;
        this.ipAddress = ipAddress;
        this.ngayTao = ngayTao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public String getDoiTuong() { return doiTuong; }
    public void setDoiTuong(String doiTuong) { this.doiTuong = doiTuong; }

    public Integer getDoiTuongId() { return doiTuongId; }
    public void setDoiTuongId(Integer doiTuongId) { this.doiTuongId = doiTuongId; }

    public String getDuLieuCu() { return duLieuCu; }
    public void setDuLieuCu(String duLieuCu) { this.duLieuCu = duLieuCu; }

    public String getDuLieuMoi() { return duLieuMoi; }
    public void setDuLieuMoi(String duLieuMoi) { this.duLieuMoi = duLieuMoi; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}
