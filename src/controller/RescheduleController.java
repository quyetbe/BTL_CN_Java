package controller;

import dao.YeuCauDoiLichDAO;
import model.YeuCauDoiLich;
import service.WorkflowService;

import java.util.List;

/**
 * Controller điều phối Quy trình duyệt đổi lịch đa cấp.
 */
public class RescheduleController {

    private final YeuCauDoiLichDAO dao;
    private final WorkflowService workflowService;

    public RescheduleController() {
        this.dao = new YeuCauDoiLichDAO();
        this.workflowService = new WorkflowService();
    }

    public List<YeuCauDoiLich> getRequests(String statusFilter) {
        return dao.getAll(statusFilter);
    }

    public List<YeuCauDoiLich> getAllRequests() {
        return dao.getAll(null);
    }

    public YeuCauDoiLich getById(int id) {
        return dao.getById(id);
    }

    public String submitRequest(YeuCauDoiLich req) {
        return workflowService.submitRequest(req);
    }

    public boolean approveLevel1(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        return workflowService.approveLevel1(requestId, approverId, approverName, isApproved, comments);
    }

    public boolean reviewLevel1(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        return approveLevel1(requestId, approverId, approverName, isApproved, comments);
    }

    public boolean approveLevel2(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        return workflowService.approveLevel2(requestId, approverId, approverName, isApproved, comments);
    }

    public boolean reviewLevel2(int requestId, int approverId, String approverName, boolean isApproved, String comments) {
        return approveLevel2(requestId, approverId, approverName, isApproved, comments);
    }
}
