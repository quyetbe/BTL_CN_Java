package service;

import connection.DBConnection;
import dao.AuditLogDAO;
import dao.ThoiKhoaBieuDAO;
import dao.YeuCauDoiLichDAO;
import model.LichSuDuyet;
import model.ThoiKhoaBieu;
import model.YeuCauDoiLich;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Xử lý Quy trình phê duyệt đổi lịch đa cấp (Workflow State Machine):
 * Cấp 1: Trưởng bộ môn / Khoa duyệt (CHO_KHOA_DUYET -> CHO_BGH_DUYET).
 * Cấp 2: Ban Giám hiệu / Phòng Đào tạo duyệt (CHO_BGH_DUYET -> DA_PHE_DUYET).
 * Tự động cập nhật Atomic Transaction sang bảng thoi_khoa_bieu khi duyệt Cấp 2 thành công.
 */
public class WorkflowService {

    private final YeuCauDoiLichDAO yeuCauDAO;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final XepLichService xepLichService;
    private final AuditLogDAO auditDAO;

    public WorkflowService() {
        this.yeuCauDAO = new YeuCauDoiLichDAO();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.xepLichService = new XepLichService();
        this.auditDAO = new AuditLogDAO();
    }

    /**
     * Giảng viên gửi đề xuất đổi lịch
     */
    public String submitRequest(YeuCauDoiLich req) {
        ThoiKhoaBieu orig = thoiKhoaBieuDAO.getById(req.getMaTkb());
        if (orig == null) return "LỖI: Ca học gốc không tồn tại!";

        // Kiểm tra xung đột ở vị trí mới
        ThoiKhoaBieu target = new ThoiKhoaBieu();
        target.setMaMon(orig.getMaMon());
        target.setMaLop(orig.getMaLop());
        target.setMaGv(orig.getMaGv());
        target.setMaPhong(req.getMaPhongMoi());
        target.setThuTrongTuan(req.getThuMoi());
        target.setTietBatDau(req.getTietBatDauMoi());
        target.setSoTiet(req.getSoTiet());
        target.setTuanBatDau(req.getTuanBatDauMoi());
        target.setTuanKetThuc(req.getTuanKetThucMoi());
        target.setHocKy(orig.getHocKy());
        target.setNamHoc(orig.getNamHoc());

        XepLichService.ConflictResult conflict = xepLichService.validateAndCheckConflict(target, orig.getId());
        if (!conflict.isValid()) {
            return "KHÔNG THỂ ĐỀ XUẤT: " + conflict.getMessage();
        }

        boolean ok = yeuCauDAO.insert(req);
        if (ok) {
            auditDAO.log(null, req.getMaGv(), "Đề xuất đổi lịch", "YEU_CAU_DOI_LICH", req.getId(), null, "Tạo yêu cầu đổi lịch", "127.0.0.1");
            return "SUCCESS: Đã gửi yêu cầu đổi lịch! Hồ sơ đang chờ Trưởng bộ môn/Khoa phê duyệt (Cấp 1).";
        }
        return "LỖI: Không thể lưu yêu cầu vào cơ sở dữ liệu.";
    }

    /**
     * Trưởng bộ môn / Khoa duyệt Cấp 1
     */
    public boolean approveLevel1(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        YeuCauDoiLich req = yeuCauDAO.getById(requestId);
        if (req == null || !"CHO_KHOA_DUYET".equals(req.getTrangThai())) {
            return false;
        }

        String nextStatus = isApproved ? "CHO_BGH_DUYET" : "TU_CHOI";
        int nextLevel = isApproved ? 2 : 1;

        boolean ok = yeuCauDAO.updateStatusAndLevel(requestId, nextStatus, nextLevel);
        if (ok) {
            LichSuDuyet ls = new LichSuDuyet(0, requestId, approverId, "TRUONG_BO_MON", isApproved ? "DONG_Y" : "TU_CHOI", comments, null);
            yeuCauDAO.addApprovalHistory(ls);
            auditDAO.log(approverId, approverName, isApproved ? "Duyệt cấp 1 (Khoa)" : "Từ chối cấp 1", "YEU_CAU_DOI_LICH", requestId, "CHO_KHOA_DUYET", nextStatus, "127.0.0.1");
        }
        return ok;
    }

    /**
     * Ban Giám hiệu / Phòng Đào tạo duyệt Cấp 2 (Quyết định cuối & CẬP NHẬT TKB)
     */
    public boolean approveLevel2(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        YeuCauDoiLich req = yeuCauDAO.getById(requestId);
        if (req == null || !"CHO_BGH_DUYET".equals(req.getTrangThai())) {
            return false;
        }

        if (!isApproved) {
            yeuCauDAO.updateStatusAndLevel(requestId, "TU_CHOI", 2);
            LichSuDuyet ls = new LichSuDuyet(0, requestId, approverId, "BAN_GIAM_HIEU", "TU_CHOI", comments, null);
            yeuCauDAO.addApprovalHistory(ls);
            auditDAO.log(approverId, approverName, "Từ chối cấp 2 (BGH)", "YEU_CAU_DOI_LICH", requestId, "CHO_BGH_DUYET", "TU_CHOI", "127.0.0.1");
            return true;
        }

        // ATOMIC TRANSACTION: Cập nhật TKB và Chuyển trạng thái yêu cầu sang DA_PHE_DUYET
        String sqlUpdateTkb = "UPDATE thoi_khoa_bieu SET ma_phong = ?, thu_trong_tuan = ?, tiet_bat_dau = ?, "
                            + "so_tiet = ?, tiet_ket_thuc = ?, tuan_bat_dau = ?, tuan_ket_thuc = ? WHERE id = ?";
        String sqlUpdateReq = "UPDATE yeu_cau_doi_lich SET trang_thai = 'DA_PHE_DUYET' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psTkb = conn.prepareStatement(sqlUpdateTkb);
                 PreparedStatement psReq = conn.prepareStatement(sqlUpdateReq)) {

                int tietKetThuc = req.getTietBatDauMoi() + req.getSoTiet() - 1;
                psTkb.setString(1, req.getMaPhongMoi());
                psTkb.setInt(2, req.getThuMoi());
                psTkb.setInt(3, req.getTietBatDauMoi());
                psTkb.setInt(4, req.getSoTiet());
                psTkb.setInt(5, tietKetThuc);
                psTkb.setInt(6, req.getTuanBatDauMoi());
                psTkb.setInt(7, req.getTuanKetThucMoi());
                psTkb.setInt(8, req.getMaTkb());
                psTkb.executeUpdate();

                psReq.setInt(1, requestId);
                psReq.executeUpdate();

                conn.commit();

                LichSuDuyet ls = new LichSuDuyet(0, requestId, approverId, "BAN_GIAM_HIEU", "DONG_Y", comments, null);
                yeuCauDAO.addApprovalHistory(ls);
                auditDAO.log(approverId, approverName, "Phê duyệt & Áp dụng TKB", "THOI_KHOA_BIEU", req.getMaTkb(), "ORIGINAL", "UPDATED_TO_NEW_TIME", "127.0.0.1");
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Lỗi Transaction approveLevel2: " + ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối DB: " + e.getMessage());
        }
        return false;
    }
}
