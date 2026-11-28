package com.example.giapha.data

data class DuongAn(
    val tenCap: String,              // "Con — Cha & Mẹ" hoặc "Chồng — Vợ"
    val loai: String,                // "Cha/mẹ–con · Ruột", "Hôn nhân · Đang kết hôn"
    val idCon: Int?,
    val idPhuHuynh: List<Int>,
    val idHonNhan: Int?
)

/** Các đường đang ẩn của một cây, theo thứ tự: cha/mẹ–con (theo tên con), rồi hôn nhân. Chỉ đọc, không đổi dữ liệu. */
fun danhSachDuongAn(du: DuLieuCay): List<DuongAn> {
    val ten = du.oThanhVien.associate { it.idOThanhVien to it.hoTen }
    fun t(id: Int) = ten[id] ?: "?"
    val capDaCuoi = du.honNhan.map { setOf(it.idChong, it.idVo) }.toSet()

    val kq = mutableListOf<DuongAn>()
    val theoCon = du.quanHeChaMe.filter { !it.hienThiLienKet }.groupBy { it.idCon }
    for (idCon in theoCon.keys.sortedBy { t(it) }) {
        val dong = theoCon.getValue(idCon).sortedBy { it.idPhuHuynh }
        val nhom = if (dong.size == 2 && setOf(dong[0].idPhuHuynh, dong[1].idPhuHuynh) in capDaCuoi) listOf(dong)
        else dong.map { listOf(it) }
        for (g in nhom) {
            kq += DuongAn(
                tenCap = "${t(idCon)} — ${g.joinToString(" & ") { t(it.idPhuHuynh) }}",
                loai = "Cha/mẹ–con · " + g.map { it.loai.nhan }.distinct().joinToString(" / "),
                idCon = idCon,
                idPhuHuynh = g.map { it.idPhuHuynh },
                idHonNhan = null
            )
        }
    }
    for (h in du.honNhan.filter { !it.hienThiLienKet }.sortedBy { t(it.idChong) }) {
        kq += DuongAn(
            tenCap = "${t(h.idChong)} — ${t(h.idVo)}",
            loai = "Hôn nhân · ${h.trangThai.nhan}",
            idCon = null, idPhuHuynh = emptyList(), idHonNhan = h.id
        )
    }
    return kq
}