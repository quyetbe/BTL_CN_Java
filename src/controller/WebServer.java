package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import connection.AppConfig;
import model.ChuongTrinhDaoTao;
import model.ThoiKhoaBieu;
import model.YeuCauDoiLich;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Embedded Lightweight Web Server chạy trực tiếp từ mã nguồn Java trên localhost.
 * Phục vụ REST APIs và giao diện Web HTML5 giám sát hệ thống.
 */
public class WebServer {

    private static HttpServer server;
    private final TimetableController timetableController;
    private final RescheduleController rescheduleController;
    private final CurriculumController curriculumController;

    public WebServer() {
        this.timetableController = new TimetableController();
        this.rescheduleController = new RescheduleController();
        this.curriculumController = new CurriculumController();
    }

    public void start() {
        int port = AppConfig.getWebPort();
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // 1. API: Danh sách thời khóa biểu
            server.createContext("/api/timetables", exchange -> {
                setCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<ThoiKhoaBieu> list = timetableController.getAll();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    ThoiKhoaBieu t = list.get(i);
                    json.append(String.format("{\"id\":%d,\"mon\":\"%s\",\"lop\":\"%s\",\"gv\":\"%s\",\"phong\":\"%s\",\"thu\":%d,\"tiet\":%d,\"soTiet\":%d}",
                            t.getId(), escape(t.getTenMon()), escape(t.getTenLop()), escape(t.getHoTenGv()), escape(t.getMaPhong()),
                            t.getThuTrongTuan(), t.getTietBatDau(), t.getSoTiet()));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            });

            // 2. API: Yêu cầu đổi lịch
            server.createContext("/api/reschedule", exchange -> {
                setCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<YeuCauDoiLich> list = rescheduleController.getRequests("ALL");
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    YeuCauDoiLich y = list.get(i);
                    json.append(String.format("{\"id\":%d,\"mon\":\"%s\",\"gv\":\"%s\",\"phongMoi\":\"%s\",\"thuMoi\":%d,\"tietMoi\":%d,\"trangThai\":\"%s\",\"capDuyet\":%d}",
                            y.getId(), escape(y.getTenMon()), escape(y.getHoTenGv()), escape(y.getMaPhongMoi()), y.getThuMoi(), y.getTietBatDauMoi(), y.getTrangThai(), y.getCapPheDuyet()));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            });

            // 3. API: Khung CTĐT 4 năm
            server.createContext("/api/curriculum", exchange -> {
                setCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                List<ChuongTrinhDaoTao> list = curriculumController.getAll();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    ChuongTrinhDaoTao c = list.get(i);
                    json.append(String.format("{\"id\":%d,\"hocKy\":%d,\"maMon\":\"%s\",\"tenMon\":\"%s\",\"tinChi\":%d,\"loai\":\"%s\"}",
                            c.getId(), c.getHocKy(), escape(c.getMaMon()), escape(c.getTenMon()), c.getSoTinChi(), c.getLoaiMon()));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            });

            // 4. Web Dashboard HTML
            server.createContext("/", exchange -> {
                String html = "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'><title>Embedded Java Web Server</title>"
                        + "<style>body{font-family:Segoe UI,sans-serif;padding:30px;background:#f8fafc;color:#0f172a}"
                        + ".card{background:#fff;border:1px solid #e2e8f0;padding:20px;border-radius:8px;max-width:800px;margin:0 auto;box-shadow:0 4px 6px -1px rgb(0 0 0/0.1)}"
                        + "h1{color:#2563eb}a{color:#2563eb;text-decoration:none;font-weight:bold}"
                        + "</style></head><body><div class='card'>"
                        + "<h1>🎓 Hệ Thống Quản Lý Đào Tạo & Thời Khóa Biểu (Java Embedded Web Server)</h1>"
                        + "<p>Web Server tích hợp sẵn trong mã nguồn Java đang chạy trên <strong>http://localhost:" + port + "</strong></p>"
                        + "<h3>📌 Danh sách REST APIs khả dụng:</h3>"
                        + "<ul>"
                        + "<li><a href='/api/timetables' target='_blank'>/api/timetables</a> - Danh sách Thời khóa biểu</li>"
                        + "<li><a href='/api/reschedule' target='_blank'>/api/reschedule</a> - Yêu cầu đổi lịch (Workflow 2 cấp)</li>"
                        + "<li><a href='/api/curriculum' target='_blank'>/api/curriculum</a> - Khung CTĐT 4 năm (8 học kỳ)</li>"
                        + "</ul>"
                        + "<hr><p style='color:#64748b;font-size:13px;'>Tương thích hoàn toàn với NetBeans IDE, Java Swing và CSDL MySQL.</p>"
                        + "</div></body></html>";
                byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("🚀 Java Embedded Web Server đang chạy tại: http://localhost:" + port);
        } catch (IOException e) {
            System.err.println("Không thể khởi động Web Server trên cổng " + port + ": " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public static void main(String[] args) {
        WebServer ws = new WebServer();
        ws.start();
        System.out.println("Java Web Server đang hoạt động trên http://localhost:" + AppConfig.getWebPort());
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}
