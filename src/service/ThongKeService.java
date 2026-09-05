package service;

import dao.GiangVienDAO;
import dao.LopHocDAO;
import dao.MonHocDAO;
import dao.PhongHocDAO;
import dao.ThoiKhoaBieuDAO;
import model.PhongHoc;
import model.ThoiKhoaBieu;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dịch vụ thống kê hiệu suất sử dụng phòng học, tìm kiếm phòng trống
 * và cung cấp số liệu tổng quan cho Dashboard.
 */
public class ThongKeService {

    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final MonHocDAO monHocDAO;
    private final LopHocDAO lopHocDAO;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;

    public ThongKeService() {
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.monHocDAO = new MonHocDAO();
        this.lopHocDAO = new LopHocDAO();
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
    }

    /**
     * DTO chứa thông tin tỷ lệ sử dụng phòng học.
     */
    public static class PhongSuDungDTO {
        private final PhongHoc phongHoc;
        private final int tongSoTietDaXep;
        private final int tongSoTietKhaDung;
        private final double tyLeSuDung;

        public PhongSuDungDTO(PhongHoc phongHoc, int tongSoTietDaXep, int tongSoTietKhaDung) {
            this.phongHoc = phongHoc;
            this.tongSoTietDaXep = tongSoTietDaXep;
            this.tongSoTietKhaDung = tongSoTietKhaDung;
            this.tyLeSuDung = tongSoTietKhaDung > 0 ? ((double) tongSoTietDaXep / tongSoTietKhaDung) * 100.0 : 0;
        }

        public PhongHoc getPhongHoc() {
            return phongHoc;
        }

        public int getTongSoTietDaXep() {
            return tongSoTietDaXep;
        }

        public int getTongSoTietKhaDung() {
            return tongSoTietKhaDung;
        }

        public double getTyLeSuDung() {
            return tyLeSuDung;
        }

        public String getTyLeFormatted() {
            return String.format("%.1f%%", tyLeSuDung);
        }
    }

    /**
     * Thống kê tỷ lệ sử dụng của toàn bộ phòng học trong một học kỳ & năm học.
     * Quy ước chuẩn: 1 tuần có 6 ngày học (Thứ 2 -> Thứ 7) x 12 tiết = 72 tiết khả dụng/tuần.
     */
    public List<PhongSuDungDTO> thongKeTyLeSuDungPhong(String hocKy, String namHoc) {
        List<PhongHoc> allRooms = phongHocDAO.getAll();
        List<ThoiKhoaBieu> allSchedules = thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, null, null, null, null, null);

        // Gom nhóm tổng số tiết theo từng mã phòng
        Map<String, Integer> roomPeriodsMap = new HashMap<>();
        for (ThoiKhoaBieu tkb : allSchedules) {
            String maPhong = tkb.getMaPhong().toUpperCase();
            roomPeriodsMap.put(maPhong, roomPeriodsMap.getOrDefault(maPhong, 0) + tkb.getSoTiet());
        }

        List<PhongSuDungDTO> result = new ArrayList<>();
        int tongTietKhaDung = 72; // 6 ngày x 12 tiết / tuần

        for (PhongHoc p : allRooms) {
            int tietDaXep = roomPeriodsMap.getOrDefault(p.getMaPhong().toUpperCase(), 0);
            result.add(new PhongSuDungDTO(p, tietDaXep, tongTietKhaDung));
        }

        return result;
    }

    /**
     * Tìm danh sách phòng học còn TRỐNG trong một khung giờ cụ thể.
     */
    public List<PhongHoc> timPhongTrong(String hocKy, String namHoc, int thuTrongTuan,
                                       int tietBatDau, int soTiet, int tuan,
                                       String loaiPhong, int minSucChua) {
        List<PhongHoc> availableRooms = phongHocDAO.getAvailableRooms();
        List<ThoiKhoaBieu> existingSchedules = thoiKhoaBieuDAO.getSchedulesForConflictCheck(hocKy, namHoc, thuTrongTuan);

        int targetEnd = tietBatDau + soTiet - 1;
        List<PhongHoc> vacantRooms = new ArrayList<>();

        for (PhongHoc p : availableRooms) {
            // Lọc theo loại phòng nếu có chỉ định
            if (!ValidationUtil.isNullOrEmpty(loaiPhong) && !"TẤT CẢ".equalsIgnoreCase(loaiPhong)) {
                if (!p.getLoaiPhong().equalsIgnoreCase(loaiPhong)) {
                    continue;
                }
            }

            // Lọc theo sức chứa tối thiểu
            if (minSucChua > 0 && p.getSucChua() < minSucChua) {
                continue;
            }

            // Kiểm tra xem phòng này có bị trùng lịch nào trong khung giờ không
            boolean isOccupied = false;
            for (ThoiKhoaBieu exist : existingSchedules) {
                if (exist.getMaPhong().equalsIgnoreCase(p.getMaPhong())) {
                    // Kiểm tra tuần
                    if (XepLichService.isWeekOverlapping(tuan, tuan, exist.getTuanBatDau(), exist.getTuanKetThuc())) {
                        // Kiểm tra tiết
                        if (XepLichService.isTimeOverlapping(tietBatDau, targetEnd, exist.getTietBatDau(), exist.getTietKetThuc())) {
                            isOccupied = true;
                            break;
                        }
                    }
                }
            }

            if (!isOccupied) {
                vacantRooms.add(p);
            }
        }

        return vacantRooms;
    }

    /**
     * Lấy các số liệu tổng quan cho trang Dashboard.
     */
    public Map<String, Integer> getDashboardSummary() {
        Map<String, Integer> map = new HashMap<>();
        map.put("totalRooms", phongHocDAO.getAll().size());
        map.put("totalLecturers", giangVienDAO.getAll().size());
        map.put("totalSubjects", monHocDAO.getAll().size());
        map.put("totalClasses", lopHocDAO.getAll().size());
        map.put("totalSchedules", thoiKhoaBieuDAO.getAll().size());
        return map;
    }
}
