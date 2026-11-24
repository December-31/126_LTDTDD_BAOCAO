package com.example.giapha.model

import java.time.LocalDateTime

data class KhongGian(
    val idKhongGian: Int,
    val tenKhongGian: String,
    val idNguoiTao: Int,
    val ngayTao: LocalDateTime,
    val cheDoRiengTu: Boolean = false
)

/** Vai trò, trạng thái và Ô được gán của một tài khoản trong một không gian. */
data class ThanVienTrongKGian(
    val idThanVienTrongKGian: Int,
    val idKhongGian: Int,
    val idTaiKhoan: Int,
    val vaiTro: VaiTro,
    val idOThanhVien: Int? = null,         // Khách tạm luôn null; Thành viên hờ có thể null
    val trangThai: TrangThaiThanhVien = TrangThaiThanhVien.HOAT_DONG,
    val ngayThamGia: LocalDateTime,
    val ngayCam: LocalDateTime? = null,
    val lyDoCam: String? = null,
    val idNguoiCam: Int? = null
)

/** Mã mời dùng một lần, hiệu lực 5 phút. */
data class MaCode(
    val ma: String,
    val idKhongGian: Int,
    val idNguoiTao: Int,
    val ngayTao: LocalDateTime,
    val ngayHetHan: LocalDateTime,
    val daSuDung: Boolean = false,
    val idNguoiDaDung: Int? = null,
    val ngaySuDung: LocalDateTime? = null
)