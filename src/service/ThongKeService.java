package service;

import dao.GiangVienDAO;
import dao.LopHocDAO;
import dao.MonHocDAO;
import dao.PhongHocDAO;
import dao.SinhVienDAO;
import dao.ThoiKhoaBieuDAO;
import model.GiangVien;
import model.LopHoc;
import model.PhongHoc;
import model.ThoiKhoaBieu;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dịch vụ thống kê hiệu suất sử dụng phòng học, tìm kiếm phòng trống,
 * thống kê giảng viên, sinh viên và cung cấp số liệu tổng quan cho Dashboard.
 */
public class ThongKeService {

    private final PhongHocDAO phongHocDAO;
    private final GiangVienDAO giangVienDAO;
    private final MonHocDAO monHocDAO;
    private final LopHocDAO lopHocDAO;
    private final SinhVienDAO sinhVienDAO;
    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;

    public ThongKeService() {
        this.phongHocDAO = new PhongHocDAO();
        this.giangVienDAO = new GiangVienDAO();
        this.monHocDAO = new MonHocDAO();
        this.lopHocDAO = new LopHocDAO();
        this.sinhVienDAO = new SinhVienDAO();
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
        map.put("totalStudents", sinhVienDAO.countTotal());
        return map;
    }

    /**
     * DTO thông tin Thống kê Giảng viên.
     */
    public static class ThongKeGiangVienDTO {
        private final GiangVien giangVien;
        private final int soLopPhuTrach;
        private final int soTietDayTuan;
        private final String danhSachMon;
        private final String danhSachLop;
        private final String danhGiaTai;

        public ThongKeGiangVienDTO(GiangVien giangVien, int soLopPhuTrach, int soTietDayTuan,
                                   String danhSachMon, String danhSachLop, String danhGiaTai) {
            this.giangVien = giangVien;
            this.soLopPhuTrach = soLopPhuTrach;
            this.soTietDayTuan = soTietDayTuan;
            this.danhSachMon = danhSachMon;
            this.danhSachLop = danhSachLop;
            this.danhGiaTai = danhGiaTai;
        }

        public GiangVien getGiangVien() { return giangVien; }
        public int getSoLopPhuTrach() { return soLopPhuTrach; }
        public int getSoTietDayTuan() { return soTietDayTuan; }
        public String getDanhSachMon() { return danhSachMon; }
        public String getDanhSachLop() { return danhSachLop; }
        public String getDanhGiaTai() { return danhGiaTai; }
    }

    /**
     * Thống kê toàn bộ Giảng viên, tính số lớp phụ trách, số tiết dạy/tuần và đánh giá tải.
     */
    public List<ThongKeGiangVienDTO> thongKeGiangVien(String hocKy, String namHoc, String khoaBoMon, String keyword) {
        List<GiangVien> allTeachers = giangVienDAO.getAll();
        List<ThoiKhoaBieu> allSchedules = thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, null, null, null, null, null);

        Map<String, List<ThoiKhoaBieu>> mapGvTkb = new HashMap<>();
        for (ThoiKhoaBieu tkb : allSchedules) {
            if (tkb.getMaGv() != null) {
                mapGvTkb.computeIfAbsent(tkb.getMaGv().trim().toUpperCase(), k -> new ArrayList<>()).add(tkb);
            }
        }

        List<ThongKeGiangVienDTO> result = new ArrayList<>();
        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";

        for (GiangVien gv : allTeachers) {
            // Lọc theo khoa bộ môn
            if (!ValidationUtil.isNullOrEmpty(khoaBoMon) && !"TẤT CẢ".equalsIgnoreCase(khoaBoMon)) {
                if (!khoaBoMon.equalsIgnoreCase(gv.getKhoaBoMon())) {
                    continue;
                }
            }

            // Lọc theo từ khóa tìm kiếm
            if (!kw.isEmpty()) {
                boolean matchMa = gv.getMaGv() != null && gv.getMaGv().toLowerCase().contains(kw);
                boolean matchTen = gv.getHoTen() != null && gv.getHoTen().toLowerCase().contains(kw);
                boolean matchEmail = gv.getEmail() != null && gv.getEmail().toLowerCase().contains(kw);
                if (!matchMa && !matchTen && !matchEmail) {
                    continue;
                }
            }

            List<ThoiKhoaBieu> listTkb = mapGvTkb.get(gv.getMaGv().trim().toUpperCase());
            int soTietDay = 0;
            Set<String> setLop = new LinkedHashSet<>();
            Set<String> setMon = new LinkedHashSet<>();

            if (listTkb != null) {
                for (ThoiKhoaBieu t : listTkb) {
                    soTietDay += t.getSoTiet();
                    if (t.getMaLop() != null) setLop.add(t.getMaLop());
                    if (t.getTenMon() != null && !t.getTenMon().isEmpty()) {
                        setMon.add(t.getTenMon());
                    } else if (t.getMaMon() != null) {
                        setMon.add(t.getMaMon());
                    }
                }
            }

            String danhGia;
            if (soTietDay == 0) {
                danhGia = "Chưa xếp lịch";
            } else if (soTietDay < 8) {
                danhGia = "Tải thấp (< 8 tiết)";
            } else if (soTietDay <= 16) {
                danhGia = "Đủ định mức (8-16 tiết)";
            } else {
                danhGia = "Vượt tải (> 16 tiết)";
            }

            String monStr = setMon.isEmpty() ? "—" : String.join(", ", setMon);
            String lopStr = setLop.isEmpty() ? "—" : String.join(", ", setLop);

            result.add(new ThongKeGiangVienDTO(gv, setLop.size(), soTietDay, monStr, lopStr, danhGia));
        }

        // Sắp xếp: Giảng viên có tiết dạy nhiều xếp trước, sau đó theo tên
        result.sort((a, b) -> {
            if (b.getSoTietDayTuan() != a.getSoTietDayTuan()) {
                return Integer.compare(b.getSoTietDayTuan(), a.getSoTietDayTuan());
            }
            return a.getGiangVien().getHoTen().compareToIgnoreCase(b.getGiangVien().getHoTen());
        });

        return result;
    }

    /**
     * DTO Thống kê Sinh viên theo từng Lớp học.
     */
    public static class ThongKeSinhVienLopDTO {
        private final LopHoc lopHoc;
        private final int tongSv;
        private final int soNam;
        private final int soNu;
        private final int soDangHoc;
        private final int soMonHocTuan;
        private final int soTietHocTuan;

        public ThongKeSinhVienLopDTO(LopHoc lopHoc, int tongSv, int soNam, int soNu,
                                     int soDangHoc, int soMonHocTuan, int soTietHocTuan) {
            this.lopHoc = lopHoc;
            this.tongSv = tongSv;
            this.soNam = soNam;
            this.soNu = soNu;
            this.soDangHoc = soDangHoc;
            this.soMonHocTuan = soMonHocTuan;
            this.soTietHocTuan = soTietHocTuan;
        }

        public LopHoc getLopHoc() { return lopHoc; }
        public int getTongSv() { return tongSv; }
        public int getSoNam() { return soNam; }
        public int getSoNu() { return soNu; }
        public int getSoDangHoc() { return soDangHoc; }
        public int getSoMonHocTuan() { return soMonHocTuan; }
        public int getSoTietHocTuan() { return soTietHocTuan; }

        public String getTyLeNamNu() {
            if (tongSv == 0) return "0% / 0%";
            double pctNam = ((double) soNam / tongSv) * 100.0;
            double pctNu = ((double) soNu / tongSv) * 100.0;
            return String.format("%.0f%% Nam - %.0f%% Nữ", pctNam, pctNu);
        }
    }

    /**
     * Thống kê sinh viên theo danh sách lớp học và tải học tập.
     */
    public List<ThongKeSinhVienLopDTO> thongKeSinhVienTheoLop(String hocKy, String namHoc, String khoaHoc) {
        List<LopHoc> allLop = lopHocDAO.getAll();
        List<SinhVienDAO.ThongKeLopRecord> lopSvRecords = sinhVienDAO.getThongKeTheoLop();
        Map<String, SinhVienDAO.ThongKeLopRecord> mapSvByLop = new HashMap<>();
        for (SinhVienDAO.ThongKeLopRecord r : lopSvRecords) {
            mapSvByLop.put(r.maLop.toUpperCase(), r);
        }

        List<ThoiKhoaBieu> allSchedules = thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, null, null, null, null, null);
        Map<String, List<ThoiKhoaBieu>> mapLopTkb = new HashMap<>();
        for (ThoiKhoaBieu tkb : allSchedules) {
            if (tkb.getMaLop() != null) {
                mapLopTkb.computeIfAbsent(tkb.getMaLop().trim().toUpperCase(), k -> new ArrayList<>()).add(tkb);
            }
        }

        List<ThongKeSinhVienLopDTO> result = new ArrayList<>();
        for (LopHoc lh : allLop) {
            if (!ValidationUtil.isNullOrEmpty(khoaHoc) && !"TẤT CẢ".equalsIgnoreCase(khoaHoc) && !"TẤT CẢ KHÓA".equalsIgnoreCase(khoaHoc)) {
                if (!khoaHoc.equalsIgnoreCase(lh.getKhoaHoc())) {
                    continue;
                }
            }

            SinhVienDAO.ThongKeLopRecord svRec = mapSvByLop.get(lh.getMaLop().toUpperCase());
            int tongSv = svRec != null ? svRec.tongSv : lh.getSiSo();
            int nam = svRec != null ? svRec.namCount : 0;
            int nu = svRec != null ? svRec.nuCount : 0;
            int dangHoc = svRec != null ? svRec.dangHocCount : tongSv;

            List<ThoiKhoaBieu> listTkb = mapLopTkb.get(lh.getMaLop().toUpperCase());
            int soTiet = 0;
            Set<String> monSet = new LinkedHashSet<>();
            if (listTkb != null) {
                for (ThoiKhoaBieu t : listTkb) {
                    soTiet += t.getSoTiet();
                    monSet.add(t.getMaMon());
                }
            }

            result.add(new ThongKeSinhVienLopDTO(lh, tongSv, nam, nu, dangHoc, monSet.size(), soTiet));
        }

        result.sort((a, b) -> a.getLopHoc().getMaLop().compareToIgnoreCase(b.getLopHoc().getMaLop()));
        return result;
    }

    /**
     * DTO Thống kê Tổng hợp theo Khóa học (K21, K22, K23, K24).
     */
    public static class ThongKeKhoaHocDTO {
        private final String khoaHoc;
        private final int soLop;
        private final int tongSv;
        private final int soNam;
        private final int soNu;

        public ThongKeKhoaHocDTO(String khoaHoc, int soLop, int tongSv, int soNam, int soNu) {
            this.khoaHoc = khoaHoc;
            this.soLop = soLop;
            this.tongSv = tongSv;
            this.soNam = soNam;
            this.soNu = soNu;
        }

        public String getKhoaHoc() { return khoaHoc; }
        public int getSoLop() { return soLop; }
        public int getTongSv() { return tongSv; }
        public int getSoNam() { return soNam; }
        public int getSoNu() { return soNu; }

        public String getTyLeNamNu() {
            if (tongSv == 0) return "0% / 0%";
            double pctNam = ((double) soNam / tongSv) * 100.0;
            double pctNu = ((double) soNu / tongSv) * 100.0;
            return String.format("%.1f%% Nam - %.1f%% Nữ", pctNam, pctNu);
        }
    }

    /**
     * Thống kê tổng hợp số lượng sinh viên theo từng Khóa học.
     */
    public List<ThongKeKhoaHocDTO> thongKeTheoKhoaHoc() {
        List<SinhVienDAO.ThongKeKhoaHocRecord> raw = sinhVienDAO.getThongKeKhoaHoc();
        List<ThongKeKhoaHocDTO> result = new ArrayList<>();
        for (SinhVienDAO.ThongKeKhoaHocRecord r : raw) {
            result.add(new ThongKeKhoaHocDTO(r.khoaHoc, r.lopCount, r.tongSv, r.namCount, r.nuCount));
        }
        return result;
    }
}
