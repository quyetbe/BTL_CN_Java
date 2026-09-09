package service;

import dao.GiangVienDAO;
import dao.LopHocDAO;
import dao.MonHocDAO;
import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import model.GiangVien;
import model.LopHoc;
import model.MonHoc;
import model.PhongHoc;
import model.ThoiKhoaBieu;

import java.util.List;

/**
 * Dịch vụ xếp lịch và kiểm tra xung đột thời khóa biểu đa chiều.
 * Đây là module trọng tâm chứa toàn bộ quy tắc nghiệp vụ xếp thời khóa biểu.
 */
public class XepLichService {

    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final MonHocDAO monHocDAO;
    private final LopHocDAO lopHocDAO;

    public XepLichService() {
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.monHocDAO = new MonHocDAO();
        this.lopHocDAO = new LopHocDAO();
    }

    /**
     * Kết quả kiểm tra xung đột / hợp lệ khi xếp lịch.
     */
    public static class ConflictResult {
        private final boolean valid;
        private final boolean warningOnly;
        private final String message;
        private final ThoiKhoaBieu conflictingSchedule;

        public ConflictResult(boolean valid, boolean warningOnly, String message, ThoiKhoaBieu conflictingSchedule) {
            this.valid = valid;
            this.warningOnly = warningOnly;
            this.message = message;
            this.conflictingSchedule = conflictingSchedule;
        }

        public static ConflictResult success() {
            return new ConflictResult(true, false, "Lịch học hợp lệ, không có xung đột.", null);
        }

        public static ConflictResult error(String message, ThoiKhoaBieu conflictingSchedule) {
            return new ConflictResult(false, false, message, conflictingSchedule);
        }

        public static ConflictResult warning(String message) {
            return new ConflictResult(true, true, message, null);
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isWarningOnly() {
            return warningOnly;
        }

        public String getMessage() {
            return message;
        }

        public ThoiKhoaBieu getConflictingSchedule() {
            return conflictingSchedule;
        }
    }

    /**
     * Kiểm tra giao nhau của hai khoảng tuần.
     * Hai khoảng [A1, A2] và [B1, B2] giao nhau khi:
     * NOT (A2 < B1 OR A1 > B2) <=> (A1 <= B2 AND A2 >= B1)
     */
    public static boolean isWeekOverlapping(int startA, int endA, int startB, int endB) {
        return startA <= endB && endA >= startB;
    }

    /**
     * Kiểm tra giao nhau của hai khoảng tiết học.
     * Hai khoảng [startA, endA] và [startB, endB] giao nhau khi:
     * startA <= endB AND endA >= startB
     * (Không giao nhau khi: endA < startB HOẶC startA > endB)
     */
    public static boolean isTimeOverlapping(int startA, int endA, int startB, int endB) {
        return startA <= endB && endA >= startB;
    }

    /**
     * Kiểm tra xung đột toàn diện trước khi lưu hoặc cập nhật thời khóa biểu.
     *
     * @param target Lịch học cần kiểm tra
     * @param excludeId ID lịch học cần loại trừ (khi sửa chính nó), null nếu là thêm mới
     * @return ConflictResult chứa thông tin chi tiết về lỗi hoặc cảnh báo nếu có
     */
    public ConflictResult validateAndCheckConflict(ThoiKhoaBieu target, Integer excludeId) {
        // 1. Kiểm tra tính tồn tại của phòng học
        PhongHoc phong = phongHocDAO.getById(target.getMaPhong());
        if (phong == null) {
            return ConflictResult.error("Phòng học [" + target.getMaPhong() + "] không tồn tại trong hệ thống!", null);
        }

        // 2. Kiểm tra trạng thái phòng học
        if (!"DANG_SU_DUNG".equalsIgnoreCase(phong.getTrangThai())) {
            return ConflictResult.error("Phòng học [" + target.getMaPhong() + " - " + phong.getTenPhong() 
                    + "] hiện ở trạng thái " + phong.getTrangThaiDisplay().toUpperCase() + ", không thể xếp lịch!", null);
        }

        // 3. Kiểm tra môn học
        MonHoc mon = monHocDAO.getById(target.getMaMon());
        if (mon == null) {
            return ConflictResult.error("Môn học [" + target.getMaMon() + "] không tồn tại trong hệ thống!", null);
        }

        // 4. Kiểm tra lớp học
        LopHoc lop = lopHocDAO.getById(target.getMaLop());
        if (lop == null) {
            return ConflictResult.error("Lớp học [" + target.getMaLop() + "] không tồn tại trong hệ thống!", null);
        }

        // 5. Kiểm tra giảng viên
        GiangVien gv = giangVienDAO.getById(target.getMaGv());
        if (gv == null) {
            return ConflictResult.error("Giảng viên [" + target.getMaGv() + "] không tồn tại trong hệ thống!", null);
        }

        // 6. Kiểm tra loại phòng phù hợp với loại môn
        if (mon.isThucHanh() && !"THUC_HANH".equalsIgnoreCase(phong.getLoaiPhong())) {
            return ConflictResult.error("Môn [" + mon.getTenMon() + "] là môn THỰC HÀNH, bắt buộc phải xếp vào phòng THỰC HÀNH / MÁY TÍNH (Hiện chọn phòng: " + phong.getLoaiPhongDisplay() + ")!", null);
        }

        // 7. Kiểm tra sức chứa phòng vs Sĩ số lớp
        if (phong.getSucChua() < lop.getSiSo()) {
            return ConflictResult.warning("CẢNH BÁO: Sức chứa phòng " + phong.getMaPhong() + " (" + phong.getSucChua() 
                    + " chỗ) nhỏ hơn sĩ số lớp " + lop.getMaLop() + " (" + lop.getSiSo() + " SV)!");
        }

        // 8. Kiểm tra xung đột thời gian với các lịch đã có trong CSDL
        List<ThoiKhoaBieu> existingList = thoiKhoaBieuDAO.getSchedulesForConflictCheck(
                target.getHocKy(), target.getNamHoc(), target.getThuTrongTuan()
        );

        int targetStart = target.getTietBatDau();
        int targetEnd = target.getTietBatDau() + target.getSoTiet() - 1;
        int targetWeekStart = target.getTuanBatDau();
        int targetWeekEnd = target.getTuanKetThuc();

        for (ThoiKhoaBieu exist : existingList) {
            // Bỏ qua chính bản ghi đang sửa
            if (excludeId != null && exist.getId() == excludeId) {
                continue;
            }

            // Kiểm tra xem khoảng tuần có giao nhau không
            if (!isWeekOverlapping(targetWeekStart, targetWeekEnd, exist.getTuanBatDau(), exist.getTuanKetThuc())) {
                continue; // Khác khoảng tuần -> Không xung đột
            }

            // Kiểm tra xem khoảng tiết có giao nhau không
            if (!isTimeOverlapping(targetStart, targetEnd, exist.getTietBatDau(), exist.getTietKetThuc())) {
                continue; // Khác khoảng tiết -> Không xung đột
            }

            // Giao nhau cả thứ, tuần và tiết: Kiểm tra 3 điều kiện xung đột

            // (A) Trùng PHÒNG HỌC
            if (target.getMaPhong().equalsIgnoreCase(exist.getMaPhong())) {
                String msg = String.format(
                        "[XUNG ĐỘT PHÒNG HỌC] Phòng [%s - %s] đã có lịch dạy!\n"
                      + "- Môn: %s (%s)\n"
                      + "- Lớp: %s (Sĩ số: %d)\n"
                      + "- Giảng viên: %s\n"
                      + "- Thời gian: Thứ %d, Tiết %d - %d, Tuần %d - %d",
                        exist.getMaPhong(), exist.getTenPhong(),
                        exist.getTenMon(), exist.getMaMon(),
                        exist.getTenLop(), exist.getSiSoLop(),
                        exist.getHoTenGv(),
                        exist.getThuTrongTuan(), exist.getTietBatDau(), exist.getTietKetThuc(),
                        exist.getTuanBatDau(), exist.getTuanKetThuc()
                );
                return ConflictResult.error(msg, exist);
            }

            // (B) Trùng GIẢNG VIÊN
            if (target.getMaGv().equalsIgnoreCase(exist.getMaGv())) {
                String msg = String.format(
                        "[XUNG ĐỘT GIẢNG VIÊN] Giảng viên [%s - %s] đang có lịch dạy lớp khác!\n"
                      + "- Môn: %s\n"
                      + "- Lớp: %s\n"
                      + "- Phòng: %s\n"
                      + "- Thời gian: Thứ %d, Tiết %d - %d, Tuần %d - %d",
                        exist.getMaGv(), exist.getHoTenGv(),
                        exist.getTenMon(),
                        exist.getTenLop(),
                        exist.getMaPhong(),
                        exist.getThuTrongTuan(), exist.getTietBatDau(), exist.getTietKetThuc(),
                        exist.getTuanBatDau(), exist.getTuanKetThuc()
                );
                return ConflictResult.error(msg, exist);
            }

            // (C) Trùng LỚP HỌC
            if (target.getMaLop().equalsIgnoreCase(exist.getMaLop())) {
                String msg = String.format(
                        "[XUNG ĐỘT LỚP HỌC] Lớp [%s - %s] đã có lịch học môn khác cùng giờ!\n"
                      + "- Môn: %s\n"
                      + "- Giảng viên: %s\n"
                      + "- Phòng: %s\n"
                      + "- Thời gian: Thứ %d, Tiết %d - %d, Tuần %d - %d",
                        exist.getMaLop(), exist.getTenLop(),
                        exist.getTenMon(),
                        exist.getHoTenGv(),
                        exist.getMaPhong(),
                        exist.getThuTrongTuan(), exist.getTietBatDau(), exist.getTietKetThuc(),
                        exist.getTuanBatDau(), exist.getTuanKetThuc()
                );
                return ConflictResult.error(msg, exist);
            }
        }

        return ConflictResult.success();
    }

    public boolean insertSchedule(ThoiKhoaBieu target) {
        return thoiKhoaBieuDAO.insert(target);
    }

    public boolean updateSchedule(ThoiKhoaBieu target) {
        return thoiKhoaBieuDAO.update(target);
    }

    public boolean deleteSchedule(int id) {
        return thoiKhoaBieuDAO.delete(id);
    }

    public List<ThoiKhoaBieu> getScheduleList(String hocKy, String namHoc, Integer tuan,
                                              String maPhong, String maGv, String maLop, Integer thu) {
        return thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, tuan, maPhong, maGv, maLop, thu, null);
    }

    public List<ThoiKhoaBieu> getScheduleList(String hocKy, String namHoc, Integer tuan,
                                              String maPhong, String maGv, String maLop, Integer thu, String khoaHoc) {
        return thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, tuan, maPhong, maGv, maLop, thu, khoaHoc);
    }
}
