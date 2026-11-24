package com.example.giapha.model

import java.time.LocalDateTime
data class TaiKhoan(
    val idTaiKhoan: Int,
    val tenHienThi: String,
    val sdt: String,                       // 10 số, ví dụ "0912345678"
    val email: String? = null,
    val loaiDangNhap: LoaiDangNhap = LoaiDangNhap.SDT_OTP,
    val trangThai: TrangThaiTaiKhoan = TrangThaiTaiKhoan.HOAT_DONG,
    val loaiTaiKhoan: LoaiTaiKhoan = LoaiTaiKhoan.THUONG,
    val ngayTao: LocalDateTime,
    val lanDangNhapCuoi: LocalDateTime? = null
)
// Cột MatKhau không đưa vào model: bản 1.0 không dùng.

data class MaXacThuc(
    val id: Int,
    val sdt: String,
    val ma: String,
    val ngayTao: LocalDateTime,
    val hetHan: LocalDateTime,
    val daSuDung: Boolean = false
)