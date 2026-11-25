package com.example.giapha.data

import com.example.giapha.model.*
import java.time.LocalDate
import java.time.LocalDateTime

object SampleAccounts {
    private fun d(s: String) = LocalDate.parse(s)
    private fun dt(s: String) = LocalDateTime.parse(s)
    private val now: LocalDateTime = LocalDateTime.now()

    val taiKhoan = listOf(
        TaiKhoan(idTaiKhoan = 1, tenHienThi = "Nguyễn Thị Lan", sdt = "0905222444", email = "lan.nguyen@gmail.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-02-28T09:10:00"), lanDangNhapCuoi = dt("2026-10-26T20:05:00")),
        TaiKhoan(idTaiKhoan = 2, tenHienThi = "Nguyễn Văn Nam", sdt = "0912111222", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-01-15T10:00:00"), lanDangNhapCuoi = dt("2026-10-20T07:30:00")),
        TaiKhoan(idTaiKhoan = 3, tenHienThi = "Nguyễn Minh Khôi", sdt = "0912345678", email = "minhkhoi.nguyen@gmail.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-03-12T08:30:00"), lanDangNhapCuoi = dt("2026-10-04T08:15:00")),
        TaiKhoan(idTaiKhoan = 4, tenHienThi = "Thảo Nguyễn", sdt = "0944321654", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2024-11-05T19:45:00"), lanDangNhapCuoi = dt("2026-10-24T21:40:00")),
        TaiKhoan(idTaiKhoan = 5, tenHienThi = "Lê Thị Hạnh", sdt = "0977123456", email = "hanh.le@gmail.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2024-11-18T07:55:00"), lanDangNhapCuoi = dt("2026-10-26T18:20:00")),
        TaiKhoan(idTaiKhoan = 6, tenHienThi = "Trần Thị Mai", sdt = "0922888999", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2024-10-25T15:00:00"), lanDangNhapCuoi = dt("2026-09-30T06:45:00")),
        TaiKhoan(idTaiKhoan = 7, tenHienThi = "Chú Bình", sdt = "0987654321", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-01-03T11:20:00"), lanDangNhapCuoi = dt("2026-09-01T08:00:00")),
        TaiKhoan(idTaiKhoan = 8, tenHienThi = "Phạm Thị Hương", sdt = "0933456789", email = "huong.pham@gmail.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2024-12-20T13:05:00"), lanDangNhapCuoi = dt("2026-10-04T14:15:00")),
        TaiKhoan(idTaiKhoan = 9, tenHienThi = "Phan Thị Kim Anh", sdt = "0988111333", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-06-01T09:00:00"), lanDangNhapCuoi = dt("2026-09-18T10:00:00")),
        TaiKhoan(idTaiKhoan = 10, tenHienThi = "Võ Văn Tâm", sdt = "0966777888", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.DA_KHOA, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2024-12-02T16:30:00"), lanDangNhapCuoi = dt("2026-09-20T19:42:00")),
        TaiKhoan(idTaiKhoan = 11, tenHienThi = "Quản trị hệ thống", sdt = "0901000001", email = "admin@giapha.app", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.ADMIN_APP, ngayTao = dt("2024-12-01T08:00:00"), lanDangNhapCuoi = dt("2026-10-25T09:00:00")),
        TaiKhoan(idTaiKhoan = 12, tenHienThi = "Lê Văn Hiếu", sdt = "0914555666", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-01-20T10:15:00"), lanDangNhapCuoi = dt("2026-10-10T20:00:00")),
        TaiKhoan(idTaiKhoan = 13, tenHienThi = "Hồ Thị Thu", sdt = "0915666777", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-06-05T14:00:00"), lanDangNhapCuoi = dt("2026-10-22T12:10:00")),
        TaiKhoan(idTaiKhoan = 14, tenHienThi = "Phan Đức Lộc", sdt = "0916777888", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-09-29T17:00:00"), lanDangNhapCuoi = dt("2026-09-30T09:12:00")),
        TaiKhoan(idTaiKhoan = 15, tenHienThi = "Bình Mai", sdt = "0917888999", email = "binh.mai@gmail.com", loaiDangNhap = LoaiDangNhap.MAT_KHAU, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-06-28T08:40:00"), lanDangNhapCuoi = dt("2026-10-15T19:30:00")),
        TaiKhoan(idTaiKhoan = 16, tenHienThi = "Đinh Công Minh", sdt = "0920100001", email = "user01@example.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-01-01T08:00:00"), lanDangNhapCuoi = dt("2026-07-01T09:00:00")),
        TaiKhoan(idTaiKhoan = 17, tenHienThi = "Bùi Thị Thanh Hà", sdt = "0920100038", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-06-04T09:11:00"), lanDangNhapCuoi = dt("2026-08-03T10:07:00")),
        TaiKhoan(idTaiKhoan = 18, tenHienThi = "Hoàng Văn Long", sdt = "0920100075", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-11-07T10:22:00"), lanDangNhapCuoi = dt("2026-09-05T11:14:00")),
        TaiKhoan(idTaiKhoan = 19, tenHienThi = "Dương Thị Mỹ Linh", sdt = "0920100112", email = "user04@example.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-04-10T11:33:00"), lanDangNhapCuoi = null),
        TaiKhoan(idTaiKhoan = 20, tenHienThi = "Lý Quốc Hưng", sdt = "0920100149", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-09-13T12:44:00"), lanDangNhapCuoi = dt("2026-07-09T13:28:00")),
        TaiKhoan(idTaiKhoan = 21, tenHienThi = "Tô Ngọc Hân", sdt = "0920100186", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-02-16T13:55:00"), lanDangNhapCuoi = dt("2026-08-11T14:35:00")),
        TaiKhoan(idTaiKhoan = 22, tenHienThi = "Huỳnh Gia Bảo", sdt = "0920100223", email = "user07@example.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-07-19T14:06:00"), lanDangNhapCuoi = dt("2026-09-13T15:42:00")),
        TaiKhoan(idTaiKhoan = 23, tenHienThi = "Mai Thanh Tùng", sdt = "0920100260", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-12-22T15:17:00"), lanDangNhapCuoi = null),
        TaiKhoan(idTaiKhoan = 24, tenHienThi = "Cao Thị Bích Ngọc", sdt = "0920100297", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2025-05-25T16:28:00"), lanDangNhapCuoi = dt("2026-07-17T17:56:00")),
        TaiKhoan(idTaiKhoan = 25, tenHienThi = "Đoàn Minh Trí", sdt = "0920100334", email = "user10@example.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-10-01T17:39:00"), lanDangNhapCuoi = dt("2026-08-19T09:03:00")),
        TaiKhoan(idTaiKhoan = 26, tenHienThi = "Trịnh Hải Yến", sdt = "0920100371", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-03-04T08:50:00"), lanDangNhapCuoi = dt("2026-09-21T10:10:00")),
        TaiKhoan(idTaiKhoan = 27, tenHienThi = "Vương Đình Khoa", sdt = "0920100408", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-08-07T09:01:00"), lanDangNhapCuoi = null),
        TaiKhoan(idTaiKhoan = 28, tenHienThi = "Lâm Thị Kiều Oanh", sdt = "0920100445", email = "user13@example.com", loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-01-10T10:12:00"), lanDangNhapCuoi = dt("2026-07-25T12:24:00")),
        TaiKhoan(idTaiKhoan = 29, tenHienThi = "Châu Anh Tuấn", sdt = "0920100482", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.HOAT_DONG, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-06-13T11:23:00"), lanDangNhapCuoi = dt("2026-08-27T13:31:00")),
        TaiKhoan(idTaiKhoan = 30, tenHienThi = "Phùng Thế Vinh", sdt = "0920100519", email = null, loaiDangNhap = LoaiDangNhap.SDT_OTP, trangThai = TrangThaiTaiKhoan.DA_KHOA, loaiTaiKhoan = LoaiTaiKhoan.THUONG, ngayTao = dt("2026-11-16T12:34:00"), lanDangNhapCuoi = dt("2026-09-02T14:38:00")),
    )

    val maXacThuc = listOf(
        MaXacThuc(id = 1, sdt = "0912345678", ma = "482915", ngayTao = now, hetHan = now.plusMinutes(5), daSuDung = false),
        MaXacThuc(id = 2, sdt = "0905222444", ma = "731024", ngayTao = dt("2026-10-26T20:00:00"), hetHan = dt("2026-10-26T20:05:00"), daSuDung = true),
        MaXacThuc(id = 3, sdt = "0987654321", ma = "905837", ngayTao = dt("2026-09-01T07:50:00"), hetHan = dt("2026-09-01T07:55:00"), daSuDung = false),
    )

    val khongGian = listOf(
        KhongGian(idKhongGian = 1, tenKhongGian = "Gia phả họ Nguyễn", idNguoiTao = 1, ngayTao = dt("2025-03-12T09:00:00"), cheDoRiengTu = false),
        KhongGian(idKhongGian = 2, tenKhongGian = "Gia phả họ Trần", idNguoiTao = 3, ngayTao = dt("2025-04-02T10:30:00"), cheDoRiengTu = true),
        KhongGian(idKhongGian = 3, tenKhongGian = "Dòng họ Lê ở Đà Nẵng", idNguoiTao = 5, ngayTao = dt("2025-01-15T08:45:00"), cheDoRiengTu = false),
        KhongGian(idKhongGian = 4, tenKhongGian = "Gia phả họ Phạm", idNguoiTao = 8, ngayTao = dt("2025-01-03T14:10:00"), cheDoRiengTu = true),
    )

    val thanVien = listOf(
        ThanVienTrongKGian(idThanVienTrongKGian = 1, idKhongGian = 1, idTaiKhoan = 1, vaiTro = VaiTro.ADMIN, idOThanhVien = 10, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-12T09:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 2, idKhongGian = 1, idTaiKhoan = 2, vaiTro = VaiTro.QUAN_LY, idOThanhVien = 8, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-14T19:30:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 3, idKhongGian = 1, idTaiKhoan = 3, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 18, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-20T20:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 4, idKhongGian = 1, idTaiKhoan = 4, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 19, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-21T21:15:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 5, idKhongGian = 1, idTaiKhoan = 5, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 9, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-22T08:05:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 6, idKhongGian = 1, idTaiKhoan = 6, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 4, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-03-25T09:30:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 7, idKhongGian = 1, idTaiKhoan = 7, vaiTro = VaiTro.THANH_VIEN_HO, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-04-02T10:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 8, idKhongGian = 1, idTaiKhoan = 8, vaiTro = VaiTro.KHACH_TAM, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2026-10-04T14:20:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 9, idKhongGian = 1, idTaiKhoan = 9, vaiTro = VaiTro.KHACH_TAM, idOThanhVien = null, trangThai = TrangThaiThanhVien.DA_ROI, ngayThamGia = dt("2026-09-18T10:05:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 10, idKhongGian = 1, idTaiKhoan = 10, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 29, trangThai = TrangThaiThanhVien.BI_CAM, ngayThamGia = dt("2025-04-10T08:00:00"), ngayCam = dt("2026-08-15T21:30:00"), lyDoCam = "Tự ý sửa thông tin của nhiều thành viên khi chưa được phép", idNguoiCam = 1),
        ThanVienTrongKGian(idThanVienTrongKGian = 11, idKhongGian = 2, idTaiKhoan = 3, vaiTro = VaiTro.ADMIN, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-04-02T10:30:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 12, idKhongGian = 2, idTaiKhoan = 1, vaiTro = VaiTro.THANH_VIEN_HO, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-04-20T16:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 13, idKhongGian = 3, idTaiKhoan = 5, vaiTro = VaiTro.ADMIN, idOThanhVien = 61, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-01-15T08:45:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 14, idKhongGian = 3, idTaiKhoan = 3, vaiTro = VaiTro.QUAN_LY, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-05-08T09:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 15, idKhongGian = 3, idTaiKhoan = 12, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 59, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-02-01T10:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 16, idKhongGian = 3, idTaiKhoan = 13, vaiTro = VaiTro.THANH_VIEN_HO, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-06-12T14:30:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 17, idKhongGian = 3, idTaiKhoan = 14, vaiTro = VaiTro.KHACH_TAM, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2026-09-30T09:12:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 18, idKhongGian = 3, idTaiKhoan = 15, vaiTro = VaiTro.THANH_VIEN, idOThanhVien = 63, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-07-01T08:30:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 19, idKhongGian = 4, idTaiKhoan = 8, vaiTro = VaiTro.ADMIN, idOThanhVien = 81, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-01-03T14:10:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
        ThanVienTrongKGian(idThanVienTrongKGian = 20, idKhongGian = 4, idTaiKhoan = 3, vaiTro = VaiTro.THANH_VIEN_HO, idOThanhVien = null, trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = dt("2025-05-20T11:00:00"), ngayCam = null, lyDoCam = null, idNguoiCam = null),
    )

    val maCode = listOf(
        MaCode(ma = "KT7H29QX", idKhongGian = 1, idNguoiTao = 2, ngayTao = now.plusMinutes(-1), ngayHetHan = now.plusMinutes(4), daSuDung = false, idNguoiDaDung = null, ngaySuDung = null),
        MaCode(ma = "HD4P8W2M", idKhongGian = 1, idNguoiTao = 1, ngayTao = dt("2026-10-04T14:16:00"), ngayHetHan = dt("2026-10-04T14:21:00"), daSuDung = true, idNguoiDaDung = 8, ngaySuDung = dt("2026-10-04T14:20:00")),
        MaCode(ma = "LM5C3R7K", idKhongGian = 1, idNguoiTao = 3, ngayTao = dt("2026-09-18T10:01:00"), ngayHetHan = dt("2026-09-18T10:06:00"), daSuDung = true, idNguoiDaDung = 9, ngaySuDung = dt("2026-09-18T10:05:00")),
        MaCode(ma = "EX9T1B6Z", idKhongGian = 1, idNguoiTao = 7, ngayTao = dt("2026-09-01T08:00:00"), ngayHetHan = dt("2026-09-01T08:05:00"), daSuDung = false, idNguoiDaDung = null, ngaySuDung = null),
        MaCode(ma = "PH2Q7N4D", idKhongGian = 4, idNguoiTao = 8, ngayTao = now.plusMinutes(-1), ngayHetHan = now.plusMinutes(4), daSuDung = false, idNguoiDaDung = null, ngaySuDung = null),
        MaCode(ma = "LE8V3J5H", idKhongGian = 3, idNguoiTao = 5, ngayTao = now.plusMinutes(-1), ngayHetHan = now.plusMinutes(4), daSuDung = false, idNguoiDaDung = null, ngaySuDung = null),
        MaCode(ma = "LE2Y6S9F", idKhongGian = 3, idNguoiTao = 5, ngayTao = dt("2026-09-30T09:08:00"), ngayHetHan = dt("2026-09-30T09:13:00"), daSuDung = true, idNguoiDaDung = 14, ngaySuDung = dt("2026-09-30T09:12:00")),
    )
}