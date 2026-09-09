package controller;

import dao.ThoiKhoaBieuDAO;
import model.ThoiKhoaBieu;
import service.XepLichService;

import java.util.List;

/**
 * Controller điều phối quản lý và xếp lịch thời khóa biểu.
 */
public class TimetableController {

    private final ThoiKhoaBieuDAO thoiKhoaBieuDAO;
    private final XepLichService xepLichService;

    public TimetableController() {
        this.thoiKhoaBieuDAO = new ThoiKhoaBieuDAO();
        this.xepLichService = new XepLichService();
    }

    public List<ThoiKhoaBieu> getAll() {
        return thoiKhoaBieuDAO.getAll();
    }

    public List<ThoiKhoaBieu> getByFilter(String hocKy, String namHoc, Integer tuan, String maPhong, String maGv, String maLop, Integer thuTrongTuan) {
        return thoiKhoaBieuDAO.getByFilter(hocKy, namHoc, tuan, maPhong, maGv, maLop, thuTrongTuan);
    }

    public XepLichService.ConflictResult validateAndSave(ThoiKhoaBieu tkb, boolean isUpdate) {
        Integer excludeId = isUpdate ? tkb.getId() : null;
        XepLichService.ConflictResult check = xepLichService.validateAndCheckConflict(tkb, excludeId);
        if (!check.isValid()) {
            return check;
        }

        boolean ok = isUpdate ? thoiKhoaBieuDAO.update(tkb) : thoiKhoaBieuDAO.insert(tkb);
        if (ok) {
            return XepLichService.ConflictResult.success();
        } else {
            return XepLichService.ConflictResult.error("Lỗi khi ghi dữ liệu xuống CSDL!", null);
        }
    }

    public boolean delete(int id) {
        return thoiKhoaBieuDAO.delete(id);
    }
}
