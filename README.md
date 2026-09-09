# ỨNG DỤNG DESKTOP QUẢN LÝ THỜI KHÓA BIỂU VÀ TÀI NGUYÊN PHÒNG HỌC
### BÁO CÁO ĐỒ ÁN MÔN HỌC: CÔNG NGHỆ JAVA - MÃ ĐỀ TÀI: CNJ56

<p align="center">
  <img src="https://img.shields.io/badge/Java-1.8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Swing-Flat_UI-007396?style=for-the-badge&logo=java&logoColor=white" alt="Swing" />
  <img src="https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/IDE-NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white" alt="NetBeans" />
  <img src="https://img.shields.io/badge/Status-Completed_(100%25)-success?style=for-the-badge" alt="Status" />
</p>

---

## 👥 THÀNH VIÊN THỰC HIỆN ĐỀ TÀI

| STT | Họ và Tên | Vai trò trong dự án | Tỷ lệ đóng góp | Trách nhiệm chính |
|:---:|:---|:---:|:---:|:---|
| **1** | **Nguyễn Mạnh Quyết** | **Trưởng nhóm** | **50%** | • Thiết kế cơ sở dữ liệu quan hệ MySQL (3NF).<br>• Xây dựng thuật toán kiểm tra xung đột thời khóa biểu 6 chiều.<br>• Phân quyền người dùng đa cấp RBAC (6 vai trò).<br>• Xây dựng module Chương trình đào tạo chuẩn 130 tín chỉ 4 năm.<br>• Thiết kế sơ đồ Draw.io (BFD & Kiến trúc phân tầng) và viết báo cáo đồ án. |
| **2** | **Hà Thái Bảo** | **Thành viên** | **50%** | • Phát triển giao diện người dùng Java Swing Flat UI chuẩn hiện đại.<br>• Xây dựng lưới Thời khóa biểu tương tác đa chiều (Ma trận & Khối).<br>• Phát triển quy trình đề xuất và phê duyệt đổi lịch 2 cấp độ.<br>• Xây dựng thanh điều hướng phân trang dữ liệu chuẩn hóa (`PaginationBar`).<br>• Triển khai module Nhật ký kiểm toán an ninh (`AuditLog`) & Thống kê KPI. |

---

## 🌟 TÍNH NĂNG NỔI BẬT

### 1. ⚡ Thuật toán Xếp lịch & Ngăn ngừa Xung đột Tức thì (Real-time Conflict Detection)
- Tự động phát hiện và cảnh báo 6 chiều vi phạm khi thêm/sửa lịch:
  1. **Trùng phòng học:** Hai lớp cùng học một phòng vào cùng thứ, tiết và tuần.
  2. **Trùng giảng viên:** Giảng viên bị phân công dạy hai lớp cùng thời điểm.
  3. **Trùng lịch lớp:** Một lớp bị xếp học 2 môn cùng khung giờ.
  4. **Tương thích loại phòng:** Môn thực hành bắt buộc phải xếp vào phòng máy/phòng lab; môn lý thuyết xếp vào giảng đường/phòng thường.
  5. **Cảnh báo sức chứa:** Tự động đối soát sĩ số lớp so với số chỗ ngồi của phòng học để tránh quá tải.
  6. **Khóa phòng bảo trì:** Ngăn chặn tuyệt đối việc xếp lịch vào các phòng đang trong trạng thái sửa chữa/bảo trì.

### 2. 📅 Lưới Thời Khóa Biểu Đa Chiều (Multidimensional Timetable Matrix)
- Trực quan hóa toàn bộ lịch học trong tuần (Thứ 2 – Chủ Nhật $\times$ 12 Tiết học).
- Đa chế độ hiển thị: Xem theo **Phòng học**, theo **Giảng viên**, theo **Lớp học** hoặc theo **Khóa sinh viên**.
- Tích hợp 2 chế độ hiển thị linh hoạt: **Dạng lưới ma trận (Matrix View)** và **Dạng khối trực quan (Block View)**.
- Hỗ trợ đổi lịch học bằng giao diện kéo-thả hoặc nhấp đúp để tra cứu chi tiết thông tin lớp/giảng viên/phòng.

### 3. 🔄 Quy Trình Đổi Lịch Học 2 Cấp Độ (2-Tier Reschedule Workflow)
- **Cấp 1 - Trưởng Khoa / Bộ Môn:** Giảng viên gửi đề xuất đổi lịch $\to$ Trưởng Khoa thẩm định lý do và tính khả thi bộ môn.
- **Cấp 2 - Ban Giám Hiệu / Phòng Đào Tạo:** Sau khi Khoa duyệt, hệ thống chuyển tiếp lên Đào tạo kiểm tra xung đột phòng và ca học trên toàn trường trước khi ra quyết định phê duyệt cuối cùng.
- Cập nhật lịch học tự động ngay khi đơn được phê duyệt; lưu trữ đầy đủ biên bản và lịch sử duyệt.

### 4. 🎓 Quản Lý Sinh Viên & Chương Trình Đào Tạo 130 Tín Chỉ (Curriculum Management)
- Thiết kế chuẩn khung chương trình đào tạo đại học chính quy 4 năm (8 học kỳ) gồm **42 môn học**, tổng cộng **130 tín chỉ**.
- Cấu hình sẵn cho tất cả các khóa sinh viên: **K21, K22, K23, K24, K25**.
- Quản lý danh mục sinh viên, lớp sinh hoạt, tiến độ tích lũy tín chỉ và thống kê phân bổ theo niên khóa.

### 5. 🛡️ Nhật Ký Kiểm Toán Hoạt Động (Security Audit Logging)
- Giám sát 100% mọi thao tác nhạy cảm: Đăng nhập, thêm/sửa/xóa TKB, đổi phòng, phê duyệt đổi lịch, thay đổi thông tin người dùng.
- Ghi nhận đầy đủ: Tên đăng nhập, vai trò, hành động thực hiện, đối tượng tác động, giá trị cũ $\to$ giá trị mới, địa chỉ IP và thời gian chính xác tới từng giây.

### 6. 📊 Dashboard KPI & Thống Kê Phân Tích Đa Chiều
- Dashboard trực quan với 6 thẻ KPI tổng quan: Tổng số phòng, Giảng viên, Sinh viên, Môn học, Tỷ lệ lấp đầy phòng học và Yêu cầu chờ xử lý.
- Đo lường công suất sử dụng phòng học theo tuần/học kỳ; phân bổ giờ dạy của từng giảng viên; thống kê sinh viên theo khoa/lớp.
- Xuất báo cáo ra định dạng file Excel / CSV hỗ trợ chuẩn mã hóa Tiếng Việt (UTF-8 BOM).

### 7. 📄 Thanh Điều Hướng Phân Trang Chuẩn Hóa (PaginationBar)
- Tích hợp thanh phân trang chuyên nghiệp cho toàn bộ các bảng danh mục dữ liệu lớn.
- Hỗ trợ nút: *Đầu trang*, *Trước*, *Các trang số*, *Sau*, *Cuối trang*, hiển thị rõ nét chỉ số trang hiện tại / tổng số trang và tổng số bản ghi.

---

## 🔐 HỆ THỐNG TÀI KHOẢN ĐĂNG NHẬP & PHÂN QUYỀN (RBAC)

Hệ thống thiết lập sẵn 6 nhóm tài khoản đại diện cho 6 cấp phân quyền nghiệp vụ thực tế trong trường đại học:

| STT | Tên đăng nhập | Mật khẩu mặc định | Vai trò (`Role`) | Chức danh / Đại diện | Phạm vi quyền hạn |
|:---:|:---|:---:|:---:|:---|:---|
| 1 | **admin** | `admin123` | `ADMIN` | Quản trị viên hệ thống | Toàn quyền hệ thống, quản lý tài khoản, cấu hình tham số, xem Audit Log |
| 2 | **bangiamhieu** | `123456` | `BAN_GIAM_HIEU` | GS.TS. Trần Văn Hiệu (Hiệu Trưởng) | Xem toàn bộ TKB, Dashboard KPI, thống kê hiệu suất, duyệt đổi lịch cấp 2 |
| 3 | **daotao** | `123456` | `PHONG_DAO_TAO` | ThS. Hoàng Minh Đào Tạo (Cán Bộ ĐT) | Xếp lịch TKB, quản lý phòng học, phê duyệt đổi lịch, xuất báo cáo |
| 4 | **truongkhoa** | `123456` | `TRUONG_KHOA` | PGS.TS. Lê Đình Khoa (Trưởng Khoa CNTT) | Quản lý giảng viên, môn học, duyệt đề xuất đổi lịch cấp 1 của giảng viên |
| 5 | **giangvien** | `123456` | `GIANG_VIEN` | TS. Nguyễn Văn An (Giảng viên) | Xem TKB giảng dạy cá nhân, tạo phiếu đề xuất xin đổi lịch dạy |
| 6 | **sinhvien** | `123456` | `SINH_VIEN` | Đỗ Xuân Hùng (Sinh viên K21) | Xem TKB lớp học, tra cứu khung chương trình đào tạo 130 tín chỉ cá nhân |

> 💡 **Tính năng Đăng nhập Nhanh (Quick Login):** Ngay tại màn hình Đăng nhập (`LoginForm`), hệ thống tích hợp sẵn các nút bấm chọn nhanh vai trò giúp thầy cô và hội đồng chấm đồ án dễ dàng chuyển đổi qua lại giữa các vai trò để kiểm thử phân quyền mà không cần gõ lại mật khẩu.

---

## 🏛️ MÔ HÌNH KIẾN TRÚC HỆ THỐNG

Dự án được xây dựng theo mô hình **Kiến trúc phân tầng chuẩn (Layered Architecture)**, đảm bảo tính đóng gói, dễ mở rộng và bảo trì:

```
┌─────────────────────────────────────────────────────────────┐
│                 TẦNG GIAO DIỆN (PRESENTATION LAYER)         │
│  • MainForm, LoginForm, Dialogs (Xếp lịch, Đổi lịch,...)    │
│  • Panels: Dashboard, TKB Grid, CTĐT, AuditLog, Thống Kê    │
│  • Components: PaginationBar, FlatButton, Custom Renderers  │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                  TẦNG ĐIỀU KHIỂN (CONTROLLER LAYER)         │
│  • AuthController, TimetableController, CurriculumController│
│  • RescheduleController, AuditController, WebServer (API)   │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                 TẦNG NGHIỆP VỤ (SERVICE LAYER)              │
│  • XepLichService (Thuật toán kiểm tra xung đột thời gian)   │
│  • WorkflowService (Luồng phê duyệt đổi lịch 2 cấp độ)      │
│  • AuthService (Xác thực người dùng & băm mật khẩu SHA-256) │
│  • AuditService (Ghi nhận vết kiểm toán hệ thống)           │
│  • ThongKeService (Tổng hợp dữ liệu & tính toán KPI)        │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│            TẦNG TRUY XUẤT DỮ LIỆU (DATA ACCESS LAYER - DAO) │
│  • ThoiKhoaBieuDAO, PhongHocDAO, GiangVienDAO, LopHocDAO    │
│  • MonHocDAO, SinhVienDAO, ChuongTrinhDaoTaoDAO             │
│  • YeuCauDoiLichDAO, AuditLogDAO, TaiKhoanDAO               │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│               TẦNG CƠ SỞ DỮ LIỆU (DATABASE LAYER)           │
│  • MySQL 8.0+ / MariaDB (Bộ mã UTF-8 Unicode utf8mb4)       │
│  • Chuẩn hóa 3NF, Ràng buộc khóa ngoại, Đánh chỉ mục Index │
└─────────────────────────────────────────────────────────────┘
```

---

## 📸 HÌNH ẢNH GIAO DIỆN HỆ THỐNG

### 1. Trang Chủ Dashboard KPI Quản Trị
![Dashboard KPI](screenshots/hinh_trangchu_moi.png)

### 2. Lưới Thời Khóa Biểu Trực Quan Đa Chiều
![Lưới TKB](screenshots/hinh_tkb_grid_centered_scroll.png)

### 3. Thời Khóa Biểu Giảng Dạy Cá Nhân Của Giảng Viên
![TKB Giảng Viên](screenshots/hinh_tkb_giangvien.png)

### 4. Đề Xuất & Phê Duyệt Đổi Lịch 2 Cấp Độ
![Đổi Lịch 2 Cấp](screenshots/hinh_dexuat_doilich.png)

### 5. Quản Lý Khung Chương Trình Đào Tạo 130 Tín Chỉ (K21 - K25)
![Khung CTĐT 130 Tín Chỉ](screenshots/hinh_ctdt_k24_130tc.png)

### 6. Nhật Ký Kiểm Toán An Ninh Hệ Thống (Audit Log)
![Nhật ký Audit Log](screenshots/hinh_audit_log.png)

### 7. Thống Kê Giờ Giảng & Tải Đào Tạo Giảng Viên
![Thống Kê Giảng Viên](screenshots/hinh_thongke_giangvien.png)

### 8. Quản Lý Sinh Viên Với Thanh Phân Trang Chuẩn Hóa
![Phân Trang Sinh Viên](screenshots/hinh_sinhvien_pagination.png)

---

## 📁 CẤU TRÚC THƯ MỤC DỰ ÁN

```
BTL/
├── docs/                               # Tài liệu thiết kế & sơ đồ kiến trúc
│   ├── architecture/
│   │   ├── bieudo_phancap_chucnang.drawio     # Sơ đồ phân cấp chức năng (BFD)
│   │   └── kientruc_hethong_phantang.drawio   # Sơ đồ kiến trúc phân tầng
│   └── images/                         # Ảnh xuất bản đồ án chất lượng cao
├── screenshots/                        # Ảnh chụp màn hình nghiệm thu giao diện
├── lib/
│   └── mysql-connector-j-8.3.0.jar     # Thư viện Driver JDBC MySQL
├── nbproject/                          # Cấu hình dự án NetBeans IDE
├── src/
│   ├── connection/                     # Quản lý kết nối JDBC Singleton
│   │   ├── DBConnection.java
│   │   └── AppConfig.java
│   ├── controller/                     # Bộ điều khiển trung gian & Web API
│   │   ├── AuthController.java
│   │   ├── TimetableController.java
│   │   ├── CurriculumController.java
│   │   ├── RescheduleController.java
│   │   ├── AuditController.java
│   │   ├── UserController.java
│   │   └── WebServer.java
│   ├── dao/                            # Tầng thao tác CSDL (CRUD)
│   │   ├── TaiKhoanDAO.java
│   │   ├── PhongHocDAO.java
│   │   ├── GiangVienDAO.java
│   │   ├── MonHocDAO.java
│   │   ├── LopHocDAO.java
│   │   ├── SinhVienDAO.java
│   │   ├── ChuongTrinhDaoTaoDAO.java
│   │   ├── ThoiKhoaBieuDAO.java
│   │   ├── YeuCauDoiLichDAO.java
│   │   └── AuditLogDAO.java
│   ├── model/                          # Các thực thể dữ liệu nghiệp vụ
│   │   ├── TaiKhoan.java
│   │   ├── PhongHoc.java
│   │   ├── GiangVien.java
│   │   ├── MonHoc.java
│   │   ├── LopHoc.java
│   │   ├── SinhVien.java
│   │   ├── ChuongTrinhDaoTao.java
│   │   ├── ThoiKhoaBieu.java
│   │   ├── YeuCauDoiLich.java
│   │   └── AuditLog.java
│   ├── service/                        # Xử lý logic nghiệp vụ cốt lõi
│   │   ├── AuthService.java
│   │   ├── XepLichService.java
│   │   ├── WorkflowService.java
│   │   ├── AuditService.java
│   │   └── ThongKeService.java
│   ├── util/                           # Tiện ích mã hóa, kiểm thử, phân trang
│   │   ├── PasswordUtil.java
│   │   ├── ValidationUtil.java
│   │   ├── ExportUtil.java
│   │   ├── UIUtil.java
│   │   └── DataSeeder.java
│   └── view/                           # Giao diện đồ họa người dùng (Swing)
│       ├── LoginForm.java
│       ├── MainForm.java
│       ├── dialog/                     # Hộp thoại popup nghiệp vụ
│       │   ├── PhongHocDialog.java
│       │   ├── GiangVienDialog.java
│       │   ├── MonHocDialog.java
│       │   ├── LopHocDialog.java
│       │   ├── XepLichDialog.java
│       │   ├── DoiLichDialog.java
│       │   └── TaiKhoanDialog.java
│       └── panel/                      # Các màn hình chính (Tabs/Panels)
│           ├── DashboardPanel.java
│           ├── TimetableGridPanel.java
│           ├── ThoiKhoaBieuPanel.java
│           ├── DeXuatDoiLichPanel.java
│           ├── DoiLichPanel.java
│           ├── CurriculumPanel.java
│           ├── SinhVienPanel.java
│           ├── GiangVienPanel.java
│           ├── PhongHocPanel.java
│           ├── MonHocPanel.java
│           ├── LopHocPanel.java
│           ├── ThongKePanel.java
│           ├── AuditLogPanel.java
│           ├── TaiKhoanPanel.java
│           └── PaginationBar.java
├── test/                               # Kịch bản kiểm thử tự động
│   └── TestLogicRunner.java
├── database.sql                        # Script khởi tạo CSDL & dữ liệu mẫu
├── BTL_CN_Java.docx                    # Thuyết minh báo cáo đồ án chính thức
├── BAO_CAO_DO_AN_CNJ56.md              # Báo cáo đồ án định dạng Markdown
├── build.xml                           # File Ant Build tự động
├── manifest.mf                         # Định nghĩa Main-Class khởi chạy
├── .gitignore                          # Cấu hình bỏ qua file rác khi đẩy Git
└── README.md                           # Tài liệu hướng dẫn sử dụng đồ án
```

---

## 🚀 HƯỚNG DẪN CÀI ĐẶT & KHỞI CHẠY (QUICK START)

### Yêu cầu tiên quyết:
- **Java Development Kit (JDK):** JDK 8, JDK 11 hoặc JDK 17+.
- **IDE:** NetBeans IDE (bản 8.2, 12, 17, 20 hoặc mới hơn).
- **Web/Database Server:** XAMPP (chứa Apache và MySQL Server).

---

### Bước 1: Khởi động MySQL Server & Nạp Cơ sở dữ liệu
1. Mở **XAMPP Control Panel**, nhấn nút **Start** tại mục **MySQL**.
2. Mở trình duyệt web bất kỳ, truy cập vào giao diện quản lý: `http://localhost/phpmyadmin/`.
3. Nhấp vào tab **Import** (hoặc **SQL**), bấm chọn file **`database.sql`** trong thư mục gốc của dự án.
4. Nhấn nút **Go / Thực hiện** để khởi tạo cơ sở dữ liệu `quanly_tkb_cnj56` kèm toàn bộ bảng và dữ liệu mẫu kiểm thử.

> ⚙️ **Thông số kết nối mặc định trong `DBConnection.java`:**
> - **Host:** `localhost` | **Port:** `3306`
> - **Database:** `quanly_tkb_cnj56`
> - **Username:** `root` | **Password:** *(để trống theo chuẩn XAMPP)*

---

### Bước 2: Mở Dự Án Trong NetBeans IDE
1. Mở phần mềm **NetBeans IDE**.
2. Trên thanh menu chính, chọn **File** $\to$ **Open Project...** (phím tắt `Ctrl + Shift + O`).
3. Điều hướng tới thư mục chứa mã nguồn: `C:\Users\...\NetBeansProjects\BTL` và nhấn **Open Project**.
4. NetBeans sẽ tự động quét dự án Ant Java SE và nạp sẵn thư viện kết nối `lib/mysql-connector-j-8.3.0.jar`.

---

### Bước 3: Biên Dịch & Khởi Chạy Ứng Dụng
1. Nhấp chuột phải vào tên dự án `BTL` ở cột bên trái $\to$ Chọn **Clean and Build** để biên dịch toàn bộ mã nguồn.
2. Nhấn phím **F6** (hoặc bấm nút **Run Project ▶** màu xanh lá cây trên thanh công cụ).
3. Màn hình Đăng nhập hiển thị: Bạn có thể đăng nhập bằng tài khoản `admin` / `admin123` hoặc bấm trực tiếp vào các nút vai trò nhanh bên dưới để trải nghiệm đầy đủ các tính năng của hệ thống!

---

## 🛠️ HƯỚNG DẪN ĐỒNG BỘ CODE LÊN GITHUB

Mỗi khi chỉnh sửa mã nguồn hoặc bổ sung tính năng mới, bạn có thể đồng bộ lên GitHub bằng lệnh:

```powershell
# 1. Thêm các tệp tin thay đổi vào Git
git add .

# 2. Ghi nhận thay đổi với thông điệp rõ ràng
git commit -m "Noi dung cap nhat tinh nang"

# 3. Đẩy lên kho lưu trữ GitHub
git push origin main
```

---

<p align="center">
  <i>Đồ án môn học Công nghệ Java - Học viện Công nghệ Bưu chính Viễn thông (PTIT)</i><br>
  <b>Nhóm tác giả: Nguyễn Mạnh Quyết & Hà Thái Bảo</b>
</p>
