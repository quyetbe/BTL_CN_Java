# ĐỒ ÁN MÃ ĐỀ TÀI: CNJ56
## ỨNG DỤNG DESKTOP QUẢN LÝ THỜI KHÓA BIỂU VÀ TÀI NGUYÊN PHÒNG HỌC
### (Java Swing + JDBC + MySQL + NetBeans IDE + XAMPP)

---

## 1. GIỚI THIỆU TỔNG QUAN

Hệ thống desktop hoàn chỉnh hỗ trợ phòng Đào tạo và bộ phận Quản trị cơ sở vật chất của nhà trường trong việc:
- Quản lý danh mục phòng học và trang thiết bị tài nguyên (máy chiếu, điều hòa, máy tính, âm thanh, sức chứa...).
- Quản lý giảng viên, bộ môn, khoa, email, số điện thoại.
- Quản lý danh mục môn học (Lý thuyết, Thực hành) và lớp học.
- **Xếp lịch giảng dạy (Thời khóa biểu)** theo Học kỳ, Năm học, Tuần, Thứ, Tiết.
- **Thuật toán thông minh tự động phát hiện & ngăn chặn xung đột lịch:**
  1. Trùng phòng học ở cùng khung giờ/tuần.
  2. Trùng giảng viên (giảng viên dạy 2 lớp cùng lúc).
  3. Trùng lớp học (lớp học 2 môn cùng lúc).
  4. Cảnh báo quá tải sức chứa phòng so với sĩ số lớp.
  5. Ràng buộc loại phòng tương thích (Môn thực hành phải xếp phòng máy tính/thực hành).
  6. Khóa xếp lịch với phòng đang bảo trì / ngừng sử dụng.
- **Giao diện Lưới Thời Khóa Biểu Tuần trực quan:** Hiển thị Thứ 2 – Chủ Nhật x 12 Tiết học, đổi chế độ xem theo Phòng, Giảng viên, Lớp học, nhấp đúp xem chi tiết.
- **Thống kê & Tra cứu phòng trống:** Đo lường tỷ lệ sử dụng từng phòng học theo tuần/học kỳ, tra cứu nhanh các phòng còn trống theo ca học để xếp lịch đột xuất.
- **Xuất báo cáo:** Xuất danh sách và lưới thời khóa biểu ra file Excel/CSV chuẩn UTF-8 tiếng Việt.
- **Phân quyền người dùng:** Quản trị viên (ADMIN) và Nhân viên đào tạo (NHANVIEN).

---

## 2. CÔNG NGHỆ & MÔ HÌNH KIẾN TRÚC

- **Ngôn ngữ:** Java SE (JDK 8 trở lên)
- **Giao diện:** Java Swing (Flat UI, Segoe UI, JTable, CardLayout)
- **IDE:** NetBeans IDE (Ant-based Java SE Project)
- **Cơ sở dữ liệu:** MySQL 8.0+ / MariaDB chạy qua **XAMPP**
- **Kết nối CSDL:** JDBC thuần (`java.sql.*`), driver `mysql-connector-j-8.3.0.jar`
- **Bảo mật:** Băm mật khẩu bằng **SHA-256**, 100% câu truy vấn dùng **PreparedStatement** (chống SQL Injection), ràng buộc toàn vẹn khóa ngoại `ON DELETE RESTRICT`.
- **Kiến trúc phân lớp chuẩn:**
  - `connection`: Singleton quản lý kết nối JDBC.
  - `model`: Thực thể dữ liệu (`PhongHoc`, `GiangVien`, `MonHoc`, `LopHoc`, `ThoiKhoaBieu`, `TaiKhoan`).
  - `dao`: Thao tác CRUD dữ liệu.
  - `service`: Chứa logic nghiệp vụ cốt lõi (`AuthService`, `XepLichService`, `ThongKeService`).
  - `util`: Mã hóa mật khẩu, kiểm tra hợp lệ dữ liệu, xuất file báo cáo UTF-8 BOM, tạo kiểu giao diện.
  - `view`: Giao diện đồ họa Swing (Forms, Panels, Dialogs).

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN

```
QuanLyTKB_CNJ56/
├── nbproject/                  # Cấu hình dự án chuẩn NetBeans IDE
│   ├── project.xml
│   └── project.properties
├── build.xml                   # File cấu hình Ant Build
├── manifest.mf                 # Manifest chỉ định Main-Class: view.LoginForm
├── lib/
│   └── mysql-connector-j-8.3.0.jar # Driver JDBC MySQL
├── database.sql                # Script tạo CSDL MySQL 3NF + Dữ liệu mẫu kiểm thử
├── test/
│   └── TestLogicRunner.java    # Bộ kiểm thử tự động thuật toán xung đột
├── src/
│   ├── connection/
│   │   └── DBConnection.java
│   ├── model/
│   │   ├── TaiKhoan.java
│   │   ├── PhongHoc.java
│   │   ├── GiangVien.java
│   │   ├── MonHoc.java
│   │   ├── LopHoc.java
│   │   └── ThoiKhoaBieu.java
│   ├── dao/
│   │   ├── TaiKhoanDAO.java
│   │   ├── PhongHocDAO.java
│   │   ├── GiangVienDAO.java
│   │   ├── MonHocDAO.java
│   │   ├── LopHocDAO.java
│   │   └── ThoiKhoaBieuDAO.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── XepLichService.java
│   │   └── ThongKeService.java
│   ├── util/
│   │   ├── PasswordUtil.java
│   │   ├── ValidationUtil.java
│   │   ├── ExportUtil.java
│   │   └── UIUtil.java
│   └── view/
│       ├── LoginForm.java
│       ├── MainForm.java
│       ├── dialog/
│       │   ├── PhongHocDialog.java
│       │   ├── GiangVienDialog.java
│       │   ├── MonHocDialog.java
│       │   ├── LopHocDialog.java
│       │   ├── XepLichDialog.java
│       │   └── TaiKhoanDialog.java
│       └── panel/
│           ├── DashboardPanel.java
│           ├── PhongHocPanel.java
│           ├── GiangVienPanel.java
│           ├── MonHocPanel.java
│           ├── LopHocPanel.java
│           ├── ThoiKhoaBieuPanel.java
│           ├── TimetableGridPanel.java
│           ├── ThongKePanel.java
│           └── TaiKhoanPanel.java
└── README.md
```

---

## 4. HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN

### Bước 1: Khởi động XAMPP & Tạo Cơ sở dữ liệu
1. Mở **XAMPP Control Panel**, nhấn **Start** tại dịch vụ **Apache** và **MySQL** (cổng mặc định 3306).
2. Mở trình duyệt web, truy cập vào phpMyAdmin: `http://localhost/phpmyadmin/`.
3. Nhấp vào tab **Import** (hoặc **SQL**), chọn file **`database.sql`** trong thư mục dự án và nhấn **Import** (hoặc **Go**).
4. CSDL `quanly_tkb_cnj56` sẽ được tạo kèm đầy đủ 6 bảng, các ràng buộc khóa ngoại, chỉ mục (Index) và dữ liệu mẫu kiểm thử.

> **Cấu hình kết nối MySQL mặc định trong `DBConnection.java`:**
> - Host: `localhost` | Cổng: `3306`
> - CSDL: `quanly_tkb_cnj56`
> - User: `root` | Password: *(để trống - mặc định của XAMPP)*

### Bước 2: Mở và Chạy dự án trên NetBeans IDE
1. Khởi động **NetBeans IDE**.
2. Trên thanh menu, chọn **File** $\rightarrow$ **Open Project...**
3. Tìm đến thư mục `BTL` (hoặc `QuanLyTKB_CNJ56`) và nhấn **Open Project**.
4. NetBeans sẽ tự động nhận diện dự án Java SE kèm thư viện `mysql-connector-j-8.3.0.jar` trong thư mục `lib/`.
5. Nhấn phím **F6** (hoặc nút xanh **Run Project**) để khởi chạy ứng dụng!
6. Màn hình **Đăng nhập (`LoginForm`)** sẽ xuất hiện.

---

## 5. TÀI KHOẢN ĐĂNG NHẬP MẪU

| Tên đăng nhập | Mật khẩu | Vai trò | Quyền hạn |
|---|---|---|---|
| **admin** | `admin123` | **ADMIN** | Toàn quyền CRUD tất cả danh mục, xếp lịch, phân quyền tài khoản |
| **daotao01** | `nhanvien123` | **NHANVIEN** | Xếp lịch, xem TKB, xem danh mục, tra cứu phòng trống (bị khóa xóa danh mục) |
| **daotao02** | `nhanvien123` | **NHANVIEN** | Tương tự nhân viên đào tạo 01 |
| **demo_lock** | `123456` | **NHANVIEN** | Tài khoản bị khóa (dùng để test tình huống bị khóa) |

---

## 6. THUẬT TOÁN PHÁT HIỆN XUNG ĐỘT THỜI KHÓA BIỂU

Thuật toán trong `XepLichService` thực hiện theo công thức:
- Hai lịch $A$ và $B$ **trùng nhau về thời gian** khi và chỉ khi:
  $$\begin{cases}
  A.\text{hoc\_ky} = B.\text{hoc\_ky} \land A.\text{nam\_hoc} = B.\text{nam\_hoc} \\
  A.\text{thu\_trong\_tuan} = B.\text{thu\_trong\_tuan} \\
  A.\text{tuan\_bat\_dau} \le B.\text{tuan\_ket\_thuc} \land A.\text{tuan\_ket\_thuc} \ge B.\text{tuan\_bat\_dau} \\
  A.\text{tiet\_bat\_dau} \le B.\text{tiet\_ket\_thuc} \land A.\text{tiet\_ket\_thuc} \ge B.\text{tiet\_bat\_dau}
  \end{cases}$$
- *(Khi sửa lịch, bản ghi đang được sửa với `excludeId` sẽ được loại trừ để không báo trùng với chính nó).*
- Các kiểm tra vi phạm:
  1. Trùng mã phòng: Báo lỗi xung đột phòng.
  2. Trùng mã giảng viên: Báo lỗi giảng viên đang dạy lớp khác.
  3. Trùng mã lớp: Báo lỗi lớp đang học môn khác.
  4. Trạng thái phòng không phải `DANG_SU_DUNG`: Từ chối xếp lịch.
  5. Môn thực hành xếp vào phòng lý thuyết: Từ chối để đảm bảo trang thiết bị.
  6. Sức chứa phòng < sĩ số lớp: Đưa ra cảnh báo trực tiếp.
