import os
import xml.etree.ElementTree as ET
from PIL import Image, ImageDraw, ImageFont

os.makedirs('docs/architecture', exist_ok=True)
os.makedirs('docs/images', exist_ok=True)

# ==============================================================================
# 1. TẠO TỆP SƠ ĐỒ DRAW.IO (XML ĐẦY ĐỦ CÁC TẦNG & GIAO DIỆN)
# ==============================================================================
drawio_content = """<?xml version="1.0" encoding="UTF-8"?>
<mxfile host="app.diagrams.net" modified="2026-09-10T00:15:00.000Z" agent="Mozilla/5.0" version="21.6.8" type="device">
  <diagram id="cnj56-layered-architecture" name="Sơ đồ Kiến trúc Hệ thống Phân tầng (CNJ56)">
    <mxGraphModel dx="1600" dy="1100" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="1654" pageHeight="1169" math="0" shadow="0">
      <root>
        <mxCell id="0" />
        <mxCell id="1" parent="0" />

        <!-- BANNER TIÊU ĐỀ HỆ THỐNG -->
        <mxCell id="banner" value="&lt;b style='font-size:18px;'&gt;SƠ ĐỒ KIẾN TRÚC HỆ THỐNG PHÂN TẦNG (LAYERED ARCHITECTURE)&lt;/b&gt;&lt;br/&gt;&lt;span style='font-size:13px;'&gt;Hệ Thống Quản Lý Thời Khóa Biểu &amp; Tài Nguyên Phòng Học - Đề Tài CNJ56 (Nhóm 11: Nguyễn Mạnh Quyết &amp; Hà Thái Bảo)&lt;/span&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#1E293B;strokeColor=#0F172A;fontColor=#FFFFFF;align=center;arcSize=6;" vertex="1" parent="1">
          <mxGeometry x="40" y="30" width="1570" height="60" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 1: PRESENTATION LAYER ==================== -->
        <mxCell id="grp_layer1" value="1. TẦNG TRÌNH DIỄN (PRESENTATION LAYER - JAVA SWING &amp; FLAT UI)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#EFF6FF;strokeColor=#3B82F6;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#1E40AF;" vertex="1" parent="1">
          <mxGeometry x="40" y="110" width="1150" height="130" as="geometry" />
        </mxCell>
        <mxCell id="l1_login" value="&lt;b&gt;LoginForm&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Xác thực SHA-256&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="15" y="40" width="140" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_main" value="&lt;b&gt;MainForm&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;CardLayout/Sidebar&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="170" y="40" width="145" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_dashboard" value="&lt;b&gt;DashboardPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;6 KPI &amp; Quick Actions&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="330" y="40" width="150" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_grid" value="&lt;b&gt;TimetableGridPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Lưới TKB Tuần/Zoom&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="495" y="40" width="150" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_schedule" value="&lt;b&gt;ThoiKhoaBieuPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Quản lý 690 lịch TKB&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="660" y="40" width="150" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_swap" value="&lt;b&gt;DeXuatDoiLichPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Quy trình duyệt 2 cấp&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;" vertex="1" parent="grp_layer1">
          <mxGeometry x="825" y="40" width="155" height="40" as="geometry" />
        </mxCell>
        <mxCell id="l1_audit" value="&lt;b&gt;AuditLogPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Nhật ký &amp; Export Log&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer1">
          <mxGeometry x="995" y="40" width="140" height="40" as="geometry" />
        </mxCell>

        <!-- Hàng 2 tầng 1: Danh mục & Phân trang -->
        <mxCell id="l1_categories" value="&lt;b&gt;Danh mục:&lt;/b&gt; PhongHocPanel (20 phòng) | GiangVienPanel (506 GV) | SinhVienPanel (2.000 SV) | MonHocPanel (50 môn) | LopHocPanel (40 lớp)" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#F1F5F9;strokeColor=#CBD5E1;fontColor=#334155;fontSize=11;" vertex="1" parent="grp_layer1">
          <mxGeometry x="15" y="85" width="700" height="35" as="geometry" />
        </mxCell>
        <mxCell id="l1_curriculum" value="&lt;b&gt;CurriculumPanel&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;CTĐT 4 Khóa - 130 TC&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#93C5FD;" vertex="1" parent="grp_layer1">
          <mxGeometry x="725" y="85" width="165" height="35" as="geometry" />
        </mxCell>
        <mxCell id="l1_pagination" value="&lt;b&gt;PaginationBar (20 mục)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Đồng bộ 10 Danh mục&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#E0E7FF;strokeColor=#6366F1;" vertex="1" parent="grp_layer1">
          <mxGeometry x="900" y="85" width="235" height="35" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 2: CONTROLLER LAYER ==================== -->
        <mxCell id="grp_layer2" value="2. TẦNG ĐIỀU HƯỚNG &amp; TIẾP NHẬN SỰ KIỆN (CONTROLLER LAYER)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#F5F3FF;strokeColor=#8B5CF6;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#6D28D9;" vertex="1" parent="1">
          <mxGeometry x="40" y="260" width="1150" height="90" as="geometry" />
        </mxCell>
        <mxCell id="l2_auth" value="&lt;b&gt;AuthController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Điều phối phiên &amp; RBAC&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#C4B5FD;" vertex="1" parent="grp_layer2">
          <mxGeometry x="15" y="40" width="160" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l2_sched" value="&lt;b&gt;ScheduleController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Xử lý truy vấn &amp; xếp lịch&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#C4B5FD;" vertex="1" parent="grp_layer2">
          <mxGeometry x="190" y="40" width="175" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l2_swap" value="&lt;b&gt;ScheduleSwapController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Điều phối xin &amp; duyệt đổi ca&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;" vertex="1" parent="grp_layer2">
          <mxGeometry x="380" y="40" width="185" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l2_audit" value="&lt;b&gt;AuditLogController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Lọc &amp; xuất file kiểm toán&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer2">
          <mxGeometry x="580" y="40" width="175" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l2_curr" value="&lt;b&gt;CurriculumController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Quản lý khung 130 tín chỉ&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#C4B5FD;" vertex="1" parent="grp_layer2">
          <mxGeometry x="770" y="40" width="180" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l2_stats" value="&lt;b&gt;StatsController&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Thống kê &amp; Quét phòng trống&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#C4B5FD;" vertex="1" parent="grp_layer2">
          <mxGeometry x="965" y="40" width="170" height="38" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 3: SERVICE LAYER ==================== -->
        <mxCell id="grp_layer3" value="3. TẦNG NGHIỆP VỤ (SERVICE LAYER - BUSINESS LOGIC &amp; SCHEDULING ENGINE)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#F0FDF4;strokeColor=#22C55E;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#15803D;" vertex="1" parent="1">
          <mxGeometry x="40" y="370" width="1150" height="110" as="geometry" />
        </mxCell>
        <mxCell id="l3_auth" value="&lt;b&gt;AuthService&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Phân quyền RBAC 6 vai trò&lt;br/&gt;Mật khẩu băm SHA-256&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#86EFAC;" vertex="1" parent="grp_layer3">
          <mxGeometry x="15" y="40" width="175" height="55" as="geometry" />
        </mxCell>
        <mxCell id="l3_engine" value="&lt;b&gt;XepLichService (Engine)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Kiểm tra xung đột 8 chiều&lt;br/&gt;Bảo đảm 0 trùng Phòng/Lớp/GV&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#16A34A;fontColor=#14532D;" vertex="1" parent="grp_layer3">
          <mxGeometry x="205" y="40" width="200" height="55" as="geometry" />
        </mxCell>
        <mxCell id="l3_workflow" value="&lt;b&gt;ApprovalWorkflowService&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Quy trình duyệt đổi lịch 2 cấp&lt;br/&gt;GV -&gt; Trưởng Khoa -&gt; P.Đào Tạo&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;fontColor=#78350F;" vertex="1" parent="grp_layer3">
          <mxGeometry x="420" y="40" width="215" height="55" as="geometry" />
        </mxCell>
        <mxCell id="l3_audit" value="&lt;b&gt;AuditLogService&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Ghi vết kiểm toán tự động&lt;br/&gt;Chống chối bỏ (Non-repudiation)&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer3">
          <mxGeometry x="650" y="40" width="190" height="55" as="geometry" />
        </mxCell>
        <mxCell id="l3_curr" value="&lt;b&gt;CurriculumService&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Chuẩn hóa CTĐT 4 năm&lt;br/&gt;42 môn - Đúng 130 tín chỉ&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#86EFAC;" vertex="1" parent="grp_layer3">
          <mxGeometry x="855" y="40" width="165" height="55" as="geometry" />
        </mxCell>
        <mxCell id="l3_stat" value="&lt;b&gt;ThongKeService&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Tỷ lệ lấp đầy phòng&lt;br/&gt;Tải giảng dạy 15.5 tiết&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#86EFAC;" vertex="1" parent="grp_layer3">
          <mxGeometry x="1030" y="40" width="105" height="55" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 4: DAO LAYER ==================== -->
        <mxCell id="grp_layer4" value="4. TẦNG TRUY CẬP DỮ LIỆU (DAO LAYER - 100% JDBC PREPAREDSTATEMENT)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#FFFBEB;strokeColor=#F59E0B;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#B45309;" vertex="1" parent="1">
          <mxGeometry x="40" y="500" width="1150" height="90" as="geometry" />
        </mxCell>
        <mxCell id="l4_tkb" value="&lt;b&gt;ThoiKhoaBieuDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Lọc 8 tiêu chí &amp; 690 lịch&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCD34D;" vertex="1" parent="grp_layer4">
          <mxGeometry x="15" y="40" width="165" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_phong" value="&lt;b&gt;PhongHocDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;20 phòng (LT &amp; PM)&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCD34D;" vertex="1" parent="grp_layer4">
          <mxGeometry x="190" y="40" width="145" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_gv" value="&lt;b&gt;GiangVienDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;506 giảng viên&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCD34D;" vertex="1" parent="grp_layer4">
          <mxGeometry x="345" y="40" width="140" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_sv" value="&lt;b&gt;SinhVienDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;2.000 sinh viên&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCD34D;" vertex="1" parent="grp_layer4">
          <mxGeometry x="495" y="40" width="140" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_mon" value="&lt;b&gt;MonHoc / LopDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;50 môn | 40 lớp&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCD34D;" vertex="1" parent="grp_layer4">
          <mxGeometry x="645" y="40" width="150" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_swap" value="&lt;b&gt;DeXuatDoiLichDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;CRUD đơn &amp; hoán đổi&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;" vertex="1" parent="grp_layer4">
          <mxGeometry x="805" y="40" width="165" height="38" as="geometry" />
        </mxCell>
        <mxCell id="l4_audit" value="&lt;b&gt;AuditLogDAO&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:10px;'&gt;Ghi log &amp; tìm kiếm log&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer4">
          <mxGeometry x="980" y="40" width="155" height="38" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 5: MODEL LAYER ==================== -->
        <mxCell id="grp_layer5" value="5. TẦNG THỰC THỂ (MODEL LAYER - PLAIN OLD JAVA OBJECTS - POJO)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#FEF2F2;strokeColor=#EF4444;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#B91C1C;" vertex="1" parent="1">
          <mxGeometry x="40" y="610" width="1150" height="85" as="geometry" />
        </mxCell>
        <mxCell id="l5_tkb" value="&lt;b&gt;ThoiKhoaBieu&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;mon, lop, gv, phong, tiet, tuan&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCA5A5;" vertex="1" parent="grp_layer5">
          <mxGeometry x="15" y="38" width="170" height="36" as="geometry" />
        </mxCell>
        <mxCell id="l5_actors" value="&lt;b&gt;SinhVien / GiangVien&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;ma_sv, ma_gv, ho_ten, khoa&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCA5A5;" vertex="1" parent="grp_layer5">
          <mxGeometry x="195" y="38" width="175" height="36" as="geometry" />
        </mxCell>
        <mxCell id="l5_cat" value="&lt;b&gt;PhongHoc / LopHoc / MonHoc&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;ma, ten, suc_chua, so_tin_chi&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCA5A5;" vertex="1" parent="grp_layer5">
          <mxGeometry x="380" y="38" width="210" height="36" as="geometry" />
        </mxCell>
        <mxCell id="l5_swap" value="&lt;b&gt;YeuCauDoiLich&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;ma_tkb, gv_de_xuat, trang_thai&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;" vertex="1" parent="grp_layer5">
          <mxGeometry x="600" y="38" width="180" height="36" as="geometry" />
        </mxCell>
        <mxCell id="l5_audit" value="&lt;b&gt;AuditLog&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;user_id, hanh_dong, ip, ngay_tao&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer5">
          <mxGeometry x="790" y="38" width="175" height="36" as="geometry" />
        </mxCell>
        <mxCell id="l5_user" value="&lt;b&gt;TaiKhoan&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:9px;'&gt;username, password_hash, role&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#FCA5A5;" vertex="1" parent="grp_layer5">
          <mxGeometry x="975" y="38" width="160" height="36" as="geometry" />
        </mxCell>

        <!-- ==================== TẦNG 6: DATABASE TIER ==================== -->
        <mxCell id="grp_layer6" value="6. CƠ SỞ DỮ LIỆU QUAN HỆ (DATABASE TIER - MYSQL 8.0 LOCALHOST:3306 - XAMPP)" style="swimlane;startSize=30;rounded=1;arcSize=6;fillColor=#F8FAFC;strokeColor=#64748B;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#334155;" vertex="1" parent="1">
          <mxGeometry x="40" y="715" width="1150" height="95" as="geometry" />
        </mxCell>
        <mxCell id="l6_tkb" value="&lt;b&gt;thoi_khoa_bieu&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;690 lịch chuẩn&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="15" y="38" width="130" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_sv" value="&lt;b&gt;sinh_vien&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;2.000 SV (K21-K24)&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="155" y="38" width="140" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_gv" value="&lt;b&gt;giang_vien&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;506 giảng viên&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="305" y="38" width="130" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_phong" value="&lt;b&gt;phong_hoc&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;20 phòng học&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="445" y="38" width="125" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_mon" value="&lt;b&gt;mon_hoc&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;50 môn học&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="580" y="38" width="120" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_lop" value="&lt;b&gt;lop_hoc&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;40 lớp học&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="710" y="38" width="120" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_curr" value="&lt;b&gt;chuong_trinh_dt&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;130 tín chỉ / khóa&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#FFFFFF;strokeColor=#94A3B8;" vertex="1" parent="grp_layer6">
          <mxGeometry x="840" y="38" width="140" height="48" as="geometry" />
        </mxCell>
        <mxCell id="l6_audit" value="&lt;b&gt;audit_log&lt;/b&gt;&lt;br/&gt;&lt;font color='#047857' style='font-size:10px;'&gt;Nhật ký bất biến&lt;/font&gt;" style="shape=cylinder3;whiteSpace=wrap;html=1;boundedLbl=1;backgroundOutline=1;size=10;fillColor=#DCFCE7;strokeColor=#22C55E;" vertex="1" parent="grp_layer6">
          <mxGeometry x="990" y="38" width="145" height="48" as="geometry" />
        </mxCell>

        <!-- ==================== CỘT PHẢI: CROSS-CUTTING CONCERNS ==================== -->
        <mxCell id="grp_cross" value="TRỤC AN NINH &amp; BỔ TRỢ HỆ THỐNG&#xa;(CROSS-CUTTING CONCERNS)" style="swimlane;startSize=40;rounded=1;arcSize=6;fillColor=#F8FAFC;strokeColor=#475569;strokeWidth=2;fontStyle=1;fontSize=13;fontColor=#1E293B;" vertex="1" parent="1">
          <mxGeometry x="1210" y="110" width="400" height="500" as="geometry" />
        </mxCell>
        <mxCell id="cc_interceptor" value="&lt;b&gt;AuditLogInterceptor&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Tự động chặn bắt sự kiện trọng yếu (Đăng nhập, CRUD TKB, Duyệt lịch, DB Tools) và kích hoạt ghi log tức thì.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="55" width="370" height="60" as="geometry" />
        </mxCell>
        <mxCell id="cc_dbconn" value="&lt;b&gt;DBConnection (Singleton Pool)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Quản lý kết nối JDBC MySQL Connector/J 8.3.0, try-with-resources, chống rò rỉ bộ nhớ (Zero Connection Leak).&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="125" width="370" height="60" as="geometry" />
        </mxCell>
        <mxCell id="cc_pwd" value="&lt;b&gt;PasswordUtil (SHA-256)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Băm mật khẩu chuẩn SHA-256 an toàn cao, xác minh mật khẩu chống tấn công từ điển &amp; Rainbow Table.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="195" width="370" height="60" as="geometry" />
        </mxCell>
        <mxCell id="cc_export" value="&lt;b&gt;ExportUtil (UTF-8 BOM)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Xuất báo cáo TKB, danh mục và nhật ký sang CSV/Excel có chèn BOM \\uFEFF hiển thị tiếng Việt hoàn hảo trên Excel.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="265" width="370" height="60" as="geometry" />
        </mxCell>
        <mxCell id="cc_val" value="&lt;b&gt;ValidationUtil&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Kiểm định Regex email, số điện thoại, miền giá trị tiết 1-12, tuần 1-20, sức chứa phòng &amp; sĩ số lớp sinh viên.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="335" width="370" height="60" as="geometry" />
        </mxCell>
        <mxCell id="cc_ui" value="&lt;b&gt;UIUtil (FlatButton &amp; TableFormatter)&lt;/b&gt;&lt;br/&gt;&lt;font color='#64748B' style='font-size:11px;'&gt;Custom Swing Component vẽ đồ họa phẳng vector khử răng cưa, căn giữa toàn bộ bảng, hỗ trợ Shift+Wheel zoom TKB.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FFFFFF;strokeColor=#94A3B8;align=left;spacingLeft=8;" vertex="1" parent="grp_cross">
          <mxGeometry x="15" y="405" width="370" height="65" as="geometry" />
        </mxCell>

        <!-- ==================== CỘT PHẢI DƯỚI: 2 LUỒNG XỬ LÝ ĐẶC THÙ ==================== -->
        <mxCell id="flow_swap" value="&lt;b style='color:#B45309;font-size:12px;'&gt;➔ LUỒNG 1: QUY TRÌNH DUYỆT ĐỔI LỊCH 2 CẤP&lt;/b&gt;&lt;br/&gt;&lt;font style='font-size:11px;color:#78350F;'&gt;&lt;b&gt;Giảng viên&lt;/b&gt; xin đổi lịch ➔ &lt;b&gt;Trưởng Khoa&lt;/b&gt; duyệt sơ bộ (Cấp 1) ➔ &lt;b&gt;Phòng Đào tạo&lt;/b&gt; phê duyệt chốt cuối (Cấp 2) ➔ Engine kiểm tra xung đột ➔ Cập nhật thoi_khoa_bieu.&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#FEF3C7;strokeColor=#F59E0B;strokeWidth=1.5;align=left;spacingLeft=10;" vertex="1" parent="1">
          <mxGeometry x="1210" y="625" width="400" height="80" as="geometry" />
        </mxCell>
        <mxCell id="flow_audit" value="&lt;b style='color:#15803D;font-size:12px;'&gt;➔ LUỒNG 2: GHI VẾT NHẬT KÝ KIỂM TOÁN (AUDIT TRAIL)&lt;/b&gt;&lt;br/&gt;&lt;font style='font-size:11px;color:#14532D;'&gt;Mọi hành động quan trọng (Login, Xếp lịch, Duyệt lịch, DB Tools) ➔ Tự động bắn tín hiệu qua &lt;b&gt;AuditLogService&lt;/b&gt; ➔ &lt;b&gt;AuditLogDAO&lt;/b&gt; ➔ Lưu vết vĩnh viễn vào bảng &lt;b&gt;audit_log&lt;/b&gt; (Chống chối bỏ).&lt;/font&gt;" style="rounded=1;whiteSpace=wrap;html=1;fillColor=#DCFCE7;strokeColor=#22C55E;strokeWidth=1.5;align=left;spacingLeft=10;" vertex="1" parent="1">
          <mxGeometry x="1210" y="720" width="400" height="90" as="geometry" />
        </mxCell>

        <!-- CÁC MŨI TÊN KẾT NỐI GIỮA CÁC TẦNG (DATA FLOW ARROWS) -->
        <mxCell id="arrow_1_2" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#3B82F6;strokeWidth=2;endArrow=classic;" edge="1" parent="1" source="grp_layer1" target="grp_layer2">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>
        <mxCell id="arrow_2_3" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#8B5CF6;strokeWidth=2;endArrow=classic;" edge="1" parent="1" source="grp_layer2" target="grp_layer3">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>
        <mxCell id="arrow_3_4" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#22C55E;strokeWidth=2;endArrow=classic;" edge="1" parent="1" source="grp_layer3" target="grp_layer4">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>
        <mxCell id="arrow_4_5" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#F59E0B;strokeWidth=2;endArrow=classic;" edge="1" parent="1" source="grp_layer4" target="grp_layer5">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>
        <mxCell id="arrow_5_6" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#EF4444;strokeWidth=2;endArrow=classic;" edge="1" parent="1" source="grp_layer5" target="grp_layer6">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>
        <mxCell id="arrow_cross" value="" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#475569;strokeWidth=2;dashed=1;endArrow=classic;startArrow=classic;" edge="1" parent="1" source="grp_layer3" target="grp_cross">
          <mxGeometry relative="1" as="geometry" />
        </mxCell>

      </root>
    </mxGraphModel>
  </diagram>
</mxfile>
"""

drawio_path = 'docs/architecture/kientruc_hethong_phantang.drawio'
with open(drawio_path, 'w', encoding='utf-8') as f:
    f.write(drawio_content.strip())
print(f"✅ Đã tạo tệp Draw.io thành công: {drawio_path}")

# ==============================================================================
# 2. RENDER RA ẢNH PNG CỰC NÉT (HIGH-RES 2400x1600) MÔ TẢ ĐÚNG CHUẨN SƠ ĐỒ DRAW.IO
# ==============================================================================
img_w, img_h = 2400, 1550
img = Image.new('RGB', (img_w, img_h), color='#F8FAFC')
draw = ImageDraw.Draw(img)

def get_font(size, bold=False):
    font_names = [
        "C:\\Windows\\Fonts\\segoeuib.ttf" if bold else "C:\\Windows\\Fonts\\segoeui.ttf",
        "C:\\Windows\\Fonts\\arialbd.ttf" if bold else "C:\\Windows\\Fonts\\arial.ttf"
    ]
    for fn in font_names:
        if os.path.exists(fn):
            return ImageFont.truetype(fn, size)
    return ImageFont.load_default()

font_banner_title = get_font(28, bold=True)
font_banner_sub = get_font(18, bold=False)
font_layer_title = get_font(20, bold=True)
font_box_title = get_font(16, bold=True)
font_box_desc = get_font(13, bold=False)
font_box_small = get_font(12, bold=False)

# Draw Banner
draw.rounded_rectangle([40, 30, 2360, 115], radius=10, fill='#0F172A', outline='#1E293B', width=2)
draw.text((1200, 52), "SƠ ĐỒ KIẾN TRÚC HỆ THỐNG PHÂN TẦNG (LAYERED ARCHITECTURE)", fill='#FFFFFF', font=font_banner_title, anchor='mm')
draw.text((1200, 88), "Hệ Thống Quản Lý Thời Khóa Biểu & Tài Nguyên Phòng Học - Đề Tài CNJ56 (Nhóm 11: Nguyễn Mạnh Quyết & Hà Thái Bảo) [Draw.io]", fill='#94A3B8', font=font_banner_sub, anchor='mm')

# Helper function to draw a layer box
def draw_layer_container(x, y, w, h, title, bg_col, border_col, title_col):
    draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_col, outline=border_col, width=2)
    draw.rounded_rectangle([x, y, x + w, y + 42], radius=10, fill=border_col)
    draw.rectangle([x, y + 25, x + w, y + 42], fill=border_col)
    draw.text((x + 18, y + 21), title, fill='#FFFFFF', font=font_layer_title, anchor='lm')

def draw_card(x, y, w, h, title, desc, bg_col='#FFFFFF', border_col='#CBD5E1', title_col='#0F172A', desc_col='#64748B'):
    draw.rounded_rectangle([x, y, x + w, y + h], radius=6, fill=bg_col, outline=border_col, width=1)
    draw.text((x + 12, y + 16), title, fill=title_col, font=font_box_title, anchor='lm')
    lines = desc.split('\n')
    line_y = y + 36
    for line in lines:
        draw.text((x + 12, line_y), line, fill=desc_col, font=font_box_desc, anchor='lm')
        line_y += 18

left_w = 1680

# 1. Tầng Trình Diễn
draw_layer_container(40, 135, left_w, 205, "1. TẦNG TRÌNH DIỄN (PRESENTATION LAYER - JAVA SWING & FLAT UI)", '#EFF6FF', '#2563EB', '#FFFFFF')
cards_l1 = [
    ("LoginForm", "Xác thực SHA-256\nPhân quyền RBAC", 40 + 20, 190, 215, 65),
    ("MainForm", "CardLayout / Sidebar\nFull màn hình tự động", 285, 190, 220, 65),
    ("DashboardPanel", "6 Thẻ KPI & Quick Actions\nTiến độ đào tạo K21-K24", 535, 190, 250, 65),
    ("TimetableGridPanel", "Ma trận 12 tiết x 7 thứ\nShift+Wheel Zoom mượt", 815, 190, 240, 65),
    ("ThoiKhoaBieuPanel", "Quản lý 690 lịch TKB\nBộ lọc đa tiêu chí", 1085, 190, 205, 65),
    ("DeXuatDoiLichPanel", "Quy trình duyệt 2 cấp\nKhoa & P. Đào tạo", 1320, 190, 215, 65, '#FEF3C7', '#F59E0B', '#B45309', '#78350F'),
    ("AuditLogPanel", "Nhật ký hệ thống\nXuất Excel UTF-8", 1565, 190, 135, 65, '#DCFCE7', '#22C55E', '#15803D', '#14532D'),
]
for c in cards_l1:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# Danh mục phụ tầng 1
draw_card(60, 270, 950, 52, "Quản Lý Danh Mục Nghiệp Vụ Cơ Sở", "PhongHocPanel (20 phòng) • GiangVienPanel (506 GV) • SinhVienPanel (2.000 SV) • MonHocPanel (50 môn) • LopHocPanel (40 lớp)", '#FFFFFF', '#93C5FD', '#1E40AF', '#475569')
draw_card(1030, 270, 310, 52, "CurriculumPanel", "Khung CTĐT 4 Khóa K21-K24 • Đúng 130 Tín Chỉ", '#FFFFFF', '#93C5FD', '#1E40AF', '#475569')
draw_card(1360, 270, 340, 52, "PaginationBar (20 mục/trang)", "Component dùng chung đồng bộ 100% tất cả danh mục", '#E0E7FF', '#6366F1', '#4338CA', '#3730A3')

# 2. Tầng Điều Hướng
draw_layer_container(40, 360, left_w, 150, "2. TẦNG ĐIỀU HƯỚNG & TIẾP NHẬN SỰ KIỆN (CONTROLLER LAYER - DISPATCHER)", '#F5F3FF', '#7C3AED', '#FFFFFF')
cards_l2 = [
    ("AuthController", "Điều phối phiên đăng nhập\nỦy quyền phân quyền", 60, 415, 250, 75),
    ("ScheduleController", "Xử lý lọc & điều phối\nXếp thời khóa biểu", 330, 415, 250, 75),
    ("ScheduleSwapController", "Điều phối luồng xin đổi ca\nDuyệt cấp 1 & cấp 2", 600, 415, 270, 75, '#FEF3C7', '#F59E0B', '#B45309', '#78350F'),
    ("AuditLogController", "Điều phối truy vấn nhật ký\nXuất báo cáo kiểm toán", 890, 415, 260, 75, '#DCFCE7', '#22C55E', '#15803D', '#14532D'),
    ("CurriculumController", "Điều phối dữ liệu khung\nChương trình 130 TC", 1170, 415, 250, 75),
    ("StatsController", "Điều phối báo cáo tải GV\n& Quét phòng trống", 1440, 415, 260, 75),
]
for c in cards_l2:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# 3. Tầng Nghiệp Vụ
draw_layer_container(40, 530, left_w, 160, "3. TẦNG NGHIỆP VỤ (SERVICE LAYER - BUSINESS LOGIC & SCHEDULING ENGINE)", '#F0FDF4', '#16A34A', '#FFFFFF')
cards_l3 = [
    ("AuthService", "Xác thực Session người dùng\nPhân quyền RBAC 6 vai trò\nMật khẩu băm SHA-256", 60, 585, 260, 85),
    ("XepLichService (Engine)", "Thuật toán kiểm tra xung đột 8 chiều\nNgăn chặn trùng Phòng, Lớp, Giảng viên\nCam kết 0 xung đột trên 690 lịch học", 340, 585, 330, 85, '#DCFCE7', '#16A34A', '#14532D', '#166534'),
    ("ApprovalWorkflowService", "Xử lý quy trình duyệt đổi lịch 2 cấp\nGV đề xuất -> Cán bộ Khoa -> P. Đào tạo\nHoán đổi dữ liệu lịch an toàn", 690, 585, 330, 85, '#FEF3C7', '#F59E0B', '#78350F', '#92400E'),
    ("AuditLogService", "Ghi vết kiểm toán tự động\nBảo đảm tính bất biến dữ liệu\nChống chối bỏ (Non-repudiation)", 1040, 585, 290, 85, '#DCFCE7', '#22C55E', '#15803D', '#14532D'),
    ("CurriculumService & ThongKe", "Chuẩn hóa CTĐT 130 TC cả 4 khóa\nTỷ lệ phòng & Tải GV 15.5 tiết/tuần", 1350, 585, 350, 85),
]
for c in cards_l3:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# 4. Tầng DAO
draw_layer_container(40, 710, left_w, 150, "4. TẦNG TRUY CẬP DỮ LIỆU (DAO LAYER - 100% JDBC PREPAREDSTATEMENT)", '#FFFBEB', '#D97706', '#FFFFFF')
cards_l4 = [
    ("ThoiKhoaBieuDAO", "Lọc đa tiêu chí & xung đột\nQuản lý 690 ca học CSDL", 60, 765, 250, 75),
    ("PhongHocDAO & GiangVienDAO", "Quản lý 20 phòng học (LT/PM)\nQuản lý 506 giảng viên", 330, 765, 260, 75),
    ("SinhVienDAO & LopHocDAO", "Quản lý 2.000 SV (K21-K24)\nQuản lý 40 lớp sinh viên", 610, 765, 260, 75),
    ("MonHocDAO & CurriculumDAO", "Quản lý 50 môn học\nKhung 42 môn - 130 TC/khóa", 890, 765, 260, 75),
    ("DeXuatDoiLichDAO", "CRUD yêu cầu đổi lịch\nThực thi cập nhật phê duyệt", 1170, 765, 260, 75, '#FEF3C7', '#F59E0B', '#B45309', '#78350F'),
    ("AuditLogDAO & TaiKhoanDAO", "Lưu vết nhat_ky_he_thong\nQuản lý tài khoản & phân quyền", 1450, 765, 250, 75, '#DCFCE7', '#22C55E', '#15803D', '#14532D'),
]
for c in cards_l4:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# 5. Tầng Thực Thể (Model Layer)
draw_layer_container(40, 880, left_w, 140, "5. TẦNG THỰC THỂ (MODEL LAYER - PLAIN OLD JAVA OBJECTS - POJO)", '#FEF2F2', '#DC2626', '#FFFFFF')
cards_l5 = [
    ("ThoiKhoaBieu", "id, ma_mon, ma_lop, ma_gv, ma_phong, thu, tiet_bd, so_tiet, tuan_bd, tuan_kt", 60, 935, 390, 65),
    ("GiangVien & SinhVien", "ma_gv, ho_ten, email, sdt, khoa | ma_sv, ho_ten, lop, khoa_hoc", 470, 935, 370, 65),
    ("PhongHoc & LopHoc & MonHoc", "ma_phong, suc_chua, loai_phong | ma_lop, si_so | ma_mon, so_tin_chi", 860, 935, 380, 65),
    ("YeuCauDoiLich & AuditLog & User", "ma_tkb, gv_de_xuat, trang_thai | user_id, hanh_dong, ip | username, role", 1260, 935, 440, 65, '#FEF3C7', '#F59E0B', '#B45309', '#78350F'),
]
for c in cards_l5:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# 6. Cơ Sở Dữ Liệu Quan Hệ (Database Tier)
draw_layer_container(40, 1040, left_w, 155, "6. CƠ SỞ DỮ LIỆU QUAN HỆ (DATABASE TIER - MYSQL 8.0 LOCALHOST:3306 - XAMPP)", '#F8FAFC', '#475569', '#FFFFFF')
cards_l6 = [
    ("thoi_khoa_bieu", "690 Lịch TKB chính thức\nTuần 1-7 & Tuần 10-16", 60, 1095, 215, 80),
    ("sinh_vien (2.000)", "4 khóa K21 - K24\n500 SV/khóa (50 SV/lớp)", 295, 1095, 215, 80),
    ("giang_vien (506)", "Cơ hữu & thỉnh giảng\n217 GV có lịch dạy", 530, 1095, 215, 80),
    ("phong_hoc (20)", "10 LT (LT201..A201)\n10 Thực hành (PM101..HT)", 765, 1095, 215, 80),
    ("mon_hoc (50)", "Khung 42 môn CTĐT\n+ 8 môn chuyên đề tự chọn", 1000, 1095, 215, 80),
    ("chuong_trinh_dao_tao", "Đúng 130 tín chỉ\n8 học kỳ cho cả 4 khóa", 1235, 1095, 215, 80),
    ("audit_log & don_doi_lich", "Bảng nhật ký kiểm toán\n& Quản lý duyệt đổi lịch 2 cấp", 1470, 1095, 230, 80, '#DCFCE7', '#22C55E', '#15803D', '#14532D'),
]
for c in cards_l6:
    draw_card(c[2], c[3], c[4], c[5], c[0], c[1], *c[6:])

# ==================== CỘT PHẢI: TRỤC AN NINH & BỔ TRỢ ====================
cross_x = 1740
cross_w = 620

draw_layer_container(cross_x, 135, cross_w, 690, "TRỤC AN NINH & BỔ TRỢ (CROSS-CUTTING CONCERNS)", '#F8FAFC', '#1E293B', '#FFFFFF')

def draw_wrapped_card(x, y, w, h, title, desc, bg_col='#FFFFFF', border_col='#CBD5E1', title_col='#0F172A', desc_col='#64748B'):
    draw.rounded_rectangle([x, y, x + w, y + h], radius=6, fill=bg_col, outline=border_col, width=1)
    draw.text((x + 12, y + 15), title, fill=title_col, font=font_box_title, anchor='lm')
    
    # Word wrap description
    words = desc.split(' ')
    lines = []
    curr_line = []
    for word in words:
        curr_line.append(word)
        test_str = ' '.join(curr_line)
        bbox = draw.textbbox((0, 0), test_str, font=font_box_desc)
        if (bbox[2] - bbox[0]) > (w - 24):
            curr_line.pop()
            if curr_line:
                lines.append(' '.join(curr_line))
            curr_line = [word]
    if curr_line:
        lines.append(' '.join(curr_line))
    
    line_y = y + 36
    for line in lines:
        draw.text((x + 12, line_y), line, fill=desc_col, font=font_box_desc, anchor='lm')
        line_y += 18

cross_items = [
    ("AuditLogInterceptor", "Tự động chặn bắt sự kiện nghiệp vụ trọng yếu (Đăng nhập, CRUD TKB, Duyệt lịch, DB Tools) và kích hoạt ghi nhật ký tức thời."),
    ("DBConnection (Singleton Pool)", "Quản lý kết nối JDBC MySQL Connector/J 8.3.0, áp dụng try-with-resources đóng mở chuẩn xác, chống rò rỉ kết nối (Zero Connection Leak)."),
    ("PasswordUtil (SHA-256 Hash)", "Mã hóa mật khẩu chuẩn SHA-256 an toàn cao, cơ chế xác minh mật khẩu chống tấn công từ điển & Rainbow Table."),
    ("ExportUtil (Excel/CSV UTF-8 BOM)", "Xuất báo cáo TKB, danh mục và lịch sử kiểm toán sang file CSV/Excel tự động chèn ký tự BOM \\uFEFF, chống lỗi hiển thị dấu tiếng Việt."),
    ("ValidationUtil", "Kiểm định định dạng Regex email, số điện thoại, miền giá trị tiết 1-12, tuần 1-20, kiểm tra sức chứa phòng tương thích với sĩ số lớp học."),
    ("UIUtil (Flat UI & TableFormatter)", "Tùy biến bộ nút bấm phẳng FlatButton tự vẽ khử răng cưa vector, căn giữa toàn bộ bảng JTable, hỗ trợ Shift+Wheel zoom TKB tuần."),
]

y_cross = 195
for item in cross_items:
    draw_wrapped_card(cross_x + 20, y_cross, cross_w - 40, 72, item[0], item[1], '#FFFFFF', '#CBD5E1', '#0F172A', '#475569')
    y_cross += 84

# Hai luồng xử lý nổi bật
draw.rounded_rectangle([cross_x, 840, cross_x + cross_w, 1010], radius=8, fill='#FEF3C7', outline='#F59E0B', width=2)
draw.text((cross_x + 20, 860), "➔ LUỒNG 1: QUY TRÌNH PHÊ DUYỆT ĐỔI LỊCH 2 CẤP", fill='#92400E', font=font_box_title, anchor='lm')
desc_flow1 = "1. Giảng viên gửi đơn xin đổi ca dạy qua DeXuatDoiLichPanel\n2. Cán bộ Trưởng Khoa thẩm định sơ bộ chuyên môn (Cấp 1)\n3. Phòng Đào tạo phê duyệt chốt cuối & Engine kiểm tra xung đột (Cấp 2)\n4. CSDL tự động hoán đổi ca học trong thoi_khoa_bieu."
draw.text((cross_x + 20, 930), desc_flow1, fill='#78350F', font=font_box_desc, anchor='lm')

draw.rounded_rectangle([cross_x, 1030, cross_x + cross_w, 1195], radius=8, fill='#DCFCE7', outline='#22C55E', width=2)
draw.text((cross_x + 20, 1050), "➔ LUỒNG 2: GHI VẾT NHẬT KÝ KIỂM TOÁN (AUDIT TRAIL)", fill='#14532D', font=font_box_title, anchor='lm')
desc_flow2 = "1. Mọi hành động trọng yếu tại UI/Service kích hoạt AuditLogInterceptor\n2. AuditLogService đóng gói dữ liệu vết (User, Thao tác, IP, Timestamp)\n3. AuditLogDAO thực thi lưu bản ghi vào bảng audit_log trong MySQL\n4. Dữ liệu bất biến (chỉ INSERT & SELECT), chống chối bỏ trách nhiệm."
draw.text((cross_x + 20, 1120), desc_flow2, fill='#166534', font=font_box_desc, anchor='lm')

# ==================== MŨI TÊN LIÊN KẾT GIỮA CÁC TẦNG ====================
def draw_down_arrow(x, y1, y2, color='#3B82F6'):
    draw.line([x, y1, x, y2], fill=color, width=4)
    draw.polygon([(x - 8, y2 - 12), (x + 8, y2 - 12), (x, y2 + 2)], fill=color)

arrow_x = 880
draw_down_arrow(arrow_x, 340, 360, '#2563EB')
draw_down_arrow(arrow_x, 510, 530, '#7C3AED')
draw_down_arrow(arrow_x, 690, 710, '#16A34A')
draw_down_arrow(arrow_x, 860, 880, '#D97706')
draw_down_arrow(arrow_x, 1020, 1040, '#DC2626')

# Chú thích Draw.io dưới đáy ảnh
draw.text((1200, 1225), "Sơ đồ kiến trúc được thiết kế trực quan bằng công cụ Draw.io (diagrams.net) • File nguồn: docs/architecture/kientruc_hethong_phantang.drawio", fill='#64748B', font=font_box_desc, anchor='mm')

# Lưu ảnh ra các thư mục
img_path_moi = 'docs/images/hinh_2_2_kientruc_hethong_moi.png'
img_path_drawio = 'docs/images/hinh_2_2_kientruc_hethong_drawio.png'
img.save(img_path_moi, 'PNG', quality=95)
img.save(img_path_drawio, 'PNG', quality=95)
print(f"✅ Đã render ảnh kiến trúc chất lượng cao Draw.io: {img_path_moi}")
