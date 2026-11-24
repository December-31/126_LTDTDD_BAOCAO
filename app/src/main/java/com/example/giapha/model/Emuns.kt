package com.example.giapha.model

/** Giá trị lưu trong CSDL nằm ở `db`. Dùng fromDb(...) khi đọc. */

enum class VaiTro(val db: String, val nhan: String) {
    ADMIN("Admin", "Admin"),
    QUAN_LY("QuanLy", "Quản lý"),
    THANH_VIEN("ThanhVien", "Thành viên"),
    THANH_VIEN_HO("ThanhVienHo", "Thành viên hờ"),
    KHACH_TAM("KhachTam", "Khách tạm");

    /** Admin hoặc Quản lý: được sửa cây, tiểu sử, mời... */
    val laQuanTri: Boolean get() = this == ADMIN || this == QUAN_LY

    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class TrangThaiThanhVien(val db: String) {
    HOAT_DONG("HoatDong"), BI_CAM("BiCam"), DA_ROI("DaRoi");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class TrangThaiTaiKhoan(val db: String) {
    HOAT_DONG("HoatDong"), DA_KHOA("DaKhoa");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class LoaiTaiKhoan(val db: String) {
    THUONG("Thuong"), ADMIN_APP("AdminApp");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class LoaiDangNhap(val db: String) {
    SDT_OTP("SDT_OTP"), MAT_KHAU("MatKhau"), GOOGLE("Google"), APPLE("Apple");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class GioiTinh(val db: String, val nhan: String) {
    NAM("Nam", "Nam"), NU("Nu", "Nữ"), KHAC("Khac", "Khác");
    companion object { fun fromDb(s: String?) = entries.firstOrNull { it.db == s } }
}

enum class TrangThaiLienKet(val db: String) {
    DANG_NOI("DangNoi"), DA_TACH_HO("DaTachHo");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

/** Loại quan hệ cha/mẹ với con. */
enum class LoaiQuanHe(val db: String, val nhan: String) {
    RUOT("Ruot", "Ruột"), NUOI("Nuoi", "Nuôi"), KE("Ke", "Kế");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class TrangThaiHonNhan(val db: String, val nhan: String) {
    DANG_KET_HON("DangKetHon", "Đang kết hôn"),
    DA_LY_HON("DaLyHon", "Đã ly hôn"),
    GOA("Goa", "Góa");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}

enum class TrangThaiAvatar(val db: String) {
    DANG_DUNG("DangDung"), LICH_SU("LichSu");
    companion object { fun fromDb(s: String) = entries.first { it.db == s } }
}