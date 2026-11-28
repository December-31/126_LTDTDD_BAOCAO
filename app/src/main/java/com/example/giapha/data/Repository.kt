package com.example.giapha.data

import com.example.giapha.model.*
import java.time.LocalDate

/** Một dòng trong danh sách không gian (GD-03): không gian kèm vai trò của người đang đăng nhập. */
data class KhongGianCuaToi(
    val khongGian: KhongGian,
    val vaiTro: VaiTro
)

/** Toàn bộ dữ liệu để vẽ cây (GD-06, GD-24). Bố cục tính ở LayoutEngine, không tính ở đây. */
data class DuLieuCay(
    val khongGian: KhongGian,
    val toi: ThanVienTrongKGian,                // vai trò của người đang xem
    val oThanhVien: List<OThanhVien>,
    val quanHeChaMe: List<QuanHeChaMe>,         // gồm cả dòng đang ẩn (HienThiLienKet = false)
    val honNhan: List<QHeHonNhan>,
    val anhDangDung: Map<Int, String?>          // id Ô -> đường dẫn ảnh đang dùng
)

/** Một người liên quan trong khối "Quan hệ gia đình" của GD-08. nhan: Ruột/Nuôi/Kế hoặc trạng thái hôn nhân. */
data class NguoiLienQuan(
    val idO: Int,
    val hoTen: String,
    val nhan: String?,
    val conSong: Boolean
)

/** Dữ liệu của màn GD-08. */
data class ChiTietO(
    val o: OThanhVien,
    val toi: ThanVienTrongKGian,          // vai trò của người đang xem
    val chaMe: List<NguoiLienQuan>,
    val voChong: List<NguoiLienQuan>,
    val con: List<NguoiLienQuan>
)

/** Tác động của việc xóa một Ô, dùng dựng nội dung hộp cảnh báo ở GD-08. */
data class TacDongXoa(
    val hoTen: String,
    val soChaMe: Int,
    val soVoChong: Int,
    val soCon: Int,
    val soThanhTuu: Int,
    val soBinhLuan: Int,
    val soTieuSu: Int,
    val tachKhoiCayChinh: List<String>     // họ tên những người sẽ mất liên kết với cây chính
)

/** Dữ liệu nhập từ form GD-07. */
data class FormO(
    val hoTen: String,
    val bietDanh: String?,
    val gioiTinh: GioiTinh?,
    val ngaySinh: LocalDate?,
    val queQuan: String?,
    val cccd: String?,
    val conSong: Boolean,
    val ngayMat: LocalDate?
)

/** Một kết quả tìm Ô kèm ngữ cảnh để phân biệt người trùng tên. */
data class KetQuaTim(
    val idO: Int,
    val hoTen: String,
    val nguCanh: String,       // "Con của: A, B" / "Vợ/chồng của: X" / "Chưa có quan hệ"
    val soHonNhan: Int         // số hôn nhân hiện có, để gợi ý "Thứ tự hôn nhân"
)

data class ChaMeChon(val idO: Int, val loai: LoaiQuanHe, val ngayXacLap: LocalDate?)

/** Quan hệ gắn kèm khi tạo Ô mới. null = Ô độc lập. */
sealed class QuanHeMoi {
    data class LaCon(val chaMe: List<ChaMeChon>) : QuanHeMoi()          // 1–2 người
    data class LaVoChong(val idNguoiKia: Int, val ngayCuoi: LocalDate?, val thuTu: Int) : QuanHeMoi()
}

/** Chiều khi thêm quan hệ ở GD-12, tính từ Ô đang xem. */
enum class ChieuQuanHe {
    LA_CON,         // Ô đang xem là con → người được chọn là cha/mẹ của Ô
    LA_PHU_HUYNH    // Ô đang xem là cha/mẹ → người được chọn là con của Ô
}

/** Một dòng ở GD-12: người ở đầu kia của quan hệ cha/mẹ–con. */
data class DongQuanHeChaMe(
    val idNguoi: Int,
    val hoTen: String,
    val conSong: Boolean,
    val loai: LoaiQuanHe,
    val ngayXacLap: LocalDate?
)

/** Dữ liệu màn GD-12: cha/mẹ của Ô và con của Ô. */
data class QuanHeChaMeCuaO(
    val o: OThanhVien,
    val chaMe: List<DongQuanHeChaMe>,
    val con: List<DongQuanHeChaMe>
)

/**
 * Cửa duy nhất giữa màn hình và dữ liệu. Màn hình không đọc SampleData trực tiếp.
 * Hiện chạy bằng FakeRepository (bộ nhớ); sau này thay bằng bản gọi API.
 * Các nhóm hàm cho bước 8–11 sẽ được thêm vào khi làm tới bước đó.
 */
interface Repository {

    // ---- Tài khoản (GD-01, 02, 04) ----
    fun guiMaDangKy(sdt: String): Kq<Unit>
    fun guiMaDangNhap(sdt: String): Kq<Unit>
    fun dangKy(ten: String, sdt: String, otp: String): Kq<TaiKhoan>
    fun dangNhap(sdt: String, otp: String): Kq<TaiKhoan>
    fun dangXuat()
    fun taiKhoanHienTai(): TaiKhoan?
    fun capNhatHoSo(tenHienThi: String, email: String?): Kq<TaiKhoan>

    // ---- Không gian (GD-03, 05) ----
    fun danhSachKhongGian(tuKhoa: String = ""): Kq<List<KhongGianCuaToi>>
    fun taoKhongGian(ten: String): Kq<KhongGian>
    fun vaoBangMa(ma: String): Kq<KhongGian>
    fun vaiTroCuaToi(idKhongGian: Int): ThanVienTrongKGian?

    // ---- Cây gia phả (GD-06) ----
    fun duLieuCay(idKhongGian: Int): Kq<DuLieuCay>

    // ---- Chi tiết Ô (GD-08) ----
    fun chiTietO(idO: Int): Kq<ChiTietO>
    /** conSong = false: xác nhận tử vong (bắt buộc có ngayMat). conSong = true: hủy xác nhận. Admin/Quản lý. */
    fun datTuVong(idO: Int, conSong: Boolean, ngayMat: java.time.LocalDate?): Kq<OThanhVien>
    /** Chỉ Admin. */
    fun datTruongToc(idO: Int, laTruongToc: Boolean): Kq<OThanhVien>
    /** Admin/Quản lý. Chưa xóa gì, chỉ tính tác động. */
    fun xemTruocXoaO(idO: Int): Kq<TacDongXoa>
    /** Xóa chỉ Ô này, các dòng quan hệ nối tới nó và dữ liệu riêng của nó (QUYET_DINH mục 5.5). */
    fun xoaO(idO: Int): Kq<Unit>
    // ---- Thêm/sửa Ô (GD-07) ----
    /** Admin/Quản lý. quanHe = null: Ô độc lập. Ghi Ô và quan hệ cùng lúc, lỗi thì không ghi gì. */
    fun themO(idKhongGian: Int, f: FormO, quanHe: QuanHeMoi? = null): Kq<OThanhVien>
    /** Admin/Quản lý. Chỉ sửa thông tin cá nhân; không đụng quan hệ, không đổi Trưởng tộc. */
    fun suaO(idO: Int, f: FormO): Kq<OThanhVien>
    /** Admin/Quản lý. Tìm Ô trong không gian theo tên (không phân biệt dấu), kèm ngữ cảnh quan hệ. */
    fun timO(idKhongGian: Int, tuKhoa: String): Kq<List<KetQuaTim>>
    // ---- Quan hệ cha/mẹ (GD-12) ----
    /** Admin/Quản lý. Cha/mẹ của Ô và con của Ô, kể cả các dòng đang ẩn. */
    fun quanHeChaMeCua(idO: Int): Kq<QuanHeChaMeCuaO>
    /** Tìm người để thêm; đã loại Ô này, người đã nối trực tiếp và những người sẽ tạo vòng lặp huyết thống. */
    fun timUngVienChaMe(idO: Int, chieu: ChieuQuanHe, tuKhoa: String): Kq<List<KetQuaTim>>
    /** Chặn: trùng dòng, vòng lặp huyết thống, quá một cha ruột hoặc một mẹ ruột. */
    fun themQuanHeChaMe(idO: Int, chieu: ChieuQuanHe, idNguoiKia: Int, loai: LoaiQuanHe, ngayXacLap: LocalDate?): Kq<Unit>
    /** Chỉ đổi loại và ngày; muốn đổi người thì xóa rồi thêm lại. */
    fun suaQuanHeChaMe(idCon: Int, idPhuHuynh: Int, loai: LoaiQuanHe, ngayXacLap: LocalDate?): Kq<Unit>
    /** Chỉ gỡ dòng quan hệ, không xóa Ô nào. */
    fun xoaQuanHeChaMe(idCon: Int, idPhuHuynh: Int): Kq<Unit>
}
