package controller;

import model.AuditLog;
import service.AuditService;

import java.util.List;

/**
 * Controller truy xuất lịch sử nhật ký hệ thống.
 */
public class AuditController {

    private final AuditService auditService;

    public AuditController() {
        this.auditService = new AuditService();
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditService.getRecentLogs(limit);
    }
}
