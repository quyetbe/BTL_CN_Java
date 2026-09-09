package controller;

import dao.ChuongTrinhDaoTaoDAO;
import model.ChuongTrinhDaoTao;

import java.util.List;

/**
 * Controller quản lý Khung Chương trình Đào tạo 4 năm (8 học kỳ).
 */
public class CurriculumController {

    private final ChuongTrinhDaoTaoDAO dao;

    public CurriculumController() {
        this.dao = new ChuongTrinhDaoTaoDAO();
    }

    public List<ChuongTrinhDaoTao> getAll() {
        return dao.getAll();
    }

    public List<ChuongTrinhDaoTao> getAllCurriculum() {
        return getAll();
    }

    public List<ChuongTrinhDaoTao> getBySemester(int hocKy) {
        return dao.getBySemester(hocKy);
    }
}
