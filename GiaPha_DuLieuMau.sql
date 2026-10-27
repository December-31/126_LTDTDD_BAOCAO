-- Du lieu mau cho database GiaPha. Chay SAU file tao bang (GiaPha_SQLServer.sql), chay 1 lan.
-- File luu UTF-8 co BOM. Neu SSMS hien sai tieng Viet: File > Open > chon "Unicode (UTF-8)".

USE GiaPha;
GO

SET QUOTED_IDENTIFIER ON;
GO

SET NOCOUNT ON;
GO

-- 1. TaiKhoan
SET IDENTITY_INSERT TaiKhoan ON;
INSERT INTO TaiKhoan (id_TaiKhoan, TenHienThi, SDT, Email, MatKhau, LoaiDangNhap, TrangThaiTaiKhoan, LoaiTaiKhoan, NgayTao, LanDangNhapCuoi) VALUES
(1, N'Nguyễn Thị Lan', '0905222444', 'lan.nguyen@gmail.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-02-28T09:10:00', '2026-10-26T20:05:00'),
(2, N'Nguyễn Văn Nam', '0912111222', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-01-15T10:00:00', '2026-10-20T07:30:00'),
(3, N'Nguyễn Minh Khôi', '0912345678', 'minhkhoi.nguyen@gmail.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-03-12T08:30:00', '2026-10-04T08:15:00'),
(4, N'Thảo Nguyễn', '0944321654', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2024-11-05T19:45:00', '2026-10-24T21:40:00'),
(5, N'Lê Thị Hạnh', '0977123456', 'hanh.le@gmail.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2024-11-18T07:55:00', '2026-10-26T18:20:00'),
(6, N'Trần Thị Mai', '0922888999', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2024-10-25T15:00:00', '2026-09-30T06:45:00'),
(7, N'Chú Bình', '0987654321', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-01-03T11:20:00', '2026-09-01T08:00:00'),
(8, N'Phạm Thị Hương', '0933456789', 'huong.pham@gmail.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2024-12-20T13:05:00', '2026-10-04T14:15:00'),
(9, N'Phan Thị Kim Anh', '0988111333', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-06-01T09:00:00', '2026-09-18T10:00:00'),
(10, N'Võ Văn Tâm', '0966777888', NULL, NULL, N'SDT_OTP', N'DaKhoa', N'Thuong', '2024-12-02T16:30:00', '2026-09-20T19:42:00'),
(11, N'Quản trị hệ thống', '0901000001', 'admin@giapha.app', NULL, N'SDT_OTP', N'HoatDong', N'AdminApp', '2024-12-01T08:00:00', '2026-10-25T09:00:00'),
(12, N'Lê Văn Hiếu', '0914555666', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-01-20T10:15:00', '2026-10-10T20:00:00'),
(13, N'Hồ Thị Thu', '0915666777', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-06-05T14:00:00', '2026-10-22T12:10:00'),
(14, N'Phan Đức Lộc', '0916777888', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-09-29T17:00:00', '2026-09-30T09:12:00'),
(15, N'Bình Mai', '0917888999', 'binh.mai@gmail.com', N'$2b$10$9CtDjtlwUVMeFH10AsN9rehPnxHQBre1kqNonS8B0xWbkzstsBmT6', N'MatKhau', N'HoatDong', N'Thuong', '2025-06-28T08:40:00', '2026-10-15T19:30:00'),
(16, N'Đinh Công Minh', '0920100001', 'user01@example.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-01-01T08:00:00', '2026-07-01T09:00:00'),
(17, N'Bùi Thị Thanh Hà', '0920100038', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-06-04T09:11:00', '2026-08-03T10:07:00'),
(18, N'Hoàng Văn Long', '0920100075', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-11-07T10:22:00', '2026-09-05T11:14:00'),
(19, N'Dương Thị Mỹ Linh', '0920100112', 'user04@example.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-04-10T11:33:00', NULL),
(20, N'Lý Quốc Hưng', '0920100149', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-09-13T12:44:00', '2026-07-09T13:28:00'),
(21, N'Tô Ngọc Hân', '0920100186', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-02-16T13:55:00', '2026-08-11T14:35:00'),
(22, N'Huỳnh Gia Bảo', '0920100223', 'user07@example.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-07-19T14:06:00', '2026-09-13T15:42:00'),
(23, N'Mai Thanh Tùng', '0920100260', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-12-22T15:17:00', NULL),
(24, N'Cao Thị Bích Ngọc', '0920100297', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2025-05-25T16:28:00', '2026-07-17T17:56:00'),
(25, N'Đoàn Minh Trí', '0920100334', 'user10@example.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-10-01T17:39:00', '2026-08-19T09:03:00'),
(26, N'Trịnh Hải Yến', '0920100371', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-03-04T08:50:00', '2026-09-21T10:10:00'),
(27, N'Vương Đình Khoa', '0920100408', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-08-07T09:01:00', NULL),
(28, N'Lâm Thị Kiều Oanh', '0920100445', 'user13@example.com', NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-01-10T10:12:00', '2026-07-25T12:24:00'),
(29, N'Châu Anh Tuấn', '0920100482', NULL, NULL, N'SDT_OTP', N'HoatDong', N'Thuong', '2026-06-13T11:23:00', '2026-08-27T13:31:00'),
(30, N'Phùng Thế Vinh', '0920100519', NULL, NULL, N'SDT_OTP', N'DaKhoa', N'Thuong', '2026-11-16T12:34:00', '2026-09-02T14:38:00');
SET IDENTITY_INSERT TaiKhoan OFF;
GO

-- 2. MaXacThuc
SET IDENTITY_INSERT MaXacThuc ON;
INSERT INTO MaXacThuc (id, SDT, Ma, NgayTao, HetHan, DaSuDung) VALUES
(1, '0912345678', '482915', GETDATE(), DATEADD(MINUTE, 5, GETDATE()), 0),
(2, '0905222444', '731024', '2026-10-26T20:00:00', '2026-10-26T20:05:00', 1),
(3, '0987654321', '905837', '2026-09-01T07:50:00', '2026-09-01T07:55:00', 0);
SET IDENTITY_INSERT MaXacThuc OFF;
GO

-- 3. KhongGian
SET IDENTITY_INSERT KhongGian ON;
INSERT INTO KhongGian (id_KhongGian, TenKhongGian, id_NguoiTao, NgayTao, CheDoRiengTu) VALUES
(1, N'Gia phả họ Nguyễn', 1, '2025-03-12T09:00:00', 0),
(2, N'Gia phả họ Trần', 3, '2025-04-02T10:30:00', 1),
(3, N'Dòng họ Lê ở Đà Nẵng', 5, '2025-01-15T08:45:00', 0),
(4, N'Gia phả họ Phạm', 8, '2025-01-03T14:10:00', 1);
SET IDENTITY_INSERT KhongGian OFF;
GO

-- 4. OThanhVien
SET IDENTITY_INSERT OThanhVien ON;
INSERT INTO OThanhVien (id_OThanhVien, id_KhongGian, HoTen, BietDanh, GioiTinh, NgaySinh, QueQuan, ConSong, NgayMat, CCCD, TrangThaiLienKet, LaTruongToc) VALUES
(1, 1, N'Nguyễn Văn Tín', NULL, N'Nam', NULL, N'Hội An, Quảng Nam', 0, '1998-09-05', NULL, N'DangNoi', 0),
(2, 1, N'Phạm Thị Bình', NULL, N'Nu', '1925-08-09', N'Điện Bàn, Quảng Nam', 0, '2005-07-06', NULL, N'DangNoi', 0),
(3, 1, N'Nguyễn Văn Hòa', NULL, N'Nam', '1946-02-21', N'Hội An, Quảng Nam', 0, '2019-02-11', NULL, N'DangNoi', 1),
(4, 1, N'Trần Thị Mai', NULL, N'Nu', '1948-11-21', N'Hội An, Quảng Nam', 1, NULL, '048048009911', N'DangNoi', 0),
(5, 1, N'Nguyễn Văn Phúc', NULL, N'Nam', '1950-04-10', N'Hội An, Quảng Nam', 0, '2021-08-09', NULL, N'DangNoi', 0),
(6, 1, N'Đặng Thị Sen', NULL, N'Nu', '1953-04-12', N'Duy Xuyên, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(7, 1, N'Võ Thị Cúc', NULL, N'Nu', '1958-06-08', N'Thăng Bình, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(8, 1, N'Nguyễn Văn Nam', N'Anh Cả', N'Nam', '1968-08-12', N'Hội An, Quảng Nam', 1, NULL, '048068012345', N'DangNoi', 0),
(9, 1, N'Lê Thị Hạnh', NULL, N'Nu', '1970-04-27', N'Đà Nẵng', 1, NULL, '048070001122', N'DangNoi', 0),
(10, 1, N'Nguyễn Thị Lan', NULL, N'Nu', '1972-09-07', N'Hội An, Quảng Nam', 1, NULL, '048072003456', N'DangNoi', 0),
(11, 1, N'Phạm Văn Hải', NULL, N'Nam', '1969-06-13', N'Tam Kỳ, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(12, 1, N'Nguyễn Văn Bảo', NULL, N'Nam', '1977-10-06', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(13, 1, N'Nguyễn Công Danh', NULL, N'Nam', '1986-11-02', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(14, 1, N'Trương Thị Ngọc', NULL, N'Nu', '1989-09-27', N'Đại Lộc, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(15, 1, N'Nguyễn Công Đạt', NULL, N'Nam', '1988-12-24', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(16, 1, N'Ngô Thị Hà', NULL, N'Nu', '1990-11-14', N'Núi Thành, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(17, 1, N'Võ Minh Tuấn', NULL, N'Nam', '1980-02-06', N'Thăng Bình, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(18, 1, N'Nguyễn Minh Khôi', NULL, N'Nam', '1995-06-24', N'Hội An, Quảng Nam', 1, NULL, '048095007890', N'DangNoi', 0),
(19, 1, N'Nguyễn Thị Thảo', NULL, N'Nu', '1998-01-11', N'Hội An, Quảng Nam', 1, NULL, '048098004321', N'DangNoi', 0),
(20, 1, N'Võ Thị An', NULL, N'Nu', '1997-07-16', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(21, 1, N'Nguyễn Gia Hân', NULL, N'Nu', '2024-10-01', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(22, 1, N'Phạm Gia Bảo', NULL, N'Nam', '1998-07-02', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(23, 1, N'Phạm Thị Mỹ', NULL, N'Nu', '2008-08-10', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(24, 1, N'Nguyễn Minh Nghĩa', NULL, N'Nam', '2014-01-14', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(25, 1, N'Nguyễn Thị Thư', NULL, N'Nu', '2017-04-05', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(26, 1, N'Nguyễn Minh Khải', NULL, N'Nam', '2015-09-26', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(27, 1, N'Nguyễn Thị Trân', NULL, N'Nu', '2018-01-20', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(28, 1, N'Nguyễn Minh Anh', NULL, N'Nu', '2020-01-06', N'Hội An, Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(29, 1, N'Võ Văn Tâm', NULL, N'Nam', '1962-04-15', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(30, 1, N'Đỗ Thị Lụa', NULL, N'Nu', '1965-07-15', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(31, 1, N'Võ Minh Quân', NULL, N'Nam', '1990-08-02', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(32, 2, N'Trần Văn Lộc', NULL, N'Nam', '1955-11-14', N'Huế', 1, NULL, NULL, N'DangNoi', 1),
(33, 2, N'Ngô Thị Tâm', NULL, N'Nu', '1958-05-23', N'Huế', 1, NULL, NULL, N'DangNoi', 0),
(34, 2, N'Trần Văn Bình', NULL, N'Nam', '1985-01-08', N'Huế', 1, NULL, NULL, N'DangNoi', 0),
(35, 2, N'Trần Thị Hương', NULL, N'Nu', '1988-04-05', N'Huế', 1, NULL, NULL, N'DangNoi', 0),
(36, 2, N'Trần Văn Sáu', NULL, N'Nam', '1960-01-26', N'Quảng Trị', 1, NULL, NULL, N'DangNoi', 0),
(37, 2, N'Lý Thị Đào', NULL, N'Nu', '1963-06-04', N'Quảng Trị', 1, NULL, NULL, N'DangNoi', 0),
(38, 2, N'Trần Văn Bình', NULL, N'Nam', '1990-04-10', N'Quảng Trị', 1, NULL, NULL, N'DangNoi', 0),
(39, 3, N'Lê Văn Cảnh', NULL, N'Nam', '1920-02-28', N'Quảng Nam', 0, '1995-12-01', NULL, N'DangNoi', 1),
(40, 3, N'Trần Thị Sáu', NULL, N'Nu', NULL, N'Quảng Nam', 0, '2001-09-17', NULL, N'DangNoi', 0),
(41, 3, N'Lê Văn Đại', N'Ông Tư', N'Nam', '1945-07-24', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(42, 3, N'Lê Văn Trung', NULL, N'Nam', '1948-04-15', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(43, 3, N'Lê Thị Nga', NULL, N'Nu', '1951-03-19', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(44, 3, N'Phan Thị Hồng', NULL, N'Nu', '1947-07-05', N'Quảng Nam', 0, '1972-06-14', NULL, N'DangNoi', 0),
(45, 3, N'Đỗ Thị Lan', NULL, N'Nu', '1950-08-08', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(46, 3, N'Võ Thị Hoa', NULL, N'Nu', '1954-08-15', N'Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(47, 3, N'Bùi Thị Mận', NULL, N'Nu', '1960-09-20', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(48, 3, N'Hoàng Thị Lý', NULL, N'Nu', '1952-03-06', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(49, 3, N'Hồ Văn Sơn', NULL, N'Nam', '1949-11-08', N'Huế', 1, NULL, NULL, N'DangNoi', 0),
(50, 3, N'Mai Văn Tâm', NULL, N'Nam', '1947-04-05', N'Quảng Bình', 1, NULL, NULL, N'DangNoi', 0),
(51, 3, N'Lê Văn An', NULL, N'Nam', '1968-07-21', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(52, 3, N'Lê Thị Bích', NULL, N'Nu', '1971-12-14', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(53, 3, N'Lê Văn Cần', NULL, N'Nam', '1976-06-07', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(54, 3, N'Lê Thị Dung', NULL, N'Nu', '1982-06-15', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(55, 3, N'Võ Minh Hải', NULL, N'Nam', '1977-03-23', N'Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(56, 3, N'Lê Văn Em', NULL, N'Nam', '1990-07-11', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(57, 3, N'Lê Thị Phượng', NULL, N'Nu', '1993-09-11', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(58, 3, N'Lê Văn Nghĩa', NULL, N'Nam', '2000-03-17', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(59, 3, N'Lê Văn Hiếu', NULL, N'Nam', '1975-11-19', N'Đà Nẵng', 1, NULL, '048075005566', N'DangNoi', 0),
(60, 3, N'Lê Thị Kim', NULL, N'Nu', '1978-04-25', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(61, 3, N'Lê Thị Hạnh', NULL, N'Nu', '1970-08-16', N'Đà Nẵng', 1, NULL, '048070001122', N'DangNoi', 0),
(62, 3, N'Hồ Thị Thu', NULL, N'Nu', '1973-02-07', N'Huế', 1, NULL, NULL, N'DangNoi', 0),
(63, 3, N'Mai Văn Bình', NULL, N'Nam', '1980-09-28', N'Quảng Bình', 1, NULL, NULL, N'DangNoi', 0),
(64, 3, N'Đinh Thị Yến', NULL, N'Nu', '1977-07-04', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(65, 3, N'Trương Văn Phong', NULL, N'Nam', '1980-07-09', N'Quảng Nam', 1, NULL, NULL, N'DangNoi', 0),
(66, 3, N'Lý Thị Hà', NULL, N'Nu', '1982-07-10', N'Quảng Bình', 1, NULL, NULL, N'DangNoi', 0),
(67, 3, N'Châu Thị Nga', NULL, N'Nu', '1992-06-26', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(68, 3, N'Lê Minh Tú', NULL, N'Nam', '2000-10-07', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(69, 3, N'Lê Văn Khải', NULL, N'Nam', '2003-10-26', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(70, 3, N'Lê Thị Lam', NULL, N'Nu', '2006-05-23', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(71, 3, N'Trương Thị Vy', NULL, N'Nu', '2010-07-26', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(72, 3, N'Mai Văn Đạt', NULL, N'Nam', '2012-05-11', N'Quảng Bình', 1, NULL, NULL, N'DangNoi', 0),
(73, 3, N'Lê Văn Phát', NULL, N'Nam', '2014-06-15', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(74, 3, N'Lê Văn Khoa', NULL, N'Nam', '2017-01-06', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(75, 3, N'Lê Thị Ánh', NULL, N'Nu', '2014-10-22', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(76, 3, N'Ngô Thị Thanh', NULL, N'Nu', '2001-07-15', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(77, 3, N'Lê Hoàng Long', NULL, N'Nam', '2024-10-14', N'Đà Nẵng', 1, NULL, NULL, N'DangNoi', 0),
(78, 4, N'Phạm Văn Đức', NULL, N'Nam', '1940-08-21', N'Quảng Ngãi', 0, '2015-10-03', NULL, N'DangNoi', 1),
(79, 4, N'Võ Thị Lài', NULL, N'Nu', NULL, N'Quảng Ngãi', 0, '2020-09-08', NULL, N'DangNoi', 0),
(80, 4, N'Phạm Văn Hải', NULL, N'Nam', '1969-06-17', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(81, 4, N'Phạm Thị Hương', NULL, N'Nu', '1975-04-15', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(82, 4, N'Phạm Văn Thắng', NULL, N'Nam', '1972-03-20', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(83, 4, N'Đặng Thị Oanh', NULL, N'Nu', '1974-12-22', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0),
(84, 4, N'Phạm Gia Huy', NULL, N'Nam', '2000-04-21', N'Quảng Ngãi', 1, NULL, NULL, N'DangNoi', 0);
SET IDENTITY_INSERT OThanhVien OFF;
GO

-- 5. ThanVienTrongKGian
SET IDENTITY_INSERT ThanVienTrongKGian ON;
INSERT INTO ThanVienTrongKGian (id_ThanVienTrongKGian, id_KhongGian, id_TaiKhoan, VaiTro, id_OThanhVien, TrangThai, NgayThamGia, NgayCam, LyDoCam, id_NguoiCam) VALUES
(1, 1, 1, N'Admin', 10, N'HoatDong', '2025-03-12T09:00:00', NULL, NULL, NULL),
(2, 1, 2, N'QuanLy', 8, N'HoatDong', '2025-03-14T19:30:00', NULL, NULL, NULL),
(3, 1, 3, N'ThanhVien', 18, N'HoatDong', '2025-03-20T20:00:00', NULL, NULL, NULL),
(4, 1, 4, N'ThanhVien', 19, N'HoatDong', '2025-03-21T21:15:00', NULL, NULL, NULL),
(5, 1, 5, N'ThanhVien', 9, N'HoatDong', '2025-03-22T08:05:00', NULL, NULL, NULL),
(6, 1, 6, N'ThanhVien', 4, N'HoatDong', '2025-03-25T09:30:00', NULL, NULL, NULL),
(7, 1, 7, N'ThanhVienHo', NULL, N'HoatDong', '2025-04-02T10:00:00', NULL, NULL, NULL),
(8, 1, 8, N'KhachTam', NULL, N'HoatDong', '2026-10-04T14:20:00', NULL, NULL, NULL),
(9, 1, 9, N'KhachTam', NULL, N'DaRoi', '2026-09-18T10:05:00', NULL, NULL, NULL),
(10, 1, 10, N'ThanhVien', 29, N'BiCam', '2025-04-10T08:00:00', '2026-08-15T21:30:00', N'Tự ý sửa thông tin của nhiều thành viên khi chưa được phép', 1),
(11, 2, 3, N'Admin', NULL, N'HoatDong', '2025-04-02T10:30:00', NULL, NULL, NULL),
(12, 2, 1, N'ThanhVienHo', NULL, N'HoatDong', '2025-04-20T16:00:00', NULL, NULL, NULL),
(13, 3, 5, N'Admin', 61, N'HoatDong', '2025-01-15T08:45:00', NULL, NULL, NULL),
(14, 3, 3, N'QuanLy', NULL, N'HoatDong', '2025-05-08T09:00:00', NULL, NULL, NULL),
(15, 3, 12, N'ThanhVien', 59, N'HoatDong', '2025-02-01T10:00:00', NULL, NULL, NULL),
(16, 3, 13, N'ThanhVienHo', NULL, N'HoatDong', '2025-06-12T14:30:00', NULL, NULL, NULL),
(17, 3, 14, N'KhachTam', NULL, N'HoatDong', '2026-09-30T09:12:00', NULL, NULL, NULL),
(18, 3, 15, N'ThanhVien', 63, N'HoatDong', '2025-07-01T08:30:00', NULL, NULL, NULL),
(19, 4, 8, N'Admin', 81, N'HoatDong', '2025-01-03T14:10:00', NULL, NULL, NULL),
(20, 4, 3, N'ThanhVienHo', NULL, N'HoatDong', '2025-05-20T11:00:00', NULL, NULL, NULL);
SET IDENTITY_INSERT ThanVienTrongKGian OFF;
GO

-- 6. QuanHeChaMe
INSERT INTO QuanHeChaMe (id_con, id_PhuHuynh, loaiQheVoiCon, NgayXacLap, HienThiLienKet) VALUES
(3, 1, N'Ruot', '1946-09-02', 1),
(3, 2, N'Ruot', '1946-09-02', 1),
(5, 1, N'Ruot', '1950-11-30', 1),
(5, 2, N'Ruot', '1950-11-30', 1),
(8, 3, N'Ruot', '1968-08-12', 1),
(8, 4, N'Ruot', '1968-08-12', 1),
(10, 3, N'Ruot', '1972-09-07', 1),
(10, 4, N'Ruot', '1972-09-07', 1),
(12, 5, N'Ruot', '1977-10-06', 1),
(12, 6, N'Ruot', '1977-10-06', 1),
(13, 5, N'Ruot', '1986-11-02', 1),
(13, 7, N'Ruot', '1986-11-02', 1),
(15, 5, N'Ruot', '1988-12-24', 0),
(15, 7, N'Ruot', '1988-12-24', 0),
(17, 7, N'Ruot', '1980-02-06', 1),
(17, 5, N'Ke', '1985-06-20', 1),
(18, 8, N'Ruot', '1995-06-24', 1),
(18, 9, N'Ruot', '1995-06-24', 1),
(19, 8, N'Ruot', '1998-01-11', 1),
(19, 9, N'Ruot', '1998-01-11', 1),
(22, 10, N'Ruot', '1998-07-02', 1),
(22, 11, N'Ruot', '1998-07-02', 1),
(23, 10, N'Nuoi', '2010-05-10', 1),
(24, 13, N'Ruot', '2014-01-14', 1),
(24, 14, N'Ruot', '2014-01-14', 1),
(25, 13, N'Ruot', '2017-04-05', 1),
(25, 14, N'Ruot', '2017-04-05', 1),
(26, 15, N'Ruot', '2015-09-26', 1),
(26, 16, N'Ruot', '2015-09-26', 1),
(27, 15, N'Ruot', '2018-01-20', 1),
(27, 16, N'Ruot', '2018-01-20', 1),
(28, 15, N'Ruot', '2020-01-06', 1),
(28, 16, N'Ruot', '2020-01-06', 1),
(21, 18, N'Ruot', '2024-10-01', 1),
(21, 20, N'Ruot', '2024-10-01', 1),
(31, 29, N'Ruot', '1990-08-02', 1),
(31, 30, N'Ruot', '1990-08-02', 1),
(34, 32, N'Ruot', '1985-01-08', 1),
(34, 33, N'Ruot', '1985-01-08', 1),
(35, 32, N'Ruot', '1988-04-05', 1),
(35, 33, N'Ruot', '1988-04-05', 1),
(38, 36, N'Ruot', '1990-04-10', 1),
(38, 37, N'Ruot', '1990-04-10', 1),
(41, 39, N'Ruot', '1945-07-24', 1),
(41, 40, N'Ruot', '1945-07-24', 1),
(42, 39, N'Ruot', '1948-04-15', 1),
(42, 40, N'Ruot', '1948-04-15', 1),
(43, 39, N'Ruot', '1951-03-19', 1),
(43, 40, N'Ruot', '1951-03-19', 1),
(51, 41, N'Ruot', '1968-07-21', 1),
(51, 44, N'Ruot', '1968-07-21', 1),
(52, 41, N'Ruot', '1971-12-14', 1),
(52, 44, N'Ruot', '1971-12-14', 1),
(53, 41, N'Ruot', '1976-06-07', 1),
(53, 45, N'Ruot', '1976-06-07', 1),
(54, 41, N'Ruot', '1982-06-15', 1),
(54, 46, N'Ruot', '1982-06-15', 1),
(55, 46, N'Ruot', '1977-03-23', 1),
(55, 41, N'Ke', '1981-03-14', 1),
(56, 41, N'Ruot', '1990-07-11', 1),
(56, 47, N'Ruot', '1990-07-11', 1),
(57, 41, N'Ruot', '1993-09-11', 1),
(57, 47, N'Ruot', '1993-09-11', 1),
(58, 41, N'Nuoi', '2002-06-15', 1),
(58, 47, N'Nuoi', '2002-06-15', 1),
(59, 42, N'Ruot', '1975-11-19', 1),
(59, 48, N'Ruot', '1975-11-19', 1),
(60, 42, N'Ruot', '1978-04-25', 1),
(60, 48, N'Ruot', '1978-04-25', 1),
(61, 42, N'Ruot', '1970-08-16', 1),
(61, 48, N'Ruot', '1970-08-16', 1),
(62, 43, N'Ruot', '1973-02-07', 1),
(62, 49, N'Ruot', '1973-02-07', 1),
(63, 43, N'Ruot', '1980-09-28', 0),
(63, 50, N'Ruot', '1980-09-28', 0),
(68, 51, N'Ruot', '2000-10-07', 1),
(68, 60, N'Ruot', '2000-10-07', 1),
(69, 59, N'Ruot', '2003-10-26', 1),
(69, 64, N'Ruot', '2003-10-26', 1),
(70, 59, N'Ruot', '2006-05-23', 1),
(70, 64, N'Ruot', '2006-05-23', 1),
(71, 54, N'Ruot', '2010-07-26', 1),
(71, 65, N'Ruot', '2010-07-26', 1),
(72, 63, N'Ruot', '2012-05-11', 1),
(72, 66, N'Ruot', '2012-05-11', 1),
(73, 56, N'Ruot', '2014-06-15', 1),
(73, 67, N'Ruot', '2014-06-15', 1),
(74, 56, N'Ruot', '2017-01-06', 1),
(74, 67, N'Ruot', '2017-01-06', 1),
(75, 53, N'Ruot', '2014-10-22', 1),
(75, 57, N'Ruot', '2014-10-22', 1),
(77, 68, N'Ruot', '2024-10-14', 1),
(77, 76, N'Ruot', '2024-10-14', 1),
(80, 78, N'Ruot', '1969-06-17', 1),
(80, 79, N'Ruot', '1969-06-17', 1),
(81, 78, N'Ruot', '1975-04-15', 1),
(81, 79, N'Ruot', '1975-04-15', 1),
(82, 78, N'Ruot', '1972-03-20', 1),
(82, 79, N'Ruot', '1972-03-20', 1),
(84, 82, N'Ruot', '2000-04-21', 1),
(84, 83, N'Ruot', '2000-04-21', 1);
GO

-- 7. QHeHonNhan
SET IDENTITY_INSERT QHeHonNhan ON;
INSERT INTO QHeHonNhan (Id, Id_Chong, Id_Vo, NgayCoi, NgayLyHon, TrangThai, ThuTuHonNhan, HienThiLienKet) VALUES
(1, 1, 2, '1944-09-28', NULL, N'Goa', 1, 1),
(2, 3, 4, '1966-02-17', NULL, N'Goa', 1, 1),
(3, 5, 6, '1975-01-05', '1983-02-19', N'DaLyHon', 1, 1),
(4, 5, 7, '1985-08-03', NULL, N'Goa', 2, 1),
(5, 8, 9, '1993-02-02', NULL, N'DangKetHon', 1, 1),
(6, 11, 10, '1996-12-08', NULL, N'DangKetHon', 1, 1),
(7, 13, 14, '2012-06-28', NULL, N'DangKetHon', 1, 1),
(8, 15, 16, '2013-10-26', NULL, N'DangKetHon', 1, 1),
(9, 18, 20, '2022-11-20', NULL, N'DangKetHon', 1, 1),
(10, 29, 30, '1988-04-20', NULL, N'DangKetHon', 1, 1),
(11, 32, 33, '1983-04-02', NULL, N'DangKetHon', 1, 1),
(12, 36, 37, '1988-09-28', NULL, N'DangKetHon', 1, 1),
(13, 39, 40, '1943-05-22', NULL, N'Goa', 1, 1),
(14, 41, 44, '1966-12-08', NULL, N'Goa', 1, 1),
(15, 41, 45, '1974-11-27', '1980-07-18', N'DaLyHon', 2, 1),
(16, 41, 46, '1981-01-27', '1987-04-05', N'DaLyHon', 3, 1),
(17, 41, 47, '1988-06-14', NULL, N'DangKetHon', 4, 1),
(18, 42, 48, '1969-05-09', NULL, N'DangKetHon', 1, 1),
(19, 49, 43, '1971-06-21', '1976-11-12', N'DaLyHon', 1, 1),
(20, 50, 43, '1978-06-02', NULL, N'DangKetHon', 2, 1),
(21, 51, 60, '1999-09-11', NULL, N'DangKetHon', 1, 1),
(22, 53, 57, '2012-10-19', NULL, N'DangKetHon', 1, 1),
(23, 55, 52, '1999-06-27', NULL, N'DangKetHon', 1, 1),
(24, 59, 64, '2001-06-14', NULL, N'DangKetHon', 1, 1),
(25, 65, 54, '2008-11-08', NULL, N'DangKetHon', 1, 1),
(26, 63, 66, '2010-09-27', NULL, N'DangKetHon', 1, 1),
(27, 56, 67, '2012-11-04', NULL, N'DangKetHon', 1, 1),
(28, 68, 76, '2022-01-15', NULL, N'DangKetHon', 1, 1),
(29, 78, 79, '1967-05-20', NULL, N'Goa', 1, 1),
(30, 82, 83, '1998-03-22', NULL, N'DangKetHon', 1, 1);
SET IDENTITY_INSERT QHeHonNhan OFF;
GO

-- 8. Avatar
SET IDENTITY_INSERT Avatar ON;
INSERT INTO Avatar (id_Avatar, id_OThanhVien, DuongDanAnh, LoaiKhung, HieuUng, TrangThai, NgayTao) VALUES
(1, 8, N'avatars/nam_20250910.jpg', N'Tron', N'Goc', N'DangDung', '2025-09-10T09:00:00'),
(2, 8, N'avatars/nam_20240615.jpg', N'Tron', N'Goc', N'LichSu', '2024-06-15T09:00:00'),
(3, 8, N'avatars/nam_20230202.jpg', N'VuongBoGoc', N'CoDien', N'LichSu', '2023-02-02T09:00:00'),
(4, 8, N'avatars/nam_20211020.jpg', N'Khong', N'Goc', N'LichSu', '2021-10-20T09:00:00'),
(5, 10, N'avatars/lan_20250312.jpg', N'VuongBoGoc', N'DenTrang', N'DangDung', '2025-03-12T09:30:00'),
(6, 3, N'avatars/hoa_20250320.jpg', N'VienNau', N'CoDien', N'DangDung', '2025-03-20T10:00:00'),
(7, 18, N'avatars/khoi_20250320.jpg', N'Tron', N'Goc', N'DangDung', '2025-03-20T20:10:00'),
(8, 19, N'avatars/thao_20250321.jpg', N'Tron', N'Goc', N'DangDung', '2025-03-21T21:20:00'),
(9, 41, N'avatars/le_dai_20250201.jpg', N'VienNau', N'Goc', N'DangDung', '2025-02-01T10:30:00'),
(10, 61, N'avatars/le_hanh_20250115.jpg', N'Tron', N'Goc', N'DangDung', '2025-01-15T09:00:00'),
(11, 39, N'avatars/le_canh_20250115.jpg', N'VienNau', N'CoDien', N'DangDung', '2025-01-15T09:30:00');
SET IDENTITY_INSERT Avatar OFF;
GO

-- 9. CuocDoi
SET IDENTITY_INSERT CuocDoi ON;
INSERT INTO CuocDoi (id_CuocDoi, id_OThanhVien, id_NguoiViet, NoiDung, NgayCapNhat) VALUES
(1, 8, 1, N'Ông Nguyễn Văn Nam sinh năm 1968 tại Hội An, Quảng Nam. Ông là con trai cả của cụ Nguyễn Văn Hòa và cụ Trần Thị Mai. Từ nhỏ ông đã theo cha học nghề mộc, sau này mở xưởng nhỏ và nuôi dạy hai con khôn lớn. Ông luôn là người giữ gìn nếp nhà và các ngày giỗ chạp của dòng họ.', '2026-09-20T20:00:00'),
(2, 3, 2, N'Cụ Nguyễn Văn Hòa sinh năm 1946, là trưởng tộc họ Nguyễn trong gần hai mươi năm. Cụ chăm lo nhà thờ họ và duy trì lệ giỗ tổ hằng năm cho tới khi mất vào năm 2019.', '2026-08-02T08:30:00'),
(3, 5, 1, N'Cụ Nguyễn Văn Phúc là em của cụ Hòa. Cụ lập gia đình hai lần, lần đầu với bà Đặng Thị Sen, lần sau với bà Võ Thị Cúc, và nuôi dạy con riêng của bà Cúc như con ruột.', '2026-08-10T19:00:00'),
(4, 41, 5, N'Ông Lê Văn Đại là con trai cả của cụ Lê Văn Cảnh. Ông trải qua bốn lần kết hôn, có con ruột, con kế và một người con nuôi. Ông làm chủ xưởng đóng ghe thuyền lâu năm ở Đà Nẵng và giữ nếp giỗ tổ của dòng họ.', '2026-09-05T18:30:00'),
(5, 39, 5, N'Cụ Lê Văn Cảnh là người đầu tiên đưa gia đình từ Quảng Nam ra lập nghiệp ở Đà Nẵng, mở đầu cho dòng họ Lê ở đây. Cụ là trưởng tộc đầu tiên.', '2026-09-05T18:00:00');
SET IDENTITY_INSERT CuocDoi OFF;
GO

-- 10. BinhLuan
SET IDENTITY_INSERT BinhLuan ON;
INSERT INTO BinhLuan (id_BinhLuan, id_CuocDoi, id_NguoiBinhLuan, NoiDung, NgayBinhLuan) VALUES
(1, 1, 5, N'Ông nhà tôi luôn nhắc con cháu phải nhớ ngày giỗ ông bà.', '2026-10-26T20:30:00'),
(2, 1, 3, N'Con còn nhớ những buổi chiều ba dạy con đóng chiếc ghế nhỏ.', '2026-10-26T09:10:00'),
(3, 1, 4, N'Cảm ơn cô Lan đã viết lại tiểu sử của ba.', '2026-10-24T21:45:00'),
(4, 2, 6, N'Cảm ơn Nam đã viết lại về ông. Ông thương con cháu lắm.', '2026-08-03T07:40:00'),
(5, 2, 2, N'Con xin bổ sung: ba thường dậy sớm quét dọn nhà thờ vào mỗi dịp rằm.', '2026-08-03T21:00:00'),
(6, 3, 3, N'Cháu nhớ chú Phúc hay kể chuyện làm ăn ngày trước.', '2026-08-12T20:20:00'),
(7, 4, 12, N'Bác Đại là người giữ lệ giỗ tổ của họ mình nhiều năm nay.', '2026-09-06T08:15:00'),
(8, 4, 3, N'Xin ghi nhận thêm: bác đối xử với con kế và con nuôi như con ruột.', '2026-09-07T19:50:00');
SET IDENTITY_INSERT BinhLuan OFF;
GO

-- 11. ThanhTuu
SET IDENTITY_INSERT ThanhTuu ON;
INSERT INTO ThanhTuu (id_ThanhTuu, id_OThanhVien, TenThanhTuu, MoTa, Nam) VALUES
(1, 8, N'Nghệ nhân mộc tiêu biểu thành phố Hội An', N'Được UBND thành phố ghi nhận vì giữ gìn nghề mộc truyền thống của địa phương.', 2019),
(2, 8, N'Trùng tu nhà thờ họ Nguyễn', N'Đứng ra vận động con cháu đóng góp và trực tiếp làm phần cửa gỗ, hoành phi cho nhà thờ họ.', 2014),
(3, 8, N'Mở xưởng mộc gia đình', N'Xưởng nhỏ do ông cùng vợ gây dựng, tạo việc làm cho nhiều người trong xóm.', 2005),
(4, 8, N'Giải nhì hội thi tay nghề thợ mộc tỉnh Quảng Nam', N'Giải thưởng đầu tiên trong nghề.', 1990),
(5, 10, N'Giáo viên dạy giỏi cấp thành phố', N'Được công nhận sau nhiều năm giảng dạy bậc tiểu học.', 2010),
(6, 3, N'Giữ chức trưởng tộc họ Nguyễn', N'Đảm nhiệm vai trò trưởng tộc và duy trì lệ giỗ tổ hằng năm.', 1998),
(7, 18, N'Tốt nghiệp loại giỏi Đại học Bách khoa Đà Nẵng', NULL, 2018),
(8, 41, N'Chủ xưởng đóng ghe thuyền Đà Nẵng', N'Gây dựng xưởng và duy trì hoạt động qua nhiều năm.', 1995),
(9, 61, N'Bác sĩ chuyên khoa I', NULL, 2005),
(10, 59, N'Kỹ sư xây dựng công trình cầu đường', N'Tham gia nhiều công trình giao thông tại miền Trung.', 2000);
SET IDENTITY_INSERT ThanhTuu OFF;
GO

-- 12. MaCode
INSERT INTO MaCode (Ma, id_KhongGian, id_NguoiTao, NgayTao, NgayHetHan, DaSuDung, id_NguoiDaDung, NgaySuDung) VALUES
('KT7H29QX', 1, 2, DATEADD(MINUTE, -1, GETDATE()), DATEADD(MINUTE, 4, GETDATE()), 0, NULL, NULL),
('HD4P8W2M', 1, 1, '2026-10-04T14:16:00', '2026-10-04T14:21:00', 1, 8, '2026-10-04T14:20:00'),
('LM5C3R7K', 1, 3, '2026-09-18T10:01:00', '2026-09-18T10:06:00', 1, 9, '2026-09-18T10:05:00'),
('EX9T1B6Z', 1, 7, '2026-09-01T08:00:00', '2026-09-01T08:05:00', 0, NULL, NULL),
('PH2Q7N4D', 4, 8, DATEADD(MINUTE, -1, GETDATE()), DATEADD(MINUTE, 4, GETDATE()), 0, NULL, NULL),
('LE8V3J5H', 3, 5, DATEADD(MINUTE, -1, GETDATE()), DATEADD(MINUTE, 4, GETDATE()), 0, NULL, NULL),
('LE2Y6S9F', 3, 5, '2026-09-30T09:08:00', '2026-09-30T09:13:00', 1, 14, '2026-09-30T09:12:00');
GO

-- Kiem tra nhanh so dong
SELECT 'TaiKhoan' AS Bang, COUNT(*) AS SoDong FROM TaiKhoan
UNION ALL SELECT 'MaXacThuc', COUNT(*) FROM MaXacThuc
UNION ALL SELECT 'KhongGian', COUNT(*) FROM KhongGian
UNION ALL SELECT 'OThanhVien', COUNT(*) FROM OThanhVien
UNION ALL SELECT 'ThanVienTrongKGian', COUNT(*) FROM ThanVienTrongKGian
UNION ALL SELECT 'QuanHeChaMe', COUNT(*) FROM QuanHeChaMe
UNION ALL SELECT 'QHeHonNhan', COUNT(*) FROM QHeHonNhan
UNION ALL SELECT 'Avatar', COUNT(*) FROM Avatar
UNION ALL SELECT 'CuocDoi', COUNT(*) FROM CuocDoi
UNION ALL SELECT 'BinhLuan', COUNT(*) FROM BinhLuan
UNION ALL SELECT 'ThanhTuu', COUNT(*) FROM ThanhTuu
UNION ALL SELECT 'MaCode', COUNT(*) FROM MaCode;
GO
