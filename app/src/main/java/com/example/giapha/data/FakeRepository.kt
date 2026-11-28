package com.example.giapha.data

import com.example.giapha.model.*
import java.time.LocalDateTime

/**
 * Bản dữ liệu cứng: giữ mọi thứ trong bộ nhớ, khởi tạo từ SampleData.
 * Mỗi lần chạy app là bắt đầu lại từ đầu (không lưu file, không nhớ đăng nhập).
 */
class FakeRepository : Repository {

    private val taiKhoan = SampleData.taiKhoan.toMutableList()
    private val khongGian = SampleData.khongGian.toMutableList()
    private val thanVien = SampleData.thanVien.toMutableList()
    private val maCode = SampleData.maCode.toMutableList()
    private val oThanhVien = SampleData.oThanhVien.toMutableList()
    private val quanHeChaMe = SampleData.quanHeChaMe.toMutableList()
    private val honNhan = SampleData.honNhan.toMutableList()
    private val avatar = SampleData.avatar.toMutableList()
    private var idHienTai: Int? = null
    /** Bật true để thử trạng thái "Lỗi kết nối" (mọi hàm đọc cây và danh sách trả lỗi). */
    var giaLapLoi: Boolean = false
    private val cuocDoi = SampleData.cuocDoi.toMutableList()
    private val binhLuan = SampleData.binhLuan.toMutableList()
    private val thanhTuu = SampleData.thanhTuu.toMutableList()

    private fun loi(l: LoiApp, tb: String) = Kq.Loi(l, tb)
    private fun matKetNoi() = loi(LoiApp.MAT_KET_NOI, "Không thể tải dữ liệu. Vui lòng kiểm tra kết nối.")
    private fun hopLeSdt(s: String) = s.length == 10 && s.all { it.isDigit() } && s.startsWith("0")
    private fun nhanDang() = idHienTai?.let { id -> taiKhoan.firstOrNull { it.idTaiKhoan == id } }

    // ---------------- Tài khoản ----------------

    override fun guiMaDangKy(sdt: String): Kq<Unit> {
        if (!hopLeSdt(sdt)) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Số điện thoại không hợp lệ")
        if (taiKhoan.any { it.sdt == sdt }) return loi(LoiApp.DA_TON_TAI, "Số điện thoại này đã đăng ký")
        return Kq.Ok(Unit)
    }

    override fun guiMaDangNhap(sdt: String): Kq<Unit> {
        if (!hopLeSdt(sdt)) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Số điện thoại không hợp lệ")
        val tk = taiKhoan.firstOrNull { it.sdt == sdt }
            ?: return loi(LoiApp.CHUA_DANG_KY, "Số điện thoại chưa đăng ký")
        if (tk.trangThai == TrangThaiTaiKhoan.DA_KHOA)
            return loi(LoiApp.TAI_KHOAN_BI_KHOA, "Tài khoản đã bị khóa")
        return Kq.Ok(Unit)
    }

    override fun dangKy(ten: String, sdt: String, otp: String): Kq<TaiKhoan> {
        if (ten.isBlank()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập tên của bạn")
        guiMaDangKy(sdt).khiLoi { return it }
        if (otp != SampleData.OTP) return loi(LoiApp.OTP_SAI, "Mã xác thực không đúng")
        val moi = TaiKhoan(
            idTaiKhoan = (taiKhoan.maxOfOrNull { it.idTaiKhoan } ?: 0) + 1,
            tenHienThi = ten.trim(), sdt = sdt,
            ngayTao = LocalDateTime.now(), lanDangNhapCuoi = LocalDateTime.now()
        )
        taiKhoan.add(moi)
        idHienTai = moi.idTaiKhoan
        return Kq.Ok(moi)
    }

    override fun dangNhap(sdt: String, otp: String): Kq<TaiKhoan> {
        guiMaDangNhap(sdt).khiLoi { return it }
        if (otp != SampleData.OTP) return loi(LoiApp.OTP_SAI, "Mã xác thực không đúng")
        val i = taiKhoan.indexOfFirst { it.sdt == sdt }
        val capNhat = taiKhoan[i].copy(lanDangNhapCuoi = LocalDateTime.now())
        taiKhoan[i] = capNhat
        idHienTai = capNhat.idTaiKhoan
        return Kq.Ok(capNhat)
    }

    override fun dangXuat() { idHienTai = null }

    override fun taiKhoanHienTai(): TaiKhoan? = nhanDang()

    override fun capNhatHoSo(tenHienThi: String, email: String?): Kq<TaiKhoan> {
        val tk = nhanDang() ?: return loi(LoiApp.CHUA_DANG_NHAP, "Bạn chưa đăng nhập")
        if (tenHienThi.isBlank()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Tên hiển thị không được để trống")
        val mail = email?.trim()?.ifEmpty { null }
        if (mail != null && !mail.contains('@'))
            return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Email không hợp lệ")
        val moi = tk.copy(tenHienThi = tenHienThi.trim(), email = mail)
        taiKhoan[taiKhoan.indexOfFirst { it.idTaiKhoan == tk.idTaiKhoan }] = moi
        return Kq.Ok(moi)
    }

    // ---------------- Không gian ----------------

    override fun danhSachKhongGian(tuKhoa: String): Kq<List<KhongGianCuaToi>> {
        if (giaLapLoi) return matKetNoi()
        val tk = nhanDang() ?: return loi(LoiApp.CHUA_DANG_NHAP, "Bạn chưa đăng nhập")
        val ds = thanVien
            .filter {
                it.idTaiKhoan == tk.idTaiKhoan &&
                        it.trangThai == TrangThaiThanhVien.HOAT_DONG &&
                        it.vaiTro != VaiTro.KHACH_TAM
            }
            .mapNotNull { tv ->
                khongGian.firstOrNull { it.idKhongGian == tv.idKhongGian }?.let { KhongGianCuaToi(it, tv.vaiTro) }
            }
            .filter { it.khongGian.tenKhongGian.contains(tuKhoa.trim(), ignoreCase = true) }
            .sortedBy { it.khongGian.idKhongGian }
        return Kq.Ok(ds)
    }

    override fun taoKhongGian(ten: String): Kq<KhongGian> {
        val tk = nhanDang() ?: return loi(LoiApp.CHUA_DANG_NHAP, "Bạn chưa đăng nhập")
        if (ten.isBlank()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập tên không gian")
        val kg = KhongGian(
            idKhongGian = (khongGian.maxOfOrNull { it.idKhongGian } ?: 0) + 1,
            tenKhongGian = ten.trim(), idNguoiTao = tk.idTaiKhoan, ngayTao = LocalDateTime.now()
        )
        khongGian.add(kg)
        thanVien.add(
            ThanVienTrongKGian(
                idThanVienTrongKGian = (thanVien.maxOfOrNull { it.idThanVienTrongKGian } ?: 0) + 1,
                idKhongGian = kg.idKhongGian, idTaiKhoan = tk.idTaiKhoan,
                vaiTro = VaiTro.ADMIN, ngayThamGia = LocalDateTime.now()
            )
        )
        return Kq.Ok(kg)
    }

    override fun vaoBangMa(ma: String): Kq<KhongGian> {
        val tk = nhanDang() ?: return loi(LoiApp.CHUA_DANG_NHAP, "Bạn chưa đăng nhập")
        val code = maCode.firstOrNull { it.ma.equals(ma.trim(), ignoreCase = true) }
            ?: return loi(LoiApp.MA_KHONG_TON_TAI, "Mã lời mời không tồn tại")
        if (code.daSuDung) return loi(LoiApp.MA_DA_DUNG, "Mã này đã được sử dụng")
        if (code.ngayHetHan.isBefore(LocalDateTime.now())) return loi(LoiApp.MA_HET_HAN, "Mã đã hết hạn")
        val kg = khongGian.first { it.idKhongGian == code.idKhongGian }
        if (kg.cheDoRiengTu) return loi(LoiApp.KHONG_GIAN_RIENG_TU, "Không gian này đang ở chế độ riêng tư")

        val i = thanVien.indexOfFirst { it.idKhongGian == kg.idKhongGian && it.idTaiKhoan == tk.idTaiKhoan }
        if (i >= 0) {
            val cu = thanVien[i]
            when (cu.trangThai) {
                TrangThaiThanhVien.BI_CAM -> return loi(LoiApp.DA_BI_CHAN, "Bạn đã bị chặn khỏi không gian này")
                // Đã có vai trò: giữ nguyên, không tạo Khách tạm, không dùng hết mã.
                TrangThaiThanhVien.HOAT_DONG -> return Kq.Ok(kg)
                // Đã rời: vào lại bằng mã thành Khách tạm (bảng có UNIQUE nên cập nhật dòng cũ).
                TrangThaiThanhVien.DA_ROI -> thanVien[i] = cu.copy(
                    vaiTro = VaiTro.KHACH_TAM, idOThanhVien = null,
                    trangThai = TrangThaiThanhVien.HOAT_DONG, ngayThamGia = LocalDateTime.now()
                )
            }
        } else {
            thanVien.add(
                ThanVienTrongKGian(
                    idThanVienTrongKGian = (thanVien.maxOfOrNull { it.idThanVienTrongKGian } ?: 0) + 1,
                    idKhongGian = kg.idKhongGian, idTaiKhoan = tk.idTaiKhoan,
                    vaiTro = VaiTro.KHACH_TAM, ngayThamGia = LocalDateTime.now()
                )
            )
        }
        maCode[maCode.indexOf(code)] = code.copy(
            daSuDung = true, idNguoiDaDung = tk.idTaiKhoan, ngaySuDung = LocalDateTime.now()
        )
        return Kq.Ok(kg)
    }

    override fun vaiTroCuaToi(idKhongGian: Int): ThanVienTrongKGian? {
        val tk = nhanDang() ?: return null
        return thanVien.firstOrNull {
            it.idKhongGian == idKhongGian && it.idTaiKhoan == tk.idTaiKhoan &&
                    it.trangThai == TrangThaiThanhVien.HOAT_DONG
        }
    }

    // ---------------- Cây gia phả ----------------

    override fun duLieuCay(idKhongGian: Int): Kq<DuLieuCay> {
        if (giaLapLoi) return matKetNoi()
        val toi = vaiTroCuaToi(idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xem không gian này")
        val kg = khongGian.firstOrNull { it.idKhongGian == idKhongGian }
            ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy không gian")
        // Riêng tư: ngắt ngay Khách tạm đang xem.
        if (kg.cheDoRiengTu && toi.vaiTro == VaiTro.KHACH_TAM)
            return loi(LoiApp.KHONG_GIAN_RIENG_TU, "Không gian này đang ở chế độ riêng tư")

        val cacO = oThanhVien.filter { it.idKhongGian == idKhongGian }
        val ids = cacO.map { it.idOThanhVien }.toSet()
        return Kq.Ok(
            DuLieuCay(
                khongGian = kg, toi = toi, oThanhVien = cacO,
                quanHeChaMe = quanHeChaMe.filter { it.idCon in ids && it.idPhuHuynh in ids },
                honNhan = honNhan.filter { it.idChong in ids && it.idVo in ids },
                anhDangDung = avatar
                    .filter { it.idOThanhVien in ids && it.trangThai == TrangThaiAvatar.DANG_DUNG }
                    .associate { it.idOThanhVien to it.duongDanAnh }
            )
        )
    }

    // ---------------- Chi tiết Ô (GD-08) ----------------

    private fun layO(idO: Int) = oThanhVien.firstOrNull { it.idOThanhVien == idO }
    private fun thayO(moi: OThanhVien) { oThanhVien[oThanhVien.indexOfFirst { it.idOThanhVien == moi.idOThanhVien }] = moi }

    override fun chiTietO(idO: Int): Kq<ChiTietO> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        val toi = vaiTroCuaToi(o.idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xem không gian này")
        val kg = khongGian.first { it.idKhongGian == o.idKhongGian }
        if (kg.cheDoRiengTu && toi.vaiTro == VaiTro.KHACH_TAM)
            return loi(LoiApp.KHONG_GIAN_RIENG_TU, "Không gian này đang ở chế độ riêng tư")

        fun nguoi(id: Int, nhan: String?) = oThanhVien.first { it.idOThanhVien == id }
            .let { NguoiLienQuan(it.idOThanhVien, it.hoTen, nhan, it.conSong) }

        val chaMe = quanHeChaMe.filter { it.idCon == idO }
            .sortedWith(compareBy({ it.loai.ordinal }, { it.idPhuHuynh }))
            .map { nguoi(it.idPhuHuynh, it.loai.nhan) }
        val voChong = honNhan.filter { it.idChong == idO || it.idVo == idO }
            .sortedWith(compareBy({ it.thuTuHonNhan ?: Int.MAX_VALUE }, { it.id }))
            .map { nguoi(if (it.idChong == idO) it.idVo else it.idChong, it.trangThai.nhan) }
        val con = quanHeChaMe.filter { it.idPhuHuynh == idO }.map { it.idCon }.distinct()
            .map { id -> oThanhVien.first { it.idOThanhVien == id } }
            .sortedWith(compareBy({ it.ngaySinh ?: java.time.LocalDate.MAX }, { it.idOThanhVien }))
            .map { nguoi(it.idOThanhVien, null) }
        return Kq.Ok(ChiTietO(o, toi, chaMe, voChong, con))
    }

    override fun datTuVong(idO: Int, conSong: Boolean, ngayMat: java.time.LocalDate?): Kq<OThanhVien> {
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        if (vaiTroCuaToi(o.idKhongGian)?.vaiTro?.laQuanTri != true)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền thực hiện thao tác này")
        if (!conSong) {
            if (ngayMat == null) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng chọn ngày mất")
            if (ngayMat.isAfter(java.time.LocalDate.now()))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày mất không thể ở tương lai")
            if (o.ngaySinh != null && ngayMat.isBefore(o.ngaySinh))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày mất không thể trước ngày sinh")
        }
        val moi = o.copy(conSong = conSong, ngayMat = if (conSong) null else ngayMat)
        thayO(moi)
        return Kq.Ok(moi)
    }

    override fun datTruongToc(idO: Int, laTruongToc: Boolean): Kq<OThanhVien> {
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        if (vaiTroCuaToi(o.idKhongGian)?.vaiTro != VaiTro.ADMIN)
            return loi(LoiApp.KHONG_CO_QUYEN, "Chỉ Admin được gán hoặc thu hồi danh hiệu Trưởng tộc")
        val moi = o.copy(laTruongToc = laTruongToc)
        thayO(moi)
        return Kq.Ok(moi)
    }

    // ---------------- Xóa Ô ----------------

    /**
     * Tập Ô thuộc cụm chính của `ids`: cụm chứa Ô có id nhỏ nhất trong số Ô có ít nhất một đường nối.
     * Phản chiếu cách LayoutEngine chọn cụm chính. (8b: hôn nhân đã ẩn cũng phải bị bỏ ở đây.)
     */
    private fun cumChinh(ids: Set<Int>): Set<Int> {
        val ke = HashMap<Int, MutableList<Int>>()
        fun noi(a: Int, b: Int) {
            if (a == b) return
            ke.getOrPut(a) { mutableListOf() }.add(b)
            ke.getOrPut(b) { mutableListOf() }.add(a)
        }
        quanHeChaMe.filter { it.hienThiLienKet && it.idCon in ids && it.idPhuHuynh in ids }
            .forEach { noi(it.idCon, it.idPhuHuynh) }
        honNhan.filter { it.idChong in ids && it.idVo in ids }.forEach { noi(it.idChong, it.idVo) }
        val goc = ke.keys.minOrNull() ?: return emptySet()
        val thay = HashSet<Int>().apply { add(goc) }
        val ngan = ArrayDeque<Int>().apply { add(goc) }
        while (ngan.isNotEmpty()) {
            for (v in ke[ngan.removeLast()].orEmpty()) if (thay.add(v)) ngan.add(v)
        }
        return thay
    }

    private fun quyenXoa(o: OThanhVien): Kq.Loi? =
        if (vaiTroCuaToi(o.idKhongGian)?.vaiTro?.laQuanTri == true) null
        else loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xóa thành viên")

    override fun xemTruocXoaO(idO: Int): Kq<TacDongXoa> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenXoa(o)?.let { return it }
        val idCD = cuocDoi.filter { it.idOThanhVien == idO }.map { it.idCuocDoi }.toSet()
        val ids = oThanhVien.filter { it.idKhongGian == o.idKhongGian }.map { it.idOThanhVien }.toSet()
        val tach = (cumChinh(ids) - idO - cumChinh(ids - idO)).sorted().mapNotNull { layO(it)?.hoTen }
        return Kq.Ok(
            TacDongXoa(
                hoTen = o.hoTen,
                soChaMe = quanHeChaMe.count { it.idCon == idO },
                soVoChong = honNhan.count { it.idChong == idO || it.idVo == idO },
                soCon = quanHeChaMe.count { it.idPhuHuynh == idO },
                soThanhTuu = thanhTuu.count { it.idOThanhVien == idO },
                soBinhLuan = binhLuan.count { it.idCuocDoi in idCD },
                soTieuSu = cuocDoi.count { it.idOThanhVien == idO && !it.noiDung.isNullOrBlank() },
                tachKhoiCayChinh = tach
            )
        )
    }

    override fun xoaO(idO: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenXoa(o)?.let { return it }
        // Thứ tự như trong SQL (không dùng ON DELETE CASCADE): dữ liệu con trước, Ô sau cùng.
        val idCD = cuocDoi.filter { it.idOThanhVien == idO }.map { it.idCuocDoi }.toSet()
        binhLuan.removeAll { it.idCuocDoi in idCD }
        cuocDoi.removeAll { it.idOThanhVien == idO }
        thanhTuu.removeAll { it.idOThanhVien == idO }
        avatar.removeAll { it.idOThanhVien == idO }
        quanHeChaMe.removeAll { it.idCon == idO || it.idPhuHuynh == idO }
        honNhan.removeAll { it.idChong == idO || it.idVo == idO }
        // Tài khoản đang được gán Ô này: gỡ gán. Thành viên rơi về Thành viên hờ (cần có Ô mới là Thành viên).
        for (i in thanVien.indices) {
            val t = thanVien[i]
            if (t.idOThanhVien == idO) {
                thanVien[i] = t.copy(
                    idOThanhVien = null,
                    vaiTro = if (t.vaiTro == VaiTro.THANH_VIEN) VaiTro.THANH_VIEN_HO else t.vaiTro
                )
            }
        }
        oThanhVien.removeAll { it.idOThanhVien == idO }
        return Kq.Ok(Unit)
    }

    // ---------------- Thêm/sửa Ô (GD-07) ----------------

    private fun String?.hoacNull() = this?.trim()?.ifEmpty { null }

    private fun kiemTraForm(f: FormO): Kq.Loi? {
        if (f.hoTen.isBlank()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập họ tên")
        val homNay = java.time.LocalDate.now()
        if (f.ngaySinh != null && f.ngaySinh.isAfter(homNay))
            return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày sinh không thể ở tương lai")
        if (!f.conSong) {
            val mat = f.ngayMat ?: return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng chọn ngày mất")
            if (mat.isAfter(homNay))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày mất không thể ở tương lai")
            if (f.ngaySinh != null && mat.isBefore(f.ngaySinh))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày mất không thể trước ngày sinh")
        }
        return null
    }

    override fun themO(idKhongGian: Int, f: FormO, quanHe: QuanHeMoi?): Kq<OThanhVien> {
        if (giaLapLoi) return matKetNoi()
        if (vaiTroCuaToi(idKhongGian)?.vaiTro?.laQuanTri != true)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền thêm thành viên")
        kiemTraForm(f)?.let { return it }
        val idMoi = (oThanhVien.maxOfOrNull { it.idOThanhVien } ?: 0) + 1

        // Kiểm tra quan hệ trước khi ghi bất cứ thứ gì (như một transaction)
        val cacChaMe = mutableListOf<QuanHeChaMe>()
        var honNhanMoi: QHeHonNhan? = null
        when (quanHe) {
            null -> {}
            is QuanHeMoi.LaCon -> {
                val ds = quanHe.chaMe
                if (ds.isEmpty())
                    return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng chọn ít nhất 1 người trong tìm kiếm")
                if (ds.size > 2 || ds.map { it.idO }.toSet().size != ds.size)
                    return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Tối đa 2 cha/mẹ và không được trùng nhau")
                for (c in ds) {
                    val p = layO(c.idO)
                    if (p == null || p.idKhongGian != idKhongGian)
                        return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy người được chọn")
                    cacChaMe.add(QuanHeChaMe(idMoi, c.idO, c.loai, c.ngayXacLap))
                }
                // Tối đa một cha ruột và một mẹ ruột
                val gioiRuot = ds.filter { it.loai == LoaiQuanHe.RUOT }.mapNotNull { layO(it.idO)?.gioiTinh }
                if (gioiRuot.size == 2 && gioiRuot[0] == gioiRuot[1] && gioiRuot[0] != GioiTinh.KHAC)
                    return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Chỉ có thể có một cha ruột và một mẹ ruột")
            }
            is QuanHeMoi.LaVoChong -> {
                val kia = layO(quanHe.idNguoiKia)
                if (kia == null || kia.idKhongGian != idKhongGian)
                    return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy người được chọn")
                if (quanHe.thuTu < 1)
                    return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Thứ tự hôn nhân phải từ 1 trở lên")
                // Nam -> chồng, Nữ -> vợ; chưa rõ thì xét giới tính người kia
                val moiLaChong = when {
                    f.gioiTinh == GioiTinh.NAM -> true
                    f.gioiTinh == GioiTinh.NU -> false
                    kia.gioiTinh == GioiTinh.NAM -> false
                    else -> true
                }
                honNhanMoi = QHeHonNhan(
                    id = (honNhan.maxOfOrNull { it.id } ?: 0) + 1,
                    idChong = if (moiLaChong) idMoi else kia.idOThanhVien,
                    idVo = if (moiLaChong) kia.idOThanhVien else idMoi,
                    ngayCoi = quanHe.ngayCuoi,
                    thuTuHonNhan = quanHe.thuTu
                )
            }
        }

        val moi = OThanhVien(
            idOThanhVien = idMoi,
            idKhongGian = idKhongGian,
            hoTen = f.hoTen.trim(),
            bietDanh = f.bietDanh.hoacNull(),
            gioiTinh = f.gioiTinh,
            ngaySinh = f.ngaySinh,
            queQuan = f.queQuan.hoacNull(),
            conSong = f.conSong,
            ngayMat = if (f.conSong) null else f.ngayMat,
            cccd = f.cccd.hoacNull()
        )
        oThanhVien.add(moi)
        quanHeChaMe.addAll(cacChaMe)
        honNhanMoi?.let { honNhan.add(it) }
        return Kq.Ok(moi)
    }

    private fun boDau(s: String): String =
        java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace('đ', 'd').replace('Đ', 'D')

    override fun timO(idKhongGian: Int, tuKhoa: String): Kq<List<KetQuaTim>> {
        if (giaLapLoi) return matKetNoi()
        if (vaiTroCuaToi(idKhongGian)?.vaiTro?.laQuanTri != true)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền thực hiện thao tác này")
        return Kq.Ok(timTheoTen(idKhongGian, tuKhoa, emptySet()))
    }

    /** Tìm theo tên (không phân biệt dấu), bỏ các Ô trong `loaiTru`, kèm ngữ cảnh quan hệ. */
    private fun timTheoTen(idKhongGian: Int, tuKhoa: String, loaiTru: Set<Int>): List<KetQuaTim> {
        val q = boDau(tuKhoa.trim())
        if (q.isEmpty()) return emptyList()
        fun ten(id: Int) = oThanhVien.first { it.idOThanhVien == id }.hoTen

        return oThanhVien
            .filter {
                it.idKhongGian == idKhongGian && it.idOThanhVien !in loaiTru &&
                        boDau(it.hoTen).contains(q, ignoreCase = true)
            }
            .sortedBy { it.idOThanhVien }
            .take(20)
            .map { o ->
                val cha = quanHeChaMe.filter { it.idCon == o.idOThanhVien }
                    .sortedBy { it.idPhuHuynh }.map { ten(it.idPhuHuynh) }
                val vc = honNhan.filter { it.idChong == o.idOThanhVien || it.idVo == o.idOThanhVien }
                val nguCanh = when {
                    cha.isNotEmpty() -> "Con của: " + cha.joinToString(", ")
                    vc.isNotEmpty() -> "Vợ/chồng của: " + vc.joinToString(", ") {
                        ten(if (it.idChong == o.idOThanhVien) it.idVo else it.idChong)
                    }
                    else -> "Chưa có quan hệ"
                }
                KetQuaTim(o.idOThanhVien, o.hoTen, nguCanh, vc.size)
            }
    }

    override fun suaO(idO: Int, f: FormO): Kq<OThanhVien> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        if (vaiTroCuaToi(o.idKhongGian)?.vaiTro?.laQuanTri != true)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền sửa thành viên")
        kiemTraForm(f)?.let { return it }
        val moi = o.copy(
            hoTen = f.hoTen.trim(),
            bietDanh = f.bietDanh.hoacNull(),
            gioiTinh = f.gioiTinh,
            ngaySinh = f.ngaySinh,
            queQuan = f.queQuan.hoacNull(),
            conSong = f.conSong,
            ngayMat = if (f.conSong) null else f.ngayMat,
            cccd = f.cccd.hoacNull()
        )
        thayO(moi)
        return Kq.Ok(moi)
    }
    // ---------------- Quan hệ cha/mẹ (GD-12) ----------------

    private fun quyenQuanTri(idKhongGian: Int): Kq.Loi? =
        if (vaiTroCuaToi(idKhongGian)?.vaiTro?.laQuanTri == true) null
        else loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền thực hiện thao tác này")

    /** Mọi tổ tiên của idO, tính cả dòng đang ẩn (ẩn chỉ là cách hiển thị, quan hệ huyết thống vẫn còn). */
    private fun toTien(idO: Int): Set<Int> {
        val thay = HashSet<Int>()
        val ngan = ArrayDeque<Int>().apply { add(idO) }
        while (ngan.isNotEmpty()) {
            val x = ngan.removeLast()
            for (r in quanHeChaMe) if (r.idCon == x && thay.add(r.idPhuHuynh)) ngan.add(r.idPhuHuynh)
        }
        return thay
    }

    /** Mọi hậu duệ của idO, tính cả dòng đang ẩn. */
    private fun hauDue(idO: Int): Set<Int> {
        val thay = HashSet<Int>()
        val ngan = ArrayDeque<Int>().apply { add(idO) }
        while (ngan.isNotEmpty()) {
            val x = ngan.removeLast()
            for (r in quanHeChaMe) if (r.idPhuHuynh == x && thay.add(r.idCon)) ngan.add(r.idCon)
        }
        return thay
    }

    /**
     * Mỗi người chỉ có tối đa một cha ruột và một mẹ ruột.
     * Các dòng Ruột sẵn có của idCon (trừ chính dòng đang xét) không được trùng giới tính với idPhuHuynh.
     */
    private fun kiemTraRuot(idCon: Int, idPhuHuynh: Int, loai: LoaiQuanHe): Kq.Loi? {
        if (loai != LoaiQuanHe.RUOT) return null
        val gioi = layO(idPhuHuynh)?.gioiTinh
        val daCo = quanHeChaMe.filter {
            it.idCon == idCon && it.loai == LoaiQuanHe.RUOT && it.idPhuHuynh != idPhuHuynh
        }
        val ten = layO(idCon)?.hoTen ?: "Người này"
        if (daCo.size >= 2) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "$ten đã có đủ cha mẹ ruột")
        if (gioi == GioiTinh.NAM || gioi == GioiTinh.NU) {
            if (daCo.any { layO(it.idPhuHuynh)?.gioiTinh == gioi })
                return loi(
                    LoiApp.DU_LIEU_KHONG_HOP_LE,
                    if (gioi == GioiTinh.NAM) "$ten đã có cha ruột" else "$ten đã có mẹ ruột"
                )
        }
        return null
    }

    private fun kiemTraNgayXacLap(ngay: java.time.LocalDate?): Kq.Loi? =
        if (ngay != null && ngay.isAfter(java.time.LocalDate.now()))
            loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày xác lập không thể ở tương lai")
        else null

    override fun quanHeChaMeCua(idO: Int): Kq<QuanHeChaMeCuaO> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        fun dong(r: QuanHeChaMe, idNguoi: Int): DongQuanHeChaMe? =
            layO(idNguoi)?.let { DongQuanHeChaMe(it.idOThanhVien, it.hoTen, it.conSong, r.loai, r.ngayXacLap) }

        val chaMe = quanHeChaMe.filter { it.idCon == idO }
            .sortedWith(compareBy({ it.loai.ordinal }, { it.idPhuHuynh }))
            .mapNotNull { dong(it, it.idPhuHuynh) }
        val con = quanHeChaMe.filter { it.idPhuHuynh == idO }
            .sortedWith(compareBy({ layO(it.idCon)?.ngaySinh ?: java.time.LocalDate.MAX }, { it.idCon }))
            .mapNotNull { dong(it, it.idCon) }
        return Kq.Ok(QuanHeChaMeCuaO(o, chaMe, con))
    }

    override fun timUngVienChaMe(idO: Int, chieu: ChieuQuanHe, tuKhoa: String): Kq<List<KetQuaTim>> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val loaiTru: Set<Int> = when (chieu) {
            // Thêm cha/mẹ: loại chính Ô, hậu duệ của Ô (vòng lặp) và cha/mẹ đã nối
            ChieuQuanHe.LA_CON ->
                hauDue(idO) + idO + quanHeChaMe.filter { it.idCon == idO }.map { it.idPhuHuynh }
            // Thêm con: loại chính Ô, tổ tiên của Ô (vòng lặp) và con đã nối
            ChieuQuanHe.LA_PHU_HUYNH ->
                toTien(idO) + idO + quanHeChaMe.filter { it.idPhuHuynh == idO }.map { it.idCon }
        }
        return Kq.Ok(timTheoTen(o.idKhongGian, tuKhoa, loaiTru))
    }

    override fun themQuanHeChaMe(
        idO: Int, chieu: ChieuQuanHe, idNguoiKia: Int, loai: LoaiQuanHe, ngayXacLap: java.time.LocalDate?
    ): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val kia = layO(idNguoiKia)
        if (kia == null || kia.idKhongGian != o.idKhongGian)
            return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy người được chọn")
        if (idNguoiKia == idO) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Không thể chọn chính người này")

        val idCon = if (chieu == ChieuQuanHe.LA_CON) idO else idNguoiKia
        val idPhuHuynh = if (chieu == ChieuQuanHe.LA_CON) idNguoiKia else idO
        if (quanHeChaMe.any { it.idCon == idCon && it.idPhuHuynh == idPhuHuynh })
            return loi(LoiApp.DA_TON_TAI, "Quan hệ này đã tồn tại")
        // Thêm dòng phụ huynh → con tạo vòng khi con vốn là tổ tiên của phụ huynh
        if (idCon in toTien(idPhuHuynh))
            return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Không thể chọn — sẽ tạo vòng lặp huyết thống")
        kiemTraRuot(idCon, idPhuHuynh, loai)?.let { return it }
        kiemTraNgayXacLap(ngayXacLap)?.let { return it }

        quanHeChaMe.add(QuanHeChaMe(idCon, idPhuHuynh, loai, ngayXacLap))
        return Kq.Ok(Unit)
    }

    override fun suaQuanHeChaMe(
        idCon: Int, idPhuHuynh: Int, loai: LoaiQuanHe, ngayXacLap: java.time.LocalDate?
    ): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val i = quanHeChaMe.indexOfFirst { it.idCon == idCon && it.idPhuHuynh == idPhuHuynh }
        val con = layO(idCon)
        if (i < 0 || con == null) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy quan hệ này")
        quyenQuanTri(con.idKhongGian)?.let { return it }
        kiemTraRuot(idCon, idPhuHuynh, loai)?.let { return it }
        kiemTraNgayXacLap(ngayXacLap)?.let { return it }
        quanHeChaMe[i] = quanHeChaMe[i].copy(loai = loai, ngayXacLap = ngayXacLap)
        return Kq.Ok(Unit)
    }

    override fun xoaQuanHeChaMe(idCon: Int, idPhuHuynh: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val con = layO(idCon)
        if (con == null || quanHeChaMe.none { it.idCon == idCon && it.idPhuHuynh == idPhuHuynh })
            return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy quan hệ này")
        quyenQuanTri(con.idKhongGian)?.let { return it }
        quanHeChaMe.removeAll { it.idCon == idCon && it.idPhuHuynh == idPhuHuynh }
        return Kq.Ok(Unit)
    }

    // ---------------- Quan hệ hôn nhân (GD-13) ----------------

    private fun kiemTraNgayCuoi(ngay: java.time.LocalDate?, a: OThanhVien, b: OThanhVien): Kq.Loi? {
        if (ngay == null) return null
        if (ngay.isAfter(java.time.LocalDate.now()))
            return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày cưới không thể ở tương lai")
        for (p in listOf(a, b)) {
            if (p.ngaySinh != null && ngay.isBefore(p.ngaySinh))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày cưới không thể trước ngày sinh của ${p.hoTen}")
        }
        return null
    }

    override fun honNhanCua(idO: Int): Kq<HonNhanCuaO> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val ds = honNhan.filter { it.idChong == idO || it.idVo == idO }
            .sortedWith(compareBy({ it.thuTuHonNhan ?: Int.MAX_VALUE }, { it.id }))
            .mapNotNull { h ->
                layO(if (h.idChong == idO) h.idVo else h.idChong)?.let {
                    DongHonNhan(h.id, it.idOThanhVien, it.hoTen, it.conSong, h.trangThai, h.ngayCoi, h.ngayLyHon, h.thuTuHonNhan)
                }
            }
        return Kq.Ok(HonNhanCuaO(o, ds))
    }

    override fun timUngVienHonNhan(idO: Int, tuKhoa: String): Kq<List<KetQuaTim>> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val daNoi = honNhan.filter { it.idChong == idO || it.idVo == idO }
            .map { if (it.idChong == idO) it.idVo else it.idChong }
        return Kq.Ok(timTheoTen(o.idKhongGian, tuKhoa, daNoi.toSet() + idO))
    }

    override fun themHonNhan(idO: Int, idNguoiKia: Int, ngayCoi: java.time.LocalDate?, thuTu: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val kia = layO(idNguoiKia)
        if (kia == null || kia.idKhongGian != o.idKhongGian)
            return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy người được chọn")
        if (idNguoiKia == idO) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Không thể chọn chính người này")
        if (honNhan.any {
                (it.idChong == idO && it.idVo == idNguoiKia) || (it.idChong == idNguoiKia && it.idVo == idO)
            }) return loi(LoiApp.DA_TON_TAI, "Hai người này đã có quan hệ hôn nhân")
        if (thuTu < 1) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Thứ tự hôn nhân phải từ 1 trở lên")
        kiemTraNgayCuoi(ngayCoi, o, kia)?.let { return it }

        // Nam -> chồng, Nữ -> vợ; chưa rõ thì xét giới tính người kia (giống themO)
        val oLaChong = when {
            o.gioiTinh == GioiTinh.NAM -> true
            o.gioiTinh == GioiTinh.NU -> false
            kia.gioiTinh == GioiTinh.NAM -> false
            else -> true
        }
        honNhan.add(
            QHeHonNhan(
                id = (honNhan.maxOfOrNull { it.id } ?: 0) + 1,
                idChong = if (oLaChong) idO else idNguoiKia,
                idVo = if (oLaChong) idNguoiKia else idO,
                ngayCoi = ngayCoi,
                thuTuHonNhan = thuTu
            )
        )
        return Kq.Ok(Unit)
    }

    override fun suaHonNhan(
        id: Int, ngayCoi: java.time.LocalDate?, trangThai: TrangThaiHonNhan,
        ngayLyHon: java.time.LocalDate?, thuTu: Int
    ): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val i = honNhan.indexOfFirst { it.id == id }
        if (i < 0) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        val h = honNhan[i]
        val chong = layO(h.idChong)
        val vo = layO(h.idVo)
        if (chong == null || vo == null) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        quyenQuanTri(chong.idKhongGian)?.let { return it }
        if (thuTu < 1) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Thứ tự hôn nhân phải từ 1 trở lên")
        kiemTraNgayCuoi(ngayCoi, chong, vo)?.let { return it }
        val ly = if (trangThai == TrangThaiHonNhan.DA_LY_HON) ngayLyHon else null
        if (ly != null) {
            if (ly.isAfter(java.time.LocalDate.now()))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày ly hôn không thể ở tương lai")
            if (ngayCoi != null && ly.isBefore(ngayCoi))
                return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Ngày ly hôn không thể trước ngày cưới")
        }
        honNhan[i] = h.copy(ngayCoi = ngayCoi, trangThai = trangThai, ngayLyHon = ly, thuTuHonNhan = thuTu)
        return Kq.Ok(Unit)
    }

    override fun xoaHonNhan(id: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val h = honNhan.firstOrNull { it.id == id } ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        val chong = layO(h.idChong) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        quyenQuanTri(chong.idKhongGian)?.let { return it }
        honNhan.removeAll { it.id == id }
        return Kq.Ok(Unit)
    }

    // ---------------- Ẩn/hiện lại quan hệ (8b) ----------------

    override fun anQuanHeChaMe(idCon: Int, idPhuHuynh: List<Int>, an: Boolean): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val con = layO(idCon) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy người này")
        quyenQuanTri(con.idKhongGian)?.let { return it }
        val viTri = quanHeChaMe.indices.filter {
            quanHeChaMe[it].idCon == idCon && quanHeChaMe[it].idPhuHuynh in idPhuHuynh
        }
        if (viTri.isEmpty()) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy quan hệ này")
        viTri.forEach { quanHeChaMe[it] = quanHeChaMe[it].copy(hienThiLienKet = !an) }
        return Kq.Ok(Unit)
    }

    override fun anHonNhan(idHonNhan: Int, an: Boolean): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val i = honNhan.indexOfFirst { it.id == idHonNhan }
        if (i < 0) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        val chong = layO(honNhan[i].idChong) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy hôn nhân này")
        quyenQuanTri(chong.idKhongGian)?.let { return it }
        honNhan[i] = honNhan[i].copy(hienThiLienKet = !an)
        return Kq.Ok(Unit)
    }
    // ---------------- Tiểu sử và bình luận (GD-09) ----------------

    private fun tenTaiKhoan(id: Int) = taiKhoan.firstOrNull { it.idTaiKhoan == id }?.tenHienThi ?: "Không rõ"

    override fun tieuSuVaBinhLuan(idO: Int): Kq<TieuSuVaBinhLuan> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        val toi = vaiTroCuaToi(o.idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xem không gian này")
        val kg = khongGian.first { it.idKhongGian == o.idKhongGian }
        if (kg.cheDoRiengTu && toi.vaiTro == VaiTro.KHACH_TAM)
            return loi(LoiApp.KHONG_GIAN_RIENG_TU, "Không gian này đang ở chế độ riêng tư")

        val cd = cuocDoi.firstOrNull { it.idOThanhVien == idO }
        val noiDung = cd?.noiDung?.takeIf { it.isNotBlank() }
        val tieuSu = if (cd != null && noiDung != null)
            TieuSuO(noiDung, tenTaiKhoan(cd.idNguoiViet), cd.ngayCapNhat) else null
        val ds = if (cd == null) emptyList() else binhLuan
            .filter { it.idCuocDoi == cd.idCuocDoi }
            .sortedWith(compareByDescending<BinhLuan> { it.ngayBinhLuan }.thenByDescending { it.idBinhLuan })
            .map {
                DongBinhLuan(
                    id = it.idBinhLuan, idNguoi = it.idNguoiBinhLuan, tenNguoi = tenTaiKhoan(it.idNguoiBinhLuan),
                    noiDung = it.noiDung, thoiGian = it.ngayBinhLuan,
                    xoaDuoc = toi.vaiTro.laQuanTri || it.idNguoiBinhLuan == toi.idTaiKhoan
                )
            }
        return Kq.Ok(TieuSuVaBinhLuan(o, toi, tieuSu, ds))
    }

    override fun luuTieuSu(idO: Int, noiDung: String): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        val nd = noiDung.trim()
        if (nd.isEmpty()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập nội dung tiểu sử")
        val tk = nhanDang() ?: return loi(LoiApp.CHUA_DANG_NHAP, "Vui lòng đăng nhập lại")
        val bay = LocalDateTime.now()
        val i = cuocDoi.indexOfFirst { it.idOThanhVien == idO }
        if (i < 0) {
            cuocDoi.add(
                CuocDoi(
                    idCuocDoi = (cuocDoi.maxOfOrNull { it.idCuocDoi } ?: 0) + 1,
                    idOThanhVien = idO, idNguoiViet = tk.idTaiKhoan, noiDung = nd, ngayCapNhat = bay
                )
            )
        } else {
            val cu = cuocDoi[i]
            // Bản rỗng do bình luận tạo ra: người lưu đầu tiên là người viết. Sửa bản có sẵn: giữ người viết cũ.
            cuocDoi[i] = cu.copy(
                noiDung = nd, ngayCapNhat = bay,
                idNguoiViet = if (cu.noiDung.isNullOrBlank()) tk.idTaiKhoan else cu.idNguoiViet
            )
        }
        return Kq.Ok(Unit)
    }

    override fun themBinhLuan(idO: Int, noiDung: String): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        val toi = vaiTroCuaToi(o.idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền bình luận")
        if (toi.vaiTro != VaiTro.ADMIN && toi.vaiTro != VaiTro.QUAN_LY && toi.vaiTro != VaiTro.THANH_VIEN)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền bình luận")
        val nd = noiDung.trim()
        if (nd.isEmpty()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập nội dung bình luận")
        val bay = LocalDateTime.now()
        val cd = cuocDoi.firstOrNull { it.idOThanhVien == idO }
            ?: CuocDoi(
                idCuocDoi = (cuocDoi.maxOfOrNull { it.idCuocDoi } ?: 0) + 1,
                idOThanhVien = idO, idNguoiViet = toi.idTaiKhoan, noiDung = null, ngayCapNhat = bay
            ).also { cuocDoi.add(it) }
        binhLuan.add(
            BinhLuan(
                idBinhLuan = (binhLuan.maxOfOrNull { it.idBinhLuan } ?: 0) + 1,
                idCuocDoi = cd.idCuocDoi, idNguoiBinhLuan = toi.idTaiKhoan, noiDung = nd, ngayBinhLuan = bay
            )
        )
        return Kq.Ok(Unit)
    }

    override fun xoaBinhLuan(idBinhLuan: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val bl = binhLuan.firstOrNull { it.idBinhLuan == idBinhLuan }
            ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy bình luận này")
        val o = cuocDoi.firstOrNull { it.idCuocDoi == bl.idCuocDoi }?.let { layO(it.idOThanhVien) }
            ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy bình luận này")
        val toi = vaiTroCuaToi(o.idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xóa bình luận này")
        if (!toi.vaiTro.laQuanTri && bl.idNguoiBinhLuan != toi.idTaiKhoan)
            return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xóa bình luận này")
        binhLuan.removeAll { it.idBinhLuan == idBinhLuan }
        return Kq.Ok(Unit)
    }
    // ---------------- Thành tựu (GD-10) ----------------

    private fun kiemTraThanhTuu(ten: String, nam: Int?): Kq.Loi? {
        if (ten.isBlank()) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Vui lòng nhập tên thành tựu")
        if (ten.trim().length > 200) return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Tên thành tựu tối đa 200 ký tự")
        if (nam != null && (nam < 1800 || nam > java.time.Year.now().value))
            return loi(LoiApp.DU_LIEU_KHONG_HOP_LE, "Năm không hợp lệ")
        return null
    }

    override fun thanhTuuCua(idO: Int): Kq<DanhSachThanhTuu> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        val toi = vaiTroCuaToi(o.idKhongGian) ?: return loi(LoiApp.KHONG_CO_QUYEN, "Bạn không có quyền xem không gian này")
        val kg = khongGian.first { it.idKhongGian == o.idKhongGian }
        if (kg.cheDoRiengTu && toi.vaiTro == VaiTro.KHACH_TAM)
            return loi(LoiApp.KHONG_GIAN_RIENG_TU, "Không gian này đang ở chế độ riêng tư")
        val ds = thanhTuu.filter { it.idOThanhVien == idO }
            .sortedWith(compareByDescending<ThanhTuu> { it.nam ?: Int.MIN_VALUE }.thenByDescending { it.idThanhTuu })
        return Kq.Ok(DanhSachThanhTuu(o, toi, ds))
    }

    override fun themThanhTuu(idO: Int, ten: String, nam: Int?, moTa: String?): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val o = layO(idO) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        kiemTraThanhTuu(ten, nam)?.let { return it }
        thanhTuu.add(
            ThanhTuu(
                idThanhTuu = (thanhTuu.maxOfOrNull { it.idThanhTuu } ?: 0) + 1,
                idOThanhVien = idO, tenThanhTuu = ten.trim(), moTa = moTa.hoacNull(), nam = nam
            )
        )
        return Kq.Ok(Unit)
    }

    override fun suaThanhTuu(idThanhTuu: Int, ten: String, nam: Int?, moTa: String?): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val i = thanhTuu.indexOfFirst { it.idThanhTuu == idThanhTuu }
        if (i < 0) return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành tựu này")
        val o = layO(thanhTuu[i].idOThanhVien) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        kiemTraThanhTuu(ten, nam)?.let { return it }
        thanhTuu[i] = thanhTuu[i].copy(tenThanhTuu = ten.trim(), moTa = moTa.hoacNull(), nam = nam)
        return Kq.Ok(Unit)
    }

    override fun xoaThanhTuu(idThanhTuu: Int): Kq<Unit> {
        if (giaLapLoi) return matKetNoi()
        val t = thanhTuu.firstOrNull { it.idThanhTuu == idThanhTuu }
            ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành tựu này")
        val o = layO(t.idOThanhVien) ?: return loi(LoiApp.KHONG_TIM_THAY, "Không tìm thấy thành viên này")
        quyenQuanTri(o.idKhongGian)?.let { return it }
        thanhTuu.removeAll { it.idThanhTuu == idThanhTuu }
        return Kq.Ok(Unit)
    }
}
