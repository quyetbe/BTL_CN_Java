package service;

import dao.AuditLogDAO;
import model.AuditLog;

import java.util.List;

public class AuditService {

    private final AuditLogDAO auditDAO;

    public AuditService() {
        this.auditDAO = new AuditLogDAO();
    }

    public void log(Integer userId, String username, String action, String entityName, Integer entityId, String oldVal, String newVal) {
        auditDAO.log(userId, username, action, entityName, entityId, oldVal, newVal, "127.0.0.1");
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditDAO.getRecentLogs(limit);
    }
}
