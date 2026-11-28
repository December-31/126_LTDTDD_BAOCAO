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
                soTieuSu = idCD.size,
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


}
