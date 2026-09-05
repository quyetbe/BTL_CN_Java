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
    `vai_tro` ENUM('ADMIN', 'NHANVIEN') NOT NULL DEFAULT 'NHANVIEN',
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

-- 1. Tài khoản (Mật khẩu băm SHA-256: 'admin123' -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9)
-- 'nhanvien123' -> 33230a1122a27ff0664db2a7d251f28fd452f36f6d2b5120a1dbbfb98ee9f166
-- '123456' -> 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
INSERT INTO `tai_khoan` (`id`, `ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `trang_thai`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Quản trị viên Hệ thống', 'admin@school.edu.vn', 'ADMIN', 1),
(2, 'daotao01', '33230a1122a27ff0664db2a7d251f28fd452f36f6d2b5120a1dbbfb98ee9f166', 'Nguyễn Thị Thu Hà', 'hadt@school.edu.vn', 'NHANVIEN', 1),
(3, 'daotao02', '33230a1122a27ff0664db2a7d251f28fd452f36f6d2b5120a1dbbfb98ee9f166', 'Trần Văn Mạnh', 'manhtv@school.edu.vn', 'NHANVIEN', 1),
(4, 'demo_lock', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Tài khoản Đã Khóa', 'lock@school.edu.vn', 'NHANVIEN', 0);

-- 2. Phòng học & Tài nguyên
INSERT INTO `phong_hoc` (`ma_phong`, `ten_phong`, `toa_nha`, `suc_chua`, `loai_phong`, `trang_thiet_bi`, `trang_thai`) VALUES
('A101', 'Phòng Lý thuyết A101', 'Tòa nhà A', 60, 'LY_THUYET', 'Máy chiếu EPSON, 2 Điều hòa Panasonic, Âm thanh micro không dây', 'DANG_SU_DUNG'),
('A102', 'Phòng Lý thuyết A102', 'Tòa nhà A', 50, 'LY_THUYET', 'Máy chiếu Sony, 2 Điều hòa Daikin, Bảng trượt', 'DANG_SU_DUNG'),
('A201', 'Phòng Lý thuyết A201', 'Tòa nhà A', 80, 'LY_THUYET', 'Máy chiếu, Hệ thống âm thanh vòm, 3 Điều hòa Daikin', 'DANG_SU_DUNG'),
('B201', 'Phòng Máy tính PM01', 'Tòa nhà B', 45, 'THUC_HANH', '45 Máy tính Core i7 16GB, Máy chiếu, Mạng LAN Gigabit, 2 Điều hòa', 'DANG_SU_DUNG'),
('B202', 'Phòng Máy tính PM02', 'Tòa nhà B', 40, 'THUC_HANH', '40 Máy tính Core i5 16GB, Máy chiếu, Điều hòa trung tâm', 'DANG_SU_DUNG'),
('B301', 'Lab Mạng & An toàn thông tin', 'Tòa nhà B', 35, 'THUC_HANH', '35 PC, Tủ Rack Cisco Router/Switch, Cáp quang thực hành', 'DANG_SU_DUNG'),
('C101', 'Hội trường lớn C101', 'Tòa nhà C', 200, 'HOI_TRUONG', 'Màn hình LED P3 300 inch, Âm thanh sân khấu chuyên nghiệp, Máy chiếu dự phòng', 'DANG_SU_DUNG'),
('C102', 'Phòng Hội thảo C102', 'Tòa nhà C', 90, 'HOI_TRUONG', 'Máy chiếu 4K, Hệ thống họp trực tuyến Polycom, Mic hội thảo', 'BAO_TRI');

-- 3. Giảng viên
INSERT INTO `giang_vien` (`ma_gv`, `ho_ten`, `khoa_bo_mon`, `email`, `so_dien_thoai`) VALUES
('GV001', 'TS. Nguyễn Văn An', 'Khoa Công nghệ thông tin', 'annv@school.edu.vn', '0912345678'),
('GV002', 'ThS. Lê Thị Bình', 'Khoa Công nghệ thông tin', 'binhlt@school.edu.vn', '0923456789'),
('GV003', 'PGS.TS. Phạm Quốc Cường', 'Bộ môn Khoa học máy tính', 'cuongpq@school.edu.vn', '0934567890'),
('GV004', 'ThS. Hoàng Thị Duyên', 'Bộ môn Kỹ thuật phần mềm', 'duyenht@school.edu.vn', '0945678901'),
('GV005', 'TS. Đỗ Minh Đức', 'Bộ môn Mạng & Hệ thống', 'ducdm@school.edu.vn', '0956789012'),
('GV006', 'ThS. Vũ Thùy Linh', 'Bộ môn Toán - Tin ứng dụng', 'linhvt@school.edu.vn', '0967890123');

-- 4. Môn học
INSERT INTO `mon_hoc` (`ma_mon`, `ten_mon`, `so_tin_chi`, `loai_mon`) VALUES
('INT1001', 'Lập trình hướng đối tượng Java', 3, 'LY_THUYET'),
('INT1002', 'Thực hành Lập trình Java', 2, 'THUC_HANH'),
('INT1003', 'Cơ sở dữ liệu & SQL', 3, 'LY_THUYET'),
('INT1004', 'Thực hành Hệ quản trị CSDL', 2, 'THUC_HANH'),
('INT1005', 'Cấu trúc dữ liệu & Giải thuật', 4, 'LY_THUYET'),
('INT1006', 'Mạng máy tính & Truyền thông', 3, 'LY_THUYET'),
('INT1007', 'Thực hành Mạng máy tính', 2, 'THUC_HANH'),
('MAT1001', 'Giải tích và Đại số tuyến tính', 4, 'LY_THUYET');

-- 5. Lớp học
INSERT INTO `lop_hoc` (`ma_lop`, `ten_lop`, `si_so`, `khoa_hoc`) VALUES
('D21CNPM01', 'Đại học Kỹ thuật Phần mềm 1 - K21', 45, '2021-2025'),
('D21CNPM02', 'Đại học Kỹ thuật Phần mềm 2 - K21', 40, '2021-2025'),
('D22CNTT01', 'Đại học Công nghệ Thông tin 1 - K22', 55, '2022-2026'),
('D22CNTT02', 'Đại học Công nghệ Thông tin 2 - K22', 50, '2022-2026'),
('D23KHMT01', 'Đại học Khoa học Máy tính 1 - K23', 38, '2023-2027'),
('D23ATTT01', 'Đại học An toàn Thông tin 1 - K23', 35, '2023-2027');

-- 6. Thời khóa biểu (Học kỳ 1 - Năm học 2025-2026)
-- Các lịch mẫu không xung đột:
INSERT INTO `thoi_khoa_bieu` (`id`, `ma_mon`, `ma_lop`, `ma_gv`, `ma_phong`, `thu_trong_tuan`, `tiet_bat_dau`, `so_tiet`, `tiet_ket_thuc`, `tuan_bat_dau`, `tuan_ket_thuc`, `hoc_ky`, `nam_hoc`, `ghi_chu`) VALUES
-- Thứ Hai: Tiết 1-3 Lớp D21CNPM01 học Java LT tại A101 bởi GV001
(1, 'INT1001', 'D21CNPM01', 'GV001', 'A101', 2, 1, 3, 3, 1, 15, 'HK1', '2025-2026', 'Lý thuyết Java cơ bản'),
-- Thứ Hai: Tiết 4-6 Lớp D21CNPM02 học Java LT tại A101 bởi GV001 (liền kề, không trùng tiết vì kết thúc 3, bắt đầu 4)
(2, 'INT1001', 'D21CNPM02', 'GV001', 'A101', 2, 4, 3, 6, 1, 15, 'HK1', '2025-2026', 'Lý thuyết Java nâng cao'),
-- Thứ Ba: Tiết 1-4 Lớp D21CNPM01 thực hành Java tại B201 bởi GV002
(3, 'INT1002', 'D21CNPM01', 'GV002', 'B201', 3, 1, 4, 4, 1, 15, 'HK1', '2025-2026', 'Thực hành Swing & JDBC'),
-- Thứ Ba: Tiết 7-10 Lớp D21CNPM02 thực hành Java tại B201 bởi GV002 (ca chiều)
(4, 'INT1002', 'D21CNPM02', 'GV002', 'B201', 3, 7, 4, 10, 1, 15, 'HK1', '2025-2026', 'Thực hành Swing & JDBC ca chiều'),
-- Thứ Tư: Tiết 1-3 Lớp D22CNTT01 học CSDL tại A201 bởi GV003
(5, 'INT1003', 'D22CNTT01', 'GV003', 'A201', 4, 1, 3, 3, 1, 15, 'HK1', '2025-2026', 'Lý thuyết mô hình quan hệ'),
-- Thứ Tư: Tiết 7-9 Lớp D22CNTT02 học CSDL tại A201 bởi GV003
(6, 'INT1003', 'D22CNTT02', 'GV003', 'A201', 4, 7, 3, 9, 1, 15, 'HK1', '2025-2026', 'Lý thuyết SQL & Index'),
-- Thứ Năm: Tiết 1-4 Lớp D22CNTT01 thực hành CSDL tại B202 bởi GV004
(7, 'INT1004', 'D22CNTT01', 'GV004', 'B202', 5, 1, 4, 4, 1, 15, 'HK1', '2025-2026', 'Cài đặt và truy vấn MySQL'),
-- Thứ Năm: Tiết 7-10 Lớp D23ATTT01 thực hành Mạng tại B301 bởi GV005
(8, 'INT1007', 'D23ATTT01', 'GV005', 'B301', 5, 7, 4, 10, 1, 15, 'HK1', '2025-2026', 'Cấu hình VLAN Switch'),
-- Thứ Sáu: Tiết 1-4 Lớp D23KHMT01 học Giải tích tại A102 bởi GV006
(9, 'MAT1001', 'D23KHMT01', 'GV006', 'A102', 6, 1, 4, 4, 1, 15, 'HK1', '2025-2026', 'Đại số tuyến tính'),
-- Thứ Bảy: Tiết 1-4 Hội thảo chuyên đề CNTT tại C101 cho D21CNPM01 & D21CNPM02 bởi GV003
(10, 'INT1005', 'D21CNPM01', 'GV003', 'C101', 7, 1, 4, 4, 1, 8, 'HK1', '2025-2026', 'Hội thảo thuật toán phân tán (Tuần 1-8)');
