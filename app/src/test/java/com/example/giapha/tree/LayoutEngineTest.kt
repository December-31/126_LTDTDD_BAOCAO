package com.example.giapha.tree

import com.example.giapha.data.SampleData
import com.example.giapha.model.OThanhVien
import com.example.giapha.model.QHeHonNhan
import com.example.giapha.model.QuanHeChaMe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Kiểm tra LayoutEngine trên dữ liệu mẫu (chạy bằng JVM, không cần máy ảo Android). */
class LayoutEngineTest {

    private class Cay(val o: List<OThanhVien>, val ket: KetQuaBoCuc)

    private fun cay(idKhongGian: Int): Cay {
        val o = SampleData.oThanhVien.filter { it.idKhongGian == idKhongGian }
        val ids = o.map { it.idOThanhVien }.toSet()
        val qh: List<QuanHeChaMe> = SampleData.quanHeChaMe.filter { it.idCon in ids && it.idPhuHuynh in ids }
        val hn: List<QHeHonNhan> = SampleData.honNhan.filter { it.idChong in ids && it.idVo in ids }
        return Cay(o, LayoutEngine.tinh(o, qh, hn))
    }

    private val tatCa get() = SampleData.khongGian.map { it.idKhongGian }

    @Test
    fun moiOMotThe() {
        for (kg in tatCa) {
            val c = cay(kg)
            val tatCaId = c.o.map { it.idOThanhVien }.toSet()
            val coThe = c.ket.the.map { it.id }.toSet()
            val coDongAn = SampleData.quanHeChaMe.any { !it.hienThiLienKet && it.idCon in tatCaId }
            if (coDongAn) assertTrue("không gian $kg", coThe.size < tatCaId.size && tatCaId.containsAll(coThe))
            else assertEquals("không gian $kg", tatCaId, coThe)
        }
    }

    @Test
    fun theKhongDeLenNhau() {
        for (kg in tatCa) {
            val t = cay(kg).ket.the
            for (i in t.indices) for (j in i + 1 until t.size) {
                val a = t[i]; val b = t[j]
                val de = a.x < b.x + 64 && b.x < a.x + 64 && a.y < b.y + 80 && b.y < a.y + 80
                assertTrue("không gian $kg: thẻ ${a.id} đè thẻ ${b.id}", !de)
            }
        }
    }

    @Test
    fun duongNoiKhongXuyenQuaThe() {
        for (kg in tatCa) {
            val k = cay(kg).ket
            for (n in k.net) for (c in k.the) {
                val x0 = minOf(n.x1, n.x2); val x1 = maxOf(n.x1, n.x2)
                val y0 = minOf(n.y1, n.y2); val y1 = maxOf(n.y1, n.y2)
                val xuyen = x1 > c.x + 0.5f && x0 < c.x + 63.5f && y1 > c.y + 0.5f && y0 < c.y + 79.5f
                assertTrue("không gian $kg: đường (${n.x1},${n.y1})-(${n.x2},${n.y2}) xuyên thẻ ${c.id}", !xuyen)
            }
        }
    }

    @Test
    fun soDoiTheoMoiDongNoi() {
        val d = cay(1).ket.the.associate { it.id to it.doi }
        assertEquals(1, d[1])     // Nguyễn Văn Tín
        assertEquals(2, d[3])     // Nguyễn Văn Hòa
        assertEquals(4, d[18])    // Nguyễn Minh Khôi
        assertEquals(5, d[21])    // Nguyễn Gia Hân
    }

    @Test
    fun nhanhTachNamRiengVaCoTieuDe() {
        val k = cay(1).ket
        assertTrue("nhánh Nguyễn Công Đạt đang ẩn thì không vẽ", k.the.none { it.id == 15 })
        assertEquals(0, k.tieuDe.count { it.chu == "Quan hệ ẩn" })              // nhánh Nguyễn Công Đạt
        assertEquals(1, k.tieuDe.count { it.chu == "Chưa nối với cây chính" })   // cụm Võ Văn Tâm, Võ Minh Quân
    }

    @Test
    fun canHuyetChiToDoDungHaiHonNhan() {
        assertEquals(0, cay(1).ket.net.count { it.canHuyet })
        assertEquals(2, cay(3).ket.net.count { it.canHuyet })    // Cần–Phượng, An–Kim; Hải–Bích không đỏ
    }

    @Test
    fun dauLyHonVaChamHonNhan() {
        assertEquals(1, cay(1).ket.dauHuy.size)
        assertEquals(2, cay(1).ket.chamHonNhan.size)             // Phúc có 2 đời vợ
        assertEquals(3, cay(3).ket.dauHuy.size)
        assertEquals(6, cay(3).ket.chamHonNhan.size)             // Đại 4 đời vợ + Nga 2 đời chồng
    }

    @Test
    fun oKhongNoiDuocThuocKhuChuaRoViTri() {
        val o = listOf(
            OThanhVien(idOThanhVien = 1, idKhongGian = 1, hoTen = "A"),
            OThanhVien(idOThanhVien = 2, idKhongGian = 1, hoTen = "B")
        )
        val k = LayoutEngine.tinh(o, emptyList(), emptyList())
        assertEquals(2, k.the.size)
        assertTrue(k.the.all { it.doi == null })
        assertTrue(k.tieuDe.any { it.chu == "Chưa rõ vị trí" })
    }

    // ---- 8b lượt 1: ẩn hôn nhân và đoạn nối chọn được ----

    /** Họ Nguyễn, ẩn hôn nhân Nam (8) – Hạnh (9), id hôn nhân 5. */
    private fun cayAnHonNhanNamHanh(): KetQuaBoCuc {
        val o = SampleData.oThanhVien.filter { it.idKhongGian == 1 }
        val ids = o.map { it.idOThanhVien }.toSet()
        val qh = SampleData.quanHeChaMe.filter { it.idCon in ids && it.idPhuHuynh in ids }
        val hn = SampleData.honNhan.filter { it.idChong in ids && it.idVo in ids }
            .map { if (it.id == 5) it.copy(hienThiLienKet = false) else it }
        return LayoutEngine.tinh(o, qh, hn)
    }

    @Test
    fun anHonNhanDuaBenDiLayChongVaConVaoCumAn() {
        val k = cayAnHonNhanNamHanh()
        val the = k.the.associateBy { it.id }
        val nam = the[8]!!
        assertTrue(nam.id == 8)
        for (id in listOf(9, 18, 19, 20, 21)) assertTrue("Ô $id không được vẽ", id !in the)
        assertEquals(cay(1).ket.the.size - 5, k.the.size)
        assertEquals(0, k.tieuDe.count { it.chu == "Quan hệ ẩn" })
        // đường hôn nhân đã ẩn không còn trong danh sách đoạn nối
        assertTrue(k.net.none { (it.nut as? NutQuanHe.HonNhan)?.idHonNhan == 5 })
    }

    @Test
    fun honNhanChuaAnThiDoanNoiChonDuoc() {
        val k = cay(1).ket
        val honNhan5 = k.net.filter { (it.nut as? NutQuanHe.HonNhan)?.idHonNhan == 5 }
        assertEquals(1, honNhan5.size)
        val d = honNhan5[0]
        val nut = k.timNut((d.x1 + d.x2) / 2, (d.y1 + d.y2) / 2, 4f)
        assertEquals(5, (nut as NutQuanHe.HonNhan).idHonNhan)
    }

    @Test
    fun doanDanVaoTheConMangDungCaHaiDongChaMe() {
        val k = cay(1).ket
        val khoi = k.net.mapNotNull { it.nut as? NutQuanHe.ChaMeCon }.first { it.idCon == 18 }
        assertEquals(setOf(8, 9), khoi.idPhuHuynh.toSet())     // nét T chung = hai dòng cha và mẹ
        val ma = k.the.first { it.id == 18 }
        val gan = k.timNut(ma.x + 32f, ma.y - 6f, 4f) as NutQuanHe.ChaMeCon
        assertEquals(18, gan.idCon)
    }

    @Test
    fun khongChamTrungDuongThiKhongChonDuoc() {
        val k = cay(1).ket
        assertEquals(null, k.timNut(-500f, -500f, 6f))
    }

    @Test
    fun khongCoOThiCanvasRong() {
        val k = LayoutEngine.tinh(emptyList(), emptyList(), emptyList())
        assertTrue(k.the.isEmpty())
        assertEquals(0f, k.rong)
    }
}