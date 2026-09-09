-- ==============================================================
-- ĐỒ ÁN MÃ ĐỀ TÀI: CNJ56
-- ỨNG DỤNG DESKTOP QUẢN LÝ THỜI KHÓA BIỂU VÀ TÀI NGUYÊN PHÒNG HỌC
-- Môi trường: MySQL 8.0+ / MariaDB (XAMPP) - phpMyAdmin
-- Bộ mã: UTF-8 Unicode (utf8mb4)
-- ==============================================================

CREATE DATABASE IF NOT EXISTS `quanly_tkb_cnj56` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `quanly_tkb_cnj56`;

-- --------------------------------------------------------------
-- 1. BẢNG: tai_khoan (Quản lý người dùng & phân quyền)
-- --------------------------------------------------------------
DROP TABLE IF EXISTS `thoi_khoa_bieu`;
DROP TABLE IF EXISTS `tai_khoan`;
DROP TABLE IF EXISTS `phong_hoc`;
DROP TABLE IF EXISTS `giang_vien`;
DROP TABLE IF EXISTS `mon_hoc`;
DROP TABLE IF EXISTS `lop_hoc`;

CREATE TABLE `tai_khoan` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_dang_nhap` VARCHAR(50) NOT NULL UNIQUE,
    `mat_khau` VARCHAR(255) NOT NULL COMMENT 'Mật khẩu băm SHA-256',
    `ho_ten` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100),
    `vai_tro` ENUM('ADMIN', 'NHANVIEN', 'SINH_VIEN', 'GIANG_VIEN', 'TRUONG_BO_MON', 'PHONG_DAO_TAO', 'BAN_GIAM_HIEU', 'TRUONG_KHOA') NOT NULL DEFAULT 'NHANVIEN',
    `trang_thai` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1: Hoạt động, 0: Bị khóa',
    `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 2. BẢNG: phong_hoc (Quản lý phòng học & tài nguyên)
-- --------------------------------------------------------------
CREATE TABLE `phong_hoc` (
    `ma_phong` VARCHAR(20) PRIMARY KEY,
    `ten_phong` VARCHAR(100) NOT NULL,
    `toa_nha` VARCHAR(50) NOT NULL,
    `suc_chua` INT NOT NULL,
    `loai_phong` ENUM('LY_THUYET', 'THUC_HANH', 'HOI_TRUONG') NOT NULL DEFAULT 'LY_THUYET',
    `trang_thiet_bi` TEXT COMMENT 'Máy chiếu, Điều hòa, 40 PC, Âm thanh...',
    `trang_thai` ENUM('DANG_SU_DUNG', 'BAO_TRI', 'NGUNG_SU_DUNG') NOT NULL DEFAULT 'DANG_SU_DUNG',
    CONSTRAINT `chk_phong_suc_chua` CHECK (`suc_chua` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 3. BẢNG: giang_vien (Quản lý giảng viên)
-- --------------------------------------------------------------
CREATE TABLE `giang_vien` (
    `ma_gv` VARCHAR(20) PRIMARY KEY,
    `ho_ten` VARCHAR(100) NOT NULL,
    `khoa_bo_mon` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `so_dien_thoai` VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 4. BẢNG: mon_hoc (Quản lý môn học)
-- --------------------------------------------------------------
CREATE TABLE `mon_hoc` (
    `ma_mon` VARCHAR(20) PRIMARY KEY,
    `ten_mon` VARCHAR(100) NOT NULL,
    `so_tin_chi` INT NOT NULL,
    `loai_mon` ENUM('LY_THUYET', 'THUC_HANH') NOT NULL DEFAULT 'LY_THUYET',
    CONSTRAINT `chk_mon_tin_chi` CHECK (`so_tin_chi` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 5. BẢNG: lop_hoc (Quản lý lớp sinh viên)
-- --------------------------------------------------------------
CREATE TABLE `lop_hoc` (
    `ma_lop` VARCHAR(20) PRIMARY KEY,
    `ten_lop` VARCHAR(100) NOT NULL,
    `si_so` INT NOT NULL,
    `khoa_hoc` VARCHAR(50) NOT NULL,
    CONSTRAINT `chk_lop_si_so` CHECK (`si_so` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 5.1. BẢNG: sinh_vien (Quản lý hồ sơ sinh viên)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sinh_vien` (
    `ma_sv` VARCHAR(20) PRIMARY KEY,
    `ho_ten` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100),
    `so_dien_thoai` VARCHAR(20),
    `gioi_tinh` ENUM('Nam', 'Nu') DEFAULT 'Nam',
    `ngay_sinh` DATE DEFAULT '2004-05-15',
    `ma_lop` VARCHAR(20),
    `khoa_hoc` VARCHAR(20),
    `trang_thai` VARCHAR(50) DEFAULT 'DANG_HOC',
    `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_sv_lop` (`ma_lop`),
    INDEX `idx_sv_khoa` (`khoa_hoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 6. BẢNG: thoi_khoa_bieu (Lịch giảng dạy & xếp phòng)
-- --------------------------------------------------------------
CREATE TABLE `thoi_khoa_bieu` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_mon` VARCHAR(20) NOT NULL,
    `ma_lop` VARCHAR(20) NOT NULL,
    `ma_gv` VARCHAR(20) NOT NULL,
    `ma_phong` VARCHAR(20) NOT NULL,
    `thu_trong_tuan` INT NOT NULL COMMENT '2: Thứ Hai, ..., 8: Chủ Nhật',
    `tiet_bat_dau` INT NOT NULL COMMENT '1 đến 12',
    `so_tiet` INT NOT NULL COMMENT 'Số tiết (1 đến 6)',
    `tiet_ket_thuc` INT NOT NULL COMMENT 'tiet_bat_dau + so_tiet - 1',
    `tuan_bat_dau` INT NOT NULL COMMENT 'Tuần bắt đầu (VD: 1)',
    `tuan_ket_thuc` INT NOT NULL COMMENT 'Tuần kết thúc (VD: 15)',
    `hoc_ky` VARCHAR(20) NOT NULL COMMENT 'HK1, HK2, HK3 (Hè)',
    `nam_hoc` VARCHAR(20) NOT NULL COMMENT 'VD: 2025-2026',
    `ghi_chu` TEXT,
    CONSTRAINT `chk_thu_tuan` CHECK (`thu_trong_tuan` BETWEEN 2 AND 8),
    CONSTRAINT `chk_tiet_bat_dau` CHECK (`tiet_bat_dau` BETWEEN 1 AND 12),
    CONSTRAINT `chk_so_tiet` CHECK (`so_tiet` BETWEEN 1 AND 6),
    CONSTRAINT `chk_tiet_ket_thuc` CHECK (`tiet_ket_thuc` BETWEEN 1 AND 12 AND `tiet_ket_thuc` >= `tiet_bat_dau`),
    CONSTRAINT `chk_tuan` CHECK (`tuan_bat_dau` >= 1 AND `tuan_ket_thuc` >= `tuan_bat_dau`),
    CONSTRAINT `fk_tkb_mon` FOREIGN KEY (`ma_mon`) REFERENCES `mon_hoc` (`ma_mon`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_tkb_lop` FOREIGN KEY (`ma_lop`) REFERENCES `lop_hoc` (`ma_lop`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_tkb_gv` FOREIGN KEY (`ma_gv`) REFERENCES `giang_vien` (`ma_gv`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_tkb_phong` FOREIGN KEY (`ma_phong`) REFERENCES `phong_hoc` (`ma_phong`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- ĐÁNH CHỈ MỤC (INDEXES) ĐỂ TỐI ƯU TRUY VẤN LỌC & TÌM KIẾM
-- --------------------------------------------------------------
CREATE INDEX `idx_tkb_phong` ON `thoi_khoa_bieu` (`ma_phong`);
CREATE INDEX `idx_tkb_gv` ON `thoi_khoa_bieu` (`ma_gv`);
CREATE INDEX `idx_tkb_lop` ON `thoi_khoa_bieu` (`ma_lop`);
CREATE INDEX `idx_tkb_thoigian` ON `thoi_khoa_bieu` (`hoc_ky`, `nam_hoc`, `thu_trong_tuan`, `tiet_bat_dau`, `tiet_ket_thuc`);
CREATE INDEX `idx_phong_toanha_loai` ON `phong_hoc` (`toa_nha`, `loai_phong`, `trang_thai`);

-- ==============================================================
-- DỮ LIỆU MẪU (SEED DATA)
-- ==============================================================

-- 1. Tài khoản (Mỗi phân quyền chỉ 1 tài khoản đăng nhập duy nhất)
-- Mật khẩu: admin -> 'admin123', các tài khoản còn lại -> '123456'
INSERT INTO `tai_khoan` (`id`, `ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `trang_thai`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Quản Trị Viên Hệ Thống', 'admin@university.edu.vn', 'ADMIN', 1),
(2, 'bangiamhieu', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'GS.TS. Trần Văn Hiệu (Hiệu Trưởng)', 'bgh@university.edu.vn', 'BAN_GIAM_HIEU', 1),
(3, 'truongkhoa', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'PGS.TS. Lê Đình Khoa (Trưởng Khoa CNTT)', 'truongkhoa.cntt@university.edu.vn', 'TRUONG_KHOA', 1),
(4, 'daotao', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ThS. Hoàng Minh Đào Tạo (Cán Bộ Đào Tạo)', 'phongdaotao@university.edu.vn', 'PHONG_DAO_TAO', 1),
(5, 'giangvien', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'TS. Nguyễn Văn An (Giảng Viên Bộ Môn)', 'annv@university.edu.vn', 'GIANG_VIEN', 1),
(6, 'sinhvien', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Đỗ Xuân Hùng (Sinh Viên K21)', 'sv210001@student.university.edu.vn', 'SINH_VIEN', 1);

-- 2. Phòng học & Tài nguyên
INSERT INTO `phong_hoc` (`ma_phong`, `ten_phong`, `toa_nha`, `suc_chua`, `loai_phong`, `trang_thiet_bi`, `trang_thai`) VALUES
('A101', 'Phòng Lý thuyết A101', 'Tòa nhà A', 60, 'LY_THUYET', 'Máy chiếu EPSON, 2 Điều hòa Panasonic, Âm thanh micro không dây', 'DANG_SU_DUNG'),
('A102', 'Phòng Lý thuyết A102', 'Tòa nhà A', 50, 'LY_THUYET', 'Máy chiếu Sony, 2 Điều hòa Daikin, Bảng trượt', 'DANG_SU_DUNG'),
('A201', 'Phòng Lý thuyết A201', 'Tòa nhà A', 80, 'LY_THUYET', 'Máy chiếu, Hệ thống âm thanh vòm, 3 Điều hòa Daikin', 'DANG_SU_DUNG'),
('B201', 'Phòng Máy tính PM01', 'Tòa nhà B', 45, 'THUC_HANH', '45 Máy tính Core i7 16GB, Máy chiếu, Mạng LAN Gigabit, 2 Điều hòa', 'DANG_SU_DUNG'),
('B202', 'Phòng Máy tính PM02', 'Tòa nhà B', 40, 'THUC_HANH', '40 Máy tính Core i5 16GB, Máy chiếu, Điều hòa trung tâm', 'DANG_SU_DUNG'),
('B301', 'Lab Mạng & An toàn thông tin', 'Tòa nhà B', 35, 'THUC_HANH', '35 PC, Tủ Rack Cisco Router/Switch, Cáp quang thực hành', 'DANG_SU_DUNG'),
('C101', 'Hội trường lớn C101', 'Tòa nhà C', 200, 'HOI_TRUONG', 'Màn hình LED P3 300 inch, Âm thanh sân khấu chuyên nghiệp, Máy chiếu dự phòng', 'DANG_SU_DUNG'),
('C102', 'Phòng Hội thảo C102', 'Tòa nhà C', 90, 'HOI_TRUONG', 'Máy chiếu 4K, Hệ thống họp trực tuyến Polycom, Mic hội thảo', 'BAO_TRI'),
('LT201', 'Giảng Đường LT201', 'Tòa B', 90, 'LY_THUYET', 'Máy chiếu Laser, Âm thanh mic không dây, 4 Điều hòa', 'DANG_SU_DUNG'),
('LT202', 'Giảng Đường LT202', 'Tòa B', 90, 'LY_THUYET', 'Máy chiếu Laser, Âm thanh mic không dây, 4 Điều hòa', 'DANG_SU_DUNG'),
('LT203', 'Giảng Đường LT203', 'Tòa B', 90, 'LY_THUYET', 'Máy chiếu Laser, Âm thanh mic không dây, 4 Điều hòa', 'DANG_SU_DUNG'),
('LT204', 'Giảng Đường LT204', 'Tòa B', 90, 'LY_THUYET', 'Máy chiếu Laser, Âm thanh mic không dây, 4 Điều hòa', 'DANG_SU_DUNG'),
('PM101', 'Phòng Máy 101', 'Tòa A', 45, 'THUC_HANH', '45 Máy PC Core i7, Máy chiếu, Điều hòa, LAN GigE', 'DANG_SU_DUNG'),
('PM102', 'Phòng Máy 102', 'Tòa A', 45, 'THUC_HANH', '45 Máy PC Core i7, Máy chiếu, Điều hòa, LAN GigE', 'DANG_SU_DUNG'),
('PM103', 'Phòng Máy 103', 'Tòa A', 45, 'THUC_HANH', '45 Máy PC Core i7, Máy chiếu, Điều hòa, LAN GigE', 'DANG_SU_DUNG'),
('PM104', 'Phòng Máy 104', 'Tòa A', 45, 'THUC_HANH', '45 Máy PC Core i7, Máy chiếu, Điều hòa, LAN GigE', 'DANG_SU_DUNG');

-- 3. Giảng viên
INSERT INTO `giang_vien` (`ma_gv`, `ho_ten`, `khoa_bo_mon`, `email`, `so_dien_thoai`) VALUES
('GV0001', 'TS. Nguyễn Văn An', 'Khoa Công nghệ thông tin', 'annv@school.edu.vn', '0912345678'),
('GV0002', 'ThS. Lê Thị Bình', 'Khoa Công nghệ thông tin', 'binhlt@school.edu.vn', '0923456789'),
('GV0003', 'PGS.TS. Phạm Quốc Cường', 'Bộ môn Khoa học máy tính', 'cuongpq@school.edu.vn', '0934567890'),
('GV0004', 'ThS. Hoàng Thị Duyên', 'Bộ môn Kỹ thuật phần mềm', 'duyenht@school.edu.vn', '0945678901'),
('GV0005', 'TS. Đỗ Minh Đức', 'Bộ môn Mạng & Hệ thống', 'ducdm@school.edu.vn', '0956789012'),
('GV0006', 'ThS. Vũ Thùy Linh', 'Bộ môn Toán - Tin ứng dụng', 'linhvt@school.edu.vn', '0967890123'),
('GV0007', 'TS. Đỗ Tuấn Vinh', 'Bộ môn Mạng & Hệ thống', 'vinhdt@school.edu.vn', '0978901234'),
('GV0008', 'ThS. Hồ Đình Long', 'Bộ môn Kỹ thuật phần mềm', 'longhd@school.edu.vn', '0989012345'),
('GV0009', 'TS. Lê Thanh Hùng', 'Khoa Công nghệ thông tin', 'hunglt@school.edu.vn', '0990123456'),
('GV0010', 'ThS. Trần Đức Sơn', 'Bộ môn Khoa học máy tính', 'sontd@school.edu.vn', '0911223344'),
('GV0011', 'TS. Đặng Tuấn Cường', 'Bộ môn Kỹ thuật phần mềm', 'cuongdt@school.edu.vn', '0922334455'),
('GV0012', 'ThS. Hoàng Đức Linh', 'Bộ môn Mạng & Hệ thống', 'linhhd@school.edu.vn', '0933445566'),
('GV0013', 'PGS.TS. Lê Hồng Thắng', 'Khoa Công nghệ thông tin', 'thanglh@school.edu.vn', '0944556677'),
('GV0014', 'TS. Vũ Đình Khoa', 'Bộ môn Khoa học máy tính', 'khoavd@school.edu.vn', '0955667788'),
('GV0015', 'ThS. Nguyễn Ngọc Yến', 'Bộ môn Toán - Tin ứng dụng', 'yennn@school.edu.vn', '0966778899'),
('GV0016', 'TS. Phạm Minh Tuấn', 'Khoa Công nghệ thông tin', 'tuanpm@school.edu.vn', '0977889900');

-- 4. Môn học (42 môn học chuẩn khung CTĐT 130 tín chỉ)
INSERT INTO `mon_hoc` (`ma_mon`, `ten_mon`, `so_tin_chi`, `loai_mon`) VALUES
-- HK1: 16 TC
('CS101', 'Tin Học Đại Cương', 3, 'LY_THUYET'),
('MA101', 'Giải Tích 1', 3, 'LY_THUYET'),
('MA102', 'Đại Số Tuyến Tính', 3, 'LY_THUYET'),
('POL101', 'Triết Học Mác - Lênin', 3, 'LY_THUYET'),
('ENG101', 'Tiếng Anh Cơ Bản 1', 3, 'LY_THUYET'),
('PE101', 'Giáo Dục Thể Chất 1', 1, 'THUC_HANH'),
-- HK2: 17 TC
('CS102', 'Kỹ Thuật Lập Trình C/C++', 4, 'THUC_HANH'),
('MA103', 'Giải Tích 2', 3, 'LY_THUYET'),
('PH101', 'Vật Lý Đại Cương', 3, 'LY_THUYET'),
('POL102', 'Kinh Tế Chính Trị Mác - Lênin', 2, 'LY_THUYET'),
('ENG102', 'Tiếng Anh Cơ Bản 2', 3, 'LY_THUYET'),
('PE102', 'Giáo Dục Thể Chất 2', 1, 'THUC_HANH'),
('LAW101', 'Pháp Luật Đại Cương', 1, 'LY_THUYET'),
-- HK3: 17 TC
('CS201', 'Cấu Trúc Dữ Liệu & Giải Thuật', 4, 'THUC_HANH'),
('CS202', 'Lập Trình Hướng Đối Tượng Java', 4, 'THUC_HANH'),
('MA201', 'Toán Rời Rạc', 3, 'LY_THUYET'),
('CS204', 'Kiến Trúc Máy Tính & Hợp Ngữ', 3, 'LY_THUYET'),
('POL103', 'Chủ Nghĩa Xã Hội Khoa Học', 2, 'LY_THUYET'),
('PE103', 'Giáo Dục Thể Chất 3', 1, 'THUC_HANH'),
-- HK4: 17 TC
('CS203', 'Cơ Sở Dữ Liệu & SQL', 4, 'THUC_HANH'),
('CS301', 'Hệ Điều Hành & Linux', 3, 'LY_THUYET'),
('MA202', 'Xác Suất Thống Kê', 3, 'LY_THUYET'),
('CS205', 'Thiết Kế Web Cơ Bản', 3, 'THUC_HANH'),
('POL104', 'Lịch Sử Đảng Cộng Sản Việt Nam', 2, 'LY_THUYET'),
('POL105', 'Tư Tưởng Hồ Chí Minh', 2, 'LY_THUYET'),
-- HK5: 17 TC
('CS302', 'Mạng Máy Tính & Viễn Thông', 3, 'THUC_HANH'),
('CS304', 'Phát Triển Ứng Dụng Web', 4, 'THUC_HANH'),
('CS305', 'Hệ Quản Trị Cơ Sở Dữ Liệu', 3, 'THUC_HANH'),
('CS306', 'Phân Tích & Thiết Kế Hệ Thống', 3, 'LY_THUYET'),
('CS307', 'Lập Trình Ứng Dụng Di Động', 4, 'THUC_HANH'),
-- HK6: 17 TC
('CS303', 'Công Nghệ Phần Mềm Hiện Đại', 3, 'LY_THUYET'),
('CS308', 'Kiểm Thử Phần Mềm & QA', 3, 'THUC_HANH'),
('CS401', 'Trí Tuệ Nhân Tạo & Học Máy', 3, 'THUC_HANH'),
('CS309', 'Lập Trình Java Nâng Cao & Spring Boot', 4, 'THUC_HANH'),
('CS310', 'Điện Toán Đám Mây & DevOps', 4, 'THUC_HANH'),
-- HK7: 17 TC
('CS402', 'An Toàn & Bảo Mật Hệ Thống', 3, 'LY_THUYET'),
('CS403', 'Quản Lý Dự Án Phần Mềm', 3, 'LY_THUYET'),
('CS404', 'Xử Lý Dữ Liệu Lớn (Big Data)', 3, 'THUC_HANH'),
('CS405', 'Internet Vạn Vật (IoT) & Ứng Dụng', 3, 'THUC_HANH'),
('CS406', 'Thực Tập Doanh Nghiệp', 5, 'THUC_HANH'),
-- HK8: 12 TC
('CS407', 'Chuyên Đề Công Nghệ Mới', 2, 'LY_THUYET'),
('CS499', 'Đồ Án Tốt Nghiệp Kỹ Sư', 10, 'THUC_HANH');

-- 5. Lớp học (40 lớp học cho 4 khóa K21 - K24, mỗi lớp đúng 50 SV)
INSERT INTO `lop_hoc` (`ma_lop`, `ten_lop`, `si_so`, `khoa_hoc`) VALUES
('D21CNTT01', 'Đại học CNTT 1 - K21', 50, 'K21'),
('D21CNTT02', 'Đại học CNTT 2 - K21', 50, 'K21'),
('D21CNTT03', 'Đại học CNTT 3 - K21', 50, 'K21'),
('D21CNTT04', 'Đại học CNTT 4 - K21', 50, 'K21'),
('D21CNTT05', 'Đại học CNTT 5 - K21', 50, 'K21'),
('D21CNTT06', 'Đại học CNTT 6 - K21', 50, 'K21'),
('D21CNTT07', 'Đại học CNTT 7 - K21', 50, 'K21'),
('D21CNTT08', 'Đại học CNTT 8 - K21', 50, 'K21'),
('D21CNTT09', 'Đại học CNTT 9 - K21', 50, 'K21'),
('D21CNTT10', 'Đại học CNTT 10 - K21', 50, 'K21'),
('D22CNTT01', 'Đại học CNTT 1 - K22', 50, 'K22'),
('D22CNTT02', 'Đại học CNTT 2 - K22', 50, 'K22'),
('D22CNTT03', 'Đại học CNTT 3 - K22', 50, 'K22'),
('D22CNTT04', 'Đại học CNTT 4 - K22', 50, 'K22'),
('D22CNTT05', 'Đại học CNTT 5 - K22', 50, 'K22'),
('D22CNTT06', 'Đại học CNTT 6 - K22', 50, 'K22'),
('D22CNTT07', 'Đại học CNTT 7 - K22', 50, 'K22'),
('D22CNTT08', 'Đại học CNTT 8 - K22', 50, 'K22'),
('D22CNTT09', 'Đại học CNTT 9 - K22', 50, 'K22'),
('D22CNTT10', 'Đại học CNTT 10 - K22', 50, 'K22'),
('D23CNTT01', 'Đại học CNTT 1 - K23', 50, 'K23'),
('D23CNTT02', 'Đại học CNTT 2 - K23', 50, 'K23'),
('D23CNTT03', 'Đại học CNTT 3 - K23', 50, 'K23'),
('D23CNTT04', 'Đại học CNTT 4 - K23', 50, 'K23'),
('D23CNTT05', 'Đại học CNTT 5 - K23', 50, 'K23'),
('D23CNTT06', 'Đại học CNTT 6 - K23', 50, 'K23'),
('D23CNTT07', 'Đại học CNTT 7 - K23', 50, 'K23'),
('D23CNTT08', 'Đại học CNTT 8 - K23', 50, 'K23'),
('D23CNTT09', 'Đại học CNTT 9 - K23', 50, 'K23'),
('D23CNTT10', 'Đại học CNTT 10 - K23', 50, 'K23'),
('D24CNTT01', 'Đại học CNTT 1 - K24', 50, 'K24'),
('D24CNTT02', 'Đại học CNTT 2 - K24', 50, 'K24'),
('D24CNTT03', 'Đại học CNTT 3 - K24', 50, 'K24'),
('D24CNTT04', 'Đại học CNTT 4 - K24', 50, 'K24'),
('D24CNTT05', 'Đại học CNTT 5 - K24', 50, 'K24'),
('D24CNTT06', 'Đại học CNTT 6 - K24', 50, 'K24'),
('D24CNTT07', 'Đại học CNTT 7 - K24', 50, 'K24'),
('D24CNTT08', 'Đại học CNTT 8 - K24', 50, 'K24'),
('D24CNTT09', 'Đại học CNTT 9 - K24', 50, 'K24'),
('D24CNTT10', 'Đại học CNTT 10 - K24', 50, 'K24');

-- 6. Thời khóa biểu (Quy tắc chuẩn: T2,4,6 hoặc T3,5,6, Full Sáng / Chiều, Block 1 Tuần 1-7, Nghỉ Tuần 8-9, Block 2 Tuần 10-16)
INSERT INTO `thoi_khoa_bieu` (`id`, `ma_mon`, `ma_lop`, `ma_gv`, `ma_phong`, `thu_trong_tuan`, `tiet_bat_dau`, `so_tiet`, `tiet_ket_thuc`, `tuan_bat_dau`, `tuan_ket_thuc`, `hoc_ky`, `nam_hoc`, `ghi_chu`) VALUES
-- K24 (Năm 1) HK1 2025-2026: D24CNTT01 (T2,4,6 Sáng Block 1 CS101 -> Nghỉ T8-9 -> Block 2 MA101)
(1, 'CS101', 'D24CNTT01', 'GV0001', 'LT201', 2, 1, 5, 5, 1, 7, 'HK1', '2025-2026', 'Block 1: Full sáng T2,4,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(2, 'CS101', 'D24CNTT01', 'GV0001', 'LT201', 4, 1, 5, 5, 1, 7, 'HK1', '2025-2026', 'Block 1: Full sáng T2,4,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(3, 'CS101', 'D24CNTT01', 'GV0001', 'LT201', 6, 1, 5, 5, 1, 7, 'HK1', '2025-2026', 'Block 1: Full sáng T2,4,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(4, 'MA101', 'D24CNTT01', 'GV0002', 'LT201', 2, 1, 5, 5, 10, 16, 'HK1', '2025-2026', 'Block 2: Full sáng T2,4,6 (Tuần 10-16). Sau nghỉ 2 tuần.'),
(5, 'MA101', 'D24CNTT01', 'GV0002', 'LT201', 4, 1, 5, 5, 10, 16, 'HK1', '2025-2026', 'Block 2: Full sáng T2,4,6 (Tuần 10-16). Sau nghỉ 2 tuần.'),
(6, 'MA101', 'D24CNTT01', 'GV0002', 'LT201', 6, 1, 5, 5, 10, 16, 'HK1', '2025-2026', 'Block 2: Full sáng T2,4,6 (Tuần 10-16). Sau nghỉ 2 tuần.'),

-- K24 (Năm 1) HK1 2025-2026: D24CNTT02 (T3,5,6 Chiều Block 1 CS101 -> Nghỉ T8-9 -> Block 2 MA101)
(7, 'CS101', 'D24CNTT02', 'GV0003', 'LT202', 3, 7, 5, 11, 1, 7, 'HK1', '2025-2026', 'Block 1: Full chiều T3,5,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(8, 'CS101', 'D24CNTT02', 'GV0003', 'LT202', 5, 7, 5, 11, 1, 7, 'HK1', '2025-2026', 'Block 1: Full chiều T3,5,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(9, 'CS101', 'D24CNTT02', 'GV0003', 'LT202', 6, 7, 5, 11, 1, 7, 'HK1', '2025-2026', 'Block 1: Full chiều T3,5,6 (Tuần 1-7). Nghỉ tuần 8-9.'),
(10, 'MA101', 'D24CNTT02', 'GV0004', 'LT202', 3, 7, 5, 11, 10, 16, 'HK1', '2025-2026', 'Block 2: Full chiều T3,5,6 (Tuần 10-16). Sau nghỉ 2 tuần.'),
(11, 'MA101', 'D24CNTT02', 'GV0004', 'LT202', 5, 7, 5, 11, 10, 16, 'HK1', '2025-2026', 'Block 2: Full chiều T3,5,6 (Tuần 10-16). Sau nghỉ 2 tuần.'),
(12, 'MA101', 'D24CNTT02', 'GV0004', 'LT202', 6, 7, 5, 11, 10, 16, 'HK1', '2025-2026', 'Block 2: Full chiều T3,5,6 (Tuần 10-16). Sau nghỉ 2 tuần.');

-- --------------------------------------------------------------
-- 7. BẢNG: chuong_trinh_dao_tao (Khung CTĐT 4 năm - 8 học kỳ)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chuong_trinh_dao_tao` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `khoa` VARCHAR(100) NOT NULL DEFAULT 'Công Nghệ Thông Tin',
    `hoc_ky` INT NOT NULL COMMENT '1 đến 8',
    `ma_mon` VARCHAR(20) NOT NULL,
    `ten_mon` VARCHAR(150) NOT NULL,
    `so_tin_chi` INT NOT NULL,
    `loai_mon` ENUM('LY_THUYET', 'THUC_HANH') NOT NULL DEFAULT 'LY_THUYET',
    `khoa_hoc` VARCHAR(20) DEFAULT 'K21-K24',
    INDEX `idx_ctdt_hk` (`hoc_ky`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Nạp Khung CTĐT 4 năm - 8 học kỳ (Mỗi khóa K21, K22, K23, K24 đều đạt chuẩn 130 tín chỉ)
INSERT INTO `chuong_trinh_dao_tao` (`khoa`, `hoc_ky`, `ma_mon`, `ten_mon`, `so_tin_chi`, `loai_mon`, `khoa_hoc`)
SELECT 'Công Nghệ Thông Tin', mh.hoc_ky, mh.ma_mon, mh.ten_mon, mh.so_tin_chi, mh.loai_mon, k.khoa_hoc
FROM (
    -- HK1: 16 TC
    SELECT 1 AS hoc_ky, 'CS101' AS ma_mon, 'Tin Học Đại Cương' AS ten_mon, 3 AS so_tin_chi, 'LY_THUYET' AS loai_mon UNION ALL
    SELECT 1, 'MA101', 'Giải Tích 1', 3, 'LY_THUYET' UNION ALL
    SELECT 1, 'MA102', 'Đại Số Tuyến Tính', 3, 'LY_THUYET' UNION ALL
    SELECT 1, 'POL101', 'Triết Học Mác - Lênin', 3, 'LY_THUYET' UNION ALL
    SELECT 1, 'ENG101', 'Tiếng Anh Cơ Bản 1', 3, 'LY_THUYET' UNION ALL
    SELECT 1, 'PE101', 'Giáo Dục Thể Chất 1', 1, 'THUC_HANH' UNION ALL
    -- HK2: 17 TC
    SELECT 2, 'CS102', 'Kỹ Thuật Lập Trình C/C++', 4, 'THUC_HANH' UNION ALL
    SELECT 2, 'MA103', 'Giải Tích 2', 3, 'LY_THUYET' UNION ALL
    SELECT 2, 'PH101', 'Vật Lý Đại Cương', 3, 'LY_THUYET' UNION ALL
    SELECT 2, 'POL102', 'Kinh Tế Chính Trị Mác - Lênin', 2, 'LY_THUYET' UNION ALL
    SELECT 2, 'ENG102', 'Tiếng Anh Cơ Bản 2', 3, 'LY_THUYET' UNION ALL
    SELECT 2, 'PE102', 'Giáo Dục Thể Chất 2', 1, 'THUC_HANH' UNION ALL
    SELECT 2, 'LAW101', 'Pháp Luật Đại Cương', 1, 'LY_THUYET' UNION ALL
    -- HK3: 17 TC
    SELECT 3, 'CS201', 'Cấu Trúc Dữ Liệu & Giải Thuật', 4, 'THUC_HANH' UNION ALL
    SELECT 3, 'CS202', 'Lập Trình Hướng Đối Tượng Java', 4, 'THUC_HANH' UNION ALL
    SELECT 3, 'MA201', 'Toán Rời Rạc', 3, 'LY_THUYET' UNION ALL
    SELECT 3, 'CS204', 'Kiến Trúc Máy Tính & Hợp Ngữ', 3, 'LY_THUYET' UNION ALL
    SELECT 3, 'POL103', 'Chủ Nghĩa Xã Hội Khoa Học', 2, 'LY_THUYET' UNION ALL
    SELECT 3, 'PE103', 'Giáo Dục Thể Chất 3', 1, 'THUC_HANH' UNION ALL
    -- HK4: 17 TC
    SELECT 4, 'CS203', 'Cơ Sở Dữ Liệu & SQL', 4, 'THUC_HANH' UNION ALL
    SELECT 4, 'CS301', 'Hệ Điều Hành & Linux', 3, 'LY_THUYET' UNION ALL
    SELECT 4, 'MA202', 'Xác Suất Thống Kê', 3, 'LY_THUYET' UNION ALL
    SELECT 4, 'CS205', 'Thiết Kế Web Cơ Bản', 3, 'THUC_HANH' UNION ALL
    SELECT 4, 'POL104', 'Lịch Sử Đảng Cộng Sản Việt Nam', 2, 'LY_THUYET' UNION ALL
    SELECT 4, 'POL105', 'Tư Tưởng Hồ Chí Minh', 2, 'LY_THUYET' UNION ALL
    -- HK5: 17 TC
    SELECT 5, 'CS302', 'Mạng Máy Tính & Viễn Thông', 3, 'THUC_HANH' UNION ALL
    SELECT 5, 'CS304', 'Phát Triển Ứng Dụng Web', 4, 'THUC_HANH' UNION ALL
    SELECT 5, 'CS305', 'Hệ Quản Trị Cơ Sở Dữ Liệu', 3, 'THUC_HANH' UNION ALL
    SELECT 5, 'CS306', 'Phân Tích & Thiết Kế Hệ Thống', 3, 'LY_THUYET' UNION ALL
    SELECT 5, 'CS307', 'Lập Trình Ứng Dụng Di Động', 4, 'THUC_HANH' UNION ALL
    -- HK6: 17 TC
    SELECT 6, 'CS303', 'Công Nghệ Phần Mềm Hiện Đại', 3, 'LY_THUYET' UNION ALL
    SELECT 6, 'CS308', 'Kiểm Thử Phần Mềm & QA', 3, 'THUC_HANH' UNION ALL
    SELECT 6, 'CS401', 'Trí Tuệ Nhân Tạo & Học Máy', 3, 'THUC_HANH' UNION ALL
    SELECT 6, 'CS309', 'Lập Trình Java Nâng Cao & Spring Boot', 4, 'THUC_HANH' UNION ALL
    SELECT 6, 'CS310', 'Điện Toán Đám Mây & DevOps', 4, 'THUC_HANH' UNION ALL
    -- HK7: 17 TC
    SELECT 7, 'CS402', 'An Toàn & Bảo Mật Hệ Thống', 3, 'LY_THUYET' UNION ALL
    SELECT 7, 'CS403', 'Quản Lý Dự Án Phần Mềm', 3, 'LY_THUYET' UNION ALL
    SELECT 7, 'CS404', 'Xử Lý Dữ Liệu Lớn (Big Data)', 3, 'THUC_HANH' UNION ALL
    SELECT 7, 'CS405', 'Internet Vạn Vật (IoT) & Ứng Dụng', 3, 'THUC_HANH' UNION ALL
    SELECT 7, 'CS406', 'Thực Tập Doanh Nghiệp', 5, 'THUC_HANH' UNION ALL
    -- HK8: 12 TC
    SELECT 8, 'CS407', 'Chuyên Đề Công Nghệ Mới', 2, 'LY_THUYET' UNION ALL
    SELECT 8, 'CS499', 'Đồ Án Tốt Nghiệp Kỹ Sư', 10, 'THUC_HANH'
) mh
CROSS JOIN (
    SELECT 'K21' AS khoa_hoc UNION ALL
    SELECT 'K22' UNION ALL
    SELECT 'K23' UNION ALL
    SELECT 'K24'
) k;

-- --------------------------------------------------------------
-- 8. BẢNG: yeu_cau_doi_lich (Workflow 2 cấp: Khoa -> BGH)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `yeu_cau_doi_lich` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_tkb` INT NOT NULL,
    `ma_gv` VARCHAR(20) NOT NULL,
    `ma_phong_moi` VARCHAR(20) NOT NULL,
    `thu_moi` INT NOT NULL,
    `tiet_bat_dau_moi` INT NOT NULL,
    `so_tiet` INT NOT NULL,
    `tuan_bat_dau_moi` INT NOT NULL,
    `tuan_ket_thuc_moi` INT NOT NULL,
    `ly_do` TEXT NOT NULL,
    `trang_thai` ENUM('CHO_KHOA_DUYET', 'CHO_BGH_DUYET', 'DA_PHE_DUYET', 'TU_CHOI') DEFAULT 'CHO_KHOA_DUYET',
    `cap_phe_duyet` INT DEFAULT 1 COMMENT '1: Khoa/Bộ môn, 2: BGH/Đào tạo',
    `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`ma_tkb`) REFERENCES `thoi_khoa_bieu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 9. BẢNG: lich_su_phe_duyet
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `lich_su_phe_duyet` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `yeu_cau_id` INT NOT NULL,
    `nguoi_duyet_id` INT NOT NULL,
    `cap_duyet` VARCHAR(50) NOT NULL COMMENT 'TRUONG_BO_MON / BAN_GIAM_HIEU',
    `hanh_dong` ENUM('DONG_Y', 'TU_CHOI') NOT NULL,
    `y_kien` TEXT,
    `ngay_duyet` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`yeu_cau_id`) REFERENCES `yeu_cau_doi_lich`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------
-- 10. BẢNG: audit_log (Nhật ký kiểm toán)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT DEFAULT NULL,
    `ten_dang_nhap` VARCHAR(50),
    `hanh_dong` VARCHAR(50) NOT NULL,
    `doi_tuong` VARCHAR(50) NOT NULL,
    `doi_tuong_id` INT DEFAULT NULL,
    `du_lieu_cu` TEXT DEFAULT NULL,
    `du_lieu_moi` TEXT DEFAULT NULL,
    `ip_address` VARCHAR(50) DEFAULT '127.0.0.1',
    `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_audit_time` (`ngay_tao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `audit_log` (`user_id`, `ten_dang_nhap`, `hanh_dong`, `doi_tuong`, `doi_tuong_id`, `du_lieu_cu`, `du_lieu_moi`, `ip_address`, `ngay_tao`) VALUES
(1, 'admin', 'Khởi tạo hệ thống', 'HE_THONG', 1, NULL, 'Khởi tạo hệ thống Quản lý Đào tạo & TKB CNJ56', '127.0.0.1', '2026-09-09 20:00:00'),
(1, 'admin', 'Nạp khung CTĐT', 'CHUONG_TRINH_DAO_TAO', 42, NULL, 'Nạp Khung CTĐT 4 năm (42 môn, 130 tín chỉ)', '127.0.0.1', '2026-09-09 20:05:00'),
(1, 'admin', 'Nạp phòng học', 'PHONG_HOC', 20, NULL, 'Khởi tạo 20 phòng học (Lý thuyết, Thực hành, Hội trường)', '127.0.0.1', '2026-09-09 20:10:00'),
(1, 'admin', 'Nạp giảng viên', 'GIANG_VIEN', 500, NULL, 'Nạp 500 Giảng viên (GV0001 - GV0500)', '127.0.0.1', '2026-09-09 20:15:00'),
(1, 'admin', 'Nạp sinh viên', 'SINH_VIEN', 2000, NULL, 'Nạp 2.000 Sinh viên 4 khóa (K21 - K24)', '127.0.0.1', '2026-09-09 20:20:00'),
(1, 'admin', 'Đăng nhập', 'TAI_KHOAN', 1, NULL, 'Đăng nhập thành công từ IP 127.0.0.1', '127.0.0.1', '2026-09-09 20:25:00'),
(2, 'daotao', 'Đăng nhập', 'TAI_KHOAN', 2, NULL, 'Cán bộ đào tạo đăng nhập', '192.168.1.15', '2026-09-09 20:26:10'),
(2, 'daotao', 'Xếp lịch tự động', 'THOI_KHOA_BIEU', NULL, NULL, 'Chạy thuật toán xếp lịch tự động HK1 2025-2026', '192.168.1.15', '2026-09-09 20:30:15'),
(2, 'daotao', 'Thêm lịch TKB', 'THOI_KHOA_BIEU', 1, NULL, 'Xếp lịch D24CNPM1 môn INT1154 Thứ 2 Tiết 1-3 Phòng A1-201', '192.168.1.15', '2026-09-09 20:31:00'),
(2, 'daotao', 'Cập nhật TKB', 'THOI_KHOA_BIEU', 5, 'Phòng A1-201', 'Đổi sang Phòng LAB-301', '192.168.1.15', '2026-09-09 20:35:40'),
(1, 'admin', 'Cập nhật sinh viên', 'SINH_VIEN', 101, 'Email: old@ptit.edu.vn', 'Cập nhật email sv B21DCCN001: b21dccn001@stu.ptit.edu.vn', '127.0.0.1', '2026-09-09 20:40:12'),
(1, 'admin', 'Cập nhật giảng viên', 'GIANG_VIEN', 2, 'Học vị: Thạc sĩ', 'Cập nhật học vị GV0002 lên Tiến sĩ', '127.0.0.1', '2026-09-09 20:45:00'),
(3, 'GV0001', 'Đăng nhập', 'TAI_KHOAN', 3, NULL, 'Giảng viên GV0001 xem lịch dạy cá nhân', '192.168.1.20', '2026-09-09 20:50:00'),
(2, 'daotao', 'Xuất file Excel', 'THOI_KHOA_BIEU', NULL, NULL, 'Xuất file Excel TKB toàn trường năm học 2025-2026', '192.168.1.15', '2026-09-09 21:00:00'),
(1, 'admin', 'Thêm phòng học', 'PHONG_HOC', 21, NULL, 'Thêm mới phòng LAB-AI (45 máy)', '127.0.0.1', '2026-09-09 21:05:00'),
(1, 'admin', 'Đặt lại mật khẩu', 'TAI_KHOAN', 105, NULL, 'Đặt lại mật khẩu cho sinh viên B22DCCN005', '127.0.0.1', '2026-09-09 21:10:00'),
(2, 'daotao', 'Xử lý xung đột', 'THOI_KHOA_BIEU', 12, 'Trùng phòng A1-201 Tiết 1-3 Thứ 2', 'Đã chuyển lớp D23HTTT1 sang Phòng A2-301', '192.168.1.15', '2026-09-09 21:15:30'),
(508, 'B21DCCN001', 'Đăng nhập', 'TAI_KHOAN', 508, NULL, 'Sinh viên đăng nhập tra cứu TKB cá nhân', '171.244.10.5', '2026-09-09 21:20:00'),
(2, 'daotao', 'Thêm lịch TKB', 'THOI_KHOA_BIEU', 18, NULL, 'Xếp lịch D22ATTT1 môn INT1410 Thứ 4 Tiết 4-6 Phòng A2-302', '192.168.1.15', '2026-09-09 21:30:00'),
(2, 'daotao', 'Xóa lịch TKB', 'THOI_KHOA_BIEU', 99, 'TKB ID 99', 'Hủy lịch học bù do giảng viên công tác', '192.168.1.15', '2026-09-09 21:35:00'),
(1, 'admin', 'Sao lưu CSDL', 'HE_THONG', NULL, NULL, 'Sao lưu cơ sở dữ liệu quanly_tkb_cnj56 định kỳ', '127.0.0.1', '2026-09-09 21:40:00'),
(2, 'daotao', 'Cập nhật lớp học', 'LOP_HOC', 5, 'Sĩ số: 75', 'Cập nhật sĩ số lớp D22CNPM1 lên 80', '192.168.1.15', '2026-09-09 21:45:00'),
(1, 'admin', 'Cập nhật phân quyền', 'TAI_KHOAN', 2, 'Role: GIANG_VIEN', 'Nâng quyền tài khoản daotao thành DAO_TAO', '127.0.0.1', '2026-09-09 21:50:00'),
(2, 'daotao', 'Nhập sinh viên', 'SINH_VIEN', NULL, NULL, 'Kiểm tra danh sách sinh viên Khóa K24 trúng tuyển', '192.168.1.15', '2026-09-09 22:00:00'),
(1, 'admin', 'Kiểm tra an toàn', 'HE_THONG', NULL, NULL, 'Quét kiểm tra an toàn hệ thống và mã hóa mật khẩu SHA-256', '127.0.0.1', '2026-09-09 22:05:00'),
(2, 'daotao', 'Kiểm tra phòng học', 'PHONG_HOC', NULL, NULL, 'Kiểm tra công suất sử dụng phòng học tuần 37', '192.168.1.15', '2026-09-09 22:10:00'),
(1, 'admin', 'Xuất nhật ký', 'AUDIT_LOG', NULL, NULL, 'Xuất nhật ký kiểm toán hệ thống tháng 09/2026', '127.0.0.1', '2026-09-09 22:15:00');

