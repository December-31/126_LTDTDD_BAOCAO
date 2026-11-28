package com.example.giapha.tree

import com.example.giapha.data.DuLieuCay
import com.example.giapha.model.LoaiQuanHe
import com.example.giapha.model.OThanhVien
import com.example.giapha.model.QHeHonNhan
import com.example.giapha.model.QuanHeChaMe
import com.example.giapha.model.TrangThaiHonNhan
import java.time.LocalDate
import java.util.TreeMap


/** Chỉ số vẽ cây, đổi ở đây là cây đổi theo. */
data class CauHinhCay(
    val theRong: Float = 64f,
    val theCao: Float = 80f,
    val khoangCachThe: Float = 28f,        // giữa hai thẻ không phải vợ chồng
    val khoangCachNhom: Float = 44f,       // giữa hai nhóm con (chừa chỗ cho nền nhóm)
    val duongVoChong: Float = 40f,         // đường ngang nối vợ chồng
    val khoangCachHang: Float = 64f,       // đáy thẻ cha mẹ -> đỉnh thẻ con
    val khoangCachHangDaHonNhan: Float = 88f,
    val honNhanDuoiThe: Float = 28f,       // đường hôn nhân (đa hôn nhân) cách đáy thẻ
    val thanhNgangTruocCon: Float = 32f,   // thanh ngang cách đỉnh thẻ con (nhóm hôn nhân)
    val thanhNgangMotCha: Float = 20f,     // thanh ngang của con chỉ có một cha/mẹ
    val thanhNgangNoiPhu: Float = 46f,     // thanh ngang của đường nối phụ (người có cha mẹ ở hai nhánh)
    val leTrai: Float = 88f,               // chừa chỗ cho dải "Đời n"
    val lePhai: Float = 24f,
    val dem: Float = 16f
)

enum class KieuNet { LIEN, DUT }

/** x, y là góc trên trái của thẻ. doi = null với Ô trong khu "Chưa rõ vị trí". */
class TheTrenCay(val id: Int, val x: Float, val y: Float, val doi: Int?)

class DoanNoi(
    val x1: Float, val y1: Float, val x2: Float, val y2: Float,
    val kieu: KieuNet,
    val canHuyet: Boolean
)

class Diem(val x: Float, val y: Float)

/** Nền nhóm con trong đa hôn nhân. chiSo chẵn = xanh nhạt, lẻ = kem. */
class NenNhom(val x: Float, val y: Float, val rong: Float, val cao: Float, val chiSo: Int)

class DaiDoi(val doi: Int, val y: Float, val cao: Float)

class TieuDeCum(val chu: String, val x: Float, val y: Float)

class KetQuaBoCuc(
    val the: List<TheTrenCay>,
    val net: List<DoanNoi>,
    val chamHonNhan: List<Diem>,
    val dauHuy: List<Diem>,           // dấu "×" ly hôn
    val nen: List<NenNhom>,
    val dai: List<DaiDoi>,
    val tieuDe: List<TieuDeCum>,
    val rong: Float,
    val cao: Float,
    val canhBao: List<String>
)

object LayoutEngine {

    fun tinh(
        oThanhVien: List<OThanhVien>,
        quanHeChaMe: List<QuanHeChaMe>,
        honNhan: List<QHeHonNhan>,
        cauHinh: CauHinhCay = CauHinhCay()
    ): KetQuaBoCuc = Tinh(oThanhVien, quanHeChaMe, honNhan, cauHinh).chay()

    fun tinh(du: DuLieuCay, cauHinh: CauHinhCay = CauHinhCay()): KetQuaBoCuc =
        tinh(du.oThanhVien, du.quanHeChaMe, du.honNhan, cauHinh)
}

// ---------------------------------------------------------------------------------------------

/** Một mảnh cây đã xếp: vị trí x (tâm thẻ) và đường biên trái/phải theo từng đời. */
private class Manh {
    val xs = HashMap<Int, Float>()
    val bien = TreeMap<Int, FloatArray>()   // đời -> [trái, phải]
    var neo = 0f                            // tâm thẻ chính (thẻ nối lên cha mẹ)

    fun trong() = bien.isEmpty()

    fun them(id: Int, x: Float, doi: Int, nuaRong: Float) {
        xs[id] = x
        val b = bien.getOrPut(doi) { floatArrayOf(Float.MAX_VALUE, -Float.MAX_VALUE) }
        b[0] = minOf(b[0], x - nuaRong)
        b[1] = maxOf(b[1], x + nuaRong)
    }

    fun dich(dx: Float) {
        xs.replaceAll { _, v -> v + dx }
        bien.values.forEach { it[0] += dx; it[1] += dx }
        neo += dx
    }

    fun gop(o: Manh) {
        xs.putAll(o.xs)
        for ((d, b) in o.bien) {
            val m = bien[d]
            if (m == null) bien[d] = b.copyOf()
            else { m[0] = minOf(m[0], b[0]); m[1] = maxOf(m[1], b[1]) }
        }
    }

    fun traiNhat() = bien.values.minOf { it[0] }
    fun phaiNhat() = bien.values.maxOf { it[1] }
}

/** Khoảng cần dịch mảnh p sang phải để cách mảnh acc ít nhất `gap` ở mọi đời chung. */
private fun canDich(acc: Manh, p: Manh, gap: Float): Float {
    var can = Float.NEGATIVE_INFINITY
    for ((d, b) in p.bien) {
        val a = acc.bien[d] ?: continue
        can = maxOf(can, a[1] + gap - b[0])
    }
    if (can == Float.NEGATIVE_INFINITY) can = acc.phaiNhat() + gap - p.traiNhat()
    return can
}

/** Ô đặt chỗ con của một khối: chỗ hôn nhân (chấm / giữa hai vợ chồng) hoặc chỗ của một người. */
private class Cho(val honNhanCho: Boolean, val chiSo: Int, val hn: QHeHonNhan?) {
    val con = mutableListOf<Khoi>()
}

/** Chỗ một khối (hoặc người) gắn vào cha mẹ. */
private class Gan(val cho: Cho, val khoi: Khoi, val links: List<QuanHeChaMe>)

/** Khối = một người, hoặc người cùng các vợ/chồng xếp trên một hàng. ms[0] là người chung. */
private class Khoi(val ms: List<Int>, val hns: List<QHeHonNhan?>) {
    val cho: List<Cho> = buildList {
        for (i in ms.indices) {
            add(Cho(false, i, null))
            if (i + 1 < ms.size) add(Cho(true, i + 1, hns[i + 1]))
        }
    }
    var chiSoChinh = 0
    var chinh: Gan? = null
    val phu = mutableListOf<Pair<Int, Gan>>()    // (chỉ số thành viên, nơi gắn phụ)
    val nhieuHonNhan get() = ms.size >= 3
}

private class Tinh(
    oThanhVien: List<OThanhVien>,
    quanHeChaMe: List<QuanHeChaMe>,
    honNhan: List<QHeHonNhan>,
    private val ch: CauHinhCay
) {
    private val byId = oThanhVien.associateBy { it.idOThanhVien }
    private val ids = byId.keys
    private val linksAll = quanHeChaMe
        .filter { it.idCon in ids && it.idPhuHuynh in ids && it.idCon != it.idPhuHuynh }
        .distinctBy { it.idCon to it.idPhuHuynh }
    private val linksVis = linksAll.filter { it.hienThiLienKet }
    private val marriages = honNhan
        .filter { it.idChong in ids && it.idVo in ids && it.idChong != it.idVo }
        .distinctBy { setOf(it.idChong, it.idVo) }

    private val canhBao = mutableListOf<String>()
    private val doi = HashMap<Int, Int>()
    private val khoiCua = HashMap<Int, Khoi>()
    private val coLienKet = HashSet<Int>()

    private val w = ch.theRong
    private val h = ch.theCao
    private val nua = ch.theRong / 2

    // ---- đầu ra ----
    private val oThe = mutableListOf<TheTrenCay>()
    private val oNetLien = mutableListOf<DoanNoi>()
    private val oNetDut = mutableListOf<DoanNoi>()
    private val oCham = mutableListOf<Diem>()
    private val oDauHuy = mutableListOf<Diem>()
    private val oNen = mutableListOf<NenNhom>()
    private val oDai = mutableListOf<DaiDoi>()
    private val oTieuDe = mutableListOf<TieuDeCum>()
    private var phaiNhatCanvas = 0f

    fun chay(): KetQuaBoCuc {
        tinhDoi()
        taoKhoi()
        ganChaMe()

        val cum = components(adjacency(linksVis, marriages), coLienKet.sorted())
        val chinh = cum.minByOrNull { it.min() }
        val thuTu = listOfNotNull(chinh) + cum.filter { it !== chinh }.sortedBy { it.min() }

        var y = ch.dem
        thuTu.forEachIndexed { i, c ->
            y = xepCum(c, y, if (i == 0) null else tieuDeCum(c)) + 28f
        }
        y = xepChuaRoViTri(y)

        val cao = if (oThe.isEmpty()) 0f else y
        val rong = if (oThe.isEmpty()) 0f else phaiNhatCanvas + ch.lePhai
        return KetQuaBoCuc(
            the = oThe, net = oNetDut + oNetLien, chamHonNhan = oCham, dauHuy = oDauHuy,
            nen = oNen, dai = oDai, tieuDe = oTieuDe, rong = rong, cao = cao, canhBao = canhBao
        )
    }

    // ---------------------------------------------------------------- số đời

    private fun adjacency(links: List<QuanHeChaMe>, ms: List<QHeHonNhan>): Map<Int, List<Int>> {
        val adj = HashMap<Int, MutableList<Int>>()
        fun noi(a: Int, b: Int) {
            adj.getOrPut(a) { mutableListOf() }.add(b)
            adj.getOrPut(b) { mutableListOf() }.add(a)
        }
        links.forEach { noi(it.idCon, it.idPhuHuynh) }
        ms.forEach { noi(it.idChong, it.idVo) }
        return adj
    }

    private fun components(adj: Map<Int, List<Int>>, nodes: Collection<Int>): List<Set<Int>> {
        val seen = HashSet<Int>()
        val out = mutableListOf<Set<Int>>()
        for (n in nodes.sorted()) {
            if (n in seen) continue
            val comp = java.util.TreeSet<Int>()
            val stack = ArrayDeque<Int>()
            stack.add(n); seen.add(n)
            while (stack.isNotEmpty()) {
                val u = stack.removeLast()
                comp.add(u)
                for (v in adj[u].orEmpty()) if (seen.add(v)) stack.add(v)
            }
            out.add(comp)
        }
        return out
    }

    /** Số đời tính theo MỌI dòng nối, kể cả dòng đang ẩn. */
    private fun tinhDoi() {
        ids.forEach { doi[it] = 0 }
        val adjAll = adjacency(linksAll, marriages)
        coLienKet.addAll(adjAll.keys)

        var lap = 0
        var doiDuoc: Boolean
        do {
            doiDuoc = false
            for (l in linksAll) {
                val can = doi[l.idPhuHuynh]!! + 1
                if (doi[l.idCon]!! < can) { doi[l.idCon] = can; doiDuoc = true }
            }
            for (m in marriages) {
                val mx = maxOf(doi[m.idChong]!!, doi[m.idVo]!!)
                if (doi[m.idChong]!! != mx) { doi[m.idChong] = mx; doiDuoc = true }
                if (doi[m.idVo]!! != mx) { doi[m.idVo] = mx; doiDuoc = true }
            }
            lap++
        } while (doiDuoc && lap < ids.size + 5)
        if (doiDuoc) canhBao += "Quan hệ có vòng lặp, số đời có thể sai."

        for (comp in components(adjAll, coLienKet)) {
            val mn = comp.minOf { doi[it]!! }
            comp.forEach { doi[it] = doi[it]!! - mn + 1 }
        }
    }

    // ---------------------------------------------------------------- cận huyết

    private val ruot: Map<Int, List<Int>> =
        linksAll.filter { it.loai == LoaiQuanHe.RUOT }.groupBy({ it.idCon }, { it.idPhuHuynh })

    /** Chính người đó và tổ tiên RUỘT trong tối đa 3 đời. Tính cả dòng ẩn. */
    private fun tienBoi(id: Int): Set<Int> {
        val kq = mutableSetOf(id)
        var tang = listOf(id)
        repeat(3) {
            val moi = tang.flatMap { ruot[it].orEmpty() }
            kq.addAll(moi)
            tang = moi
        }
        return kq
    }

    private fun canHuyet(m: QHeHonNhan?) =
        m != null && tienBoi(m.idChong).intersect(tienBoi(m.idVo)).isNotEmpty()

    // ---------------------------------------------------------------- khối và nơi gắn

    private fun taoKhoi() {
        val comps = components(adjacency(emptyList(), marriages), ids)
        for (comp in comps) {
            val (ms, hns) = sapXepKhoi(comp, marriages.filter { it.idChong in comp })
            val k = Khoi(ms, hns)
            ms.forEach { khoiCua[it] = k }
        }
    }

    private fun sapXepKhoi(comp: Set<Int>, ms: List<QHeHonNhan>): Pair<List<Int>, List<QHeHonNhan?>> {
        if (comp.size == 1) return listOf(comp.first()) to listOf(null)
        if (comp.size == 2) {
            val m = ms.first()
            return listOf(m.idChong, m.idVo) to listOf(null, m)           // chồng bên trái, vợ bên phải
        }
        val bac = comp.associateWith { id -> ms.count { it.idChong == id || it.idVo == id } }
        val chung = comp.sortedWith(compareByDescending<Int> { bac[it]!! }.thenBy { it }).first()
        val cuaChung = ms.filter { it.idChong == chung || it.idVo == chung }
            .sortedWith(compareBy({ it.thuTuHonNhan ?: Int.MAX_VALUE }, { if (it.idChong == chung) it.idVo else it.idChong }))
        val members = mutableListOf(chung)
        val hns = mutableListOf<QHeHonNhan?>(null)
        for (m in cuaChung) {
            members.add(if (m.idChong == chung) m.idVo else m.idChong)
            hns.add(m)
        }
        val conLai = comp.filter { it !in members }.sorted()
        if (conLai.isNotEmpty() || bac.values.count { it >= 2 } > 1)
            canhBao += "Có chuỗi hôn nhân phức tạp quanh Ô #$chung, một số đường hôn nhân không được vẽ."
        conLai.forEach { members.add(it); hns.add(null) }
        return members to hns
    }

    private fun cacKhoi(): List<Khoi> = ids.sorted().map { khoiCua[it]!! }.distinct()

    /** Các nơi một người gắn vào cha mẹ (chỉ dòng đang hiện). Hai phụ huynh đã cưới nhau gộp thành một. */
    private fun cacGan(id: Int): List<Gan> {
        val conLai = linksVis.filter { it.idCon == id }.toMutableList()
        val kq = mutableListOf<Gan>()
        for (m in marriages) {
            val a = conLai.firstOrNull { it.idPhuHuynh == m.idChong }
            val b = conLai.firstOrNull { it.idPhuHuynh == m.idVo }
            if (a == null || b == null) continue
            val k = khoiCua[m.idChong]!!
            val c = k.cho.firstOrNull { it.honNhanCho && it.hn === m } ?: continue
            kq.add(Gan(c, k, listOf(a, b)))
            conLai.remove(a); conLai.remove(b)
        }
        for (l in conLai) {
            val k = khoiCua[l.idPhuHuynh]!!
            val c = k.cho.first { !it.honNhanCho && it.chiSo == k.ms.indexOf(l.idPhuHuynh) }
            kq.add(Gan(c, k, listOf(l)))
        }
        return kq
    }

    /** Mỗi khối chọn MỘT nơi gắn chính (để thành cây); các nơi còn lại vẽ thành đường nối phụ. */
    private fun ganChaMe() {
        for (k in cacKhoi()) {
            val ungVien = mutableListOf<Triple<Int, Int, Gan>>()
            k.ms.forEachIndexed { i, id ->
                cacGan(id).forEachIndexed { j, g ->
                    if (g.khoi !== k && doi[g.khoi.ms[0]]!! < doi[id]!!) ungVien.add(Triple(i, j, g))
                }
            }
            ungVien.sortWith(compareBy({ if (it.third.cho.honNhanCho) 0 else 1 }, { it.first }, { it.second }))
            val dau = ungVien.firstOrNull() ?: continue
            k.chiSoChinh = dau.first
            k.chinh = dau.third
            dau.third.cho.con.add(k)
            ungVien.drop(1).forEach { k.phu.add(it.first to it.third) }
        }
    }

    // ---------------------------------------------------------------- xếp ngang (Reingold–Tilford)

    private val khoaSinh = compareBy<Khoi>(
        { byId[it.ms[it.chiSoChinh]]!!.ngaySinh ?: LocalDate.MAX },
        { it.ms[it.chiSoChinh] }
    )

    private fun xepKhoi(k: Khoi): Manh {
        val n = k.ms.size
        val d = doi[k.ms[0]]!!
        val con = Manh()
        val neoCho = HashMap<Cho, Pair<Float, Float>>()

        for (c in k.cho) {
            if (c.con.isEmpty()) continue
            val nhom = Manh()
            val neos = mutableListOf<Float>()
            for (kc in c.con.sortedWith(khoaSinh)) {
                val m = xepKhoi(kc)
                m.dich(if (nhom.trong()) 0f else canDich(nhom, m, ch.khoangCachThe))
                neos.add(m.neo)
                nhom.gop(m)
            }
            val off = if (con.trong()) 0f else canDich(con, nhom, ch.khoangCachNhom)
            nhom.dich(off)
            con.gop(nhom)
            neoCho[c] = (neos.first() + off) to (neos.last() + off)
        }

        val dau = neoCho.values.minOfOrNull { it.first }
        val cuoi = neoCho.values.maxOfOrNull { it.second }
        val giua = if (dau != null && cuoi != null) (dau + cuoi) / 2 else 0f
        fun tamCho(c: Cho) = neoCho[c]?.let { (it.first + it.second) / 2 }

        val px = FloatArray(n)
        when {
            n == 1 -> px[0] = giua
            n == 2 -> {
                val mid = tamCho(k.cho.first { it.honNhanCho }) ?: giua
                val nuaCach = (w + ch.duongVoChong) / 2
                px[0] = mid - nuaCach; px[1] = mid + nuaCach
            }
            else -> {
                val buoc = w + ch.khoangCachThe
                val xs = FloatArray(n) { Float.NaN }
                for (c in k.cho) if (c.honNhanCho) xs[c.chiSo] = tamCho(c) ?: Float.NaN
                val f = (1 until n).firstOrNull { !xs[it].isNaN() }
                if (f == null) xs[1] = giua
                val goc = f ?: 1
                for (j in goc - 1 downTo 1) xs[j] = xs[j + 1] - buoc
                for (j in goc + 1 until n) if (xs[j].isNaN()) xs[j] = xs[j - 1] + buoc
                for (j in 2 until n) xs[j] = maxOf(xs[j], xs[j - 1] + buoc)
                px[0] = xs[1] - buoc
                for (j in 1 until n) px[j] = xs[j]
            }
        }
        for (i in 0 until n) con.them(k.ms[i], px[i], d, nua)
        con.neo = px[k.chiSoChinh]
        return con
    }

    // ---------------------------------------------------------------- một cụm: xếp, tính hàng, sinh đường

    private fun tieuDeCum(c: Set<Int>): String {
        val tach = c.filter { id -> linksAll.any { !it.hienThiLienKet && it.idCon == id } }
        val dau = (tach.ifEmpty { c.toList() }).minWithOrNull(compareBy({ doi[it]!! }, { it }))!!
        return (if (tach.isNotEmpty()) "Nhánh tách: " else "Cụm rời: ") + byId[dau]!!.hoTen
    }

    /** Trả về y cuối cụm. */
    private fun xepCum(cum: Set<Int>, yGoc: Float, tieuDe: String?): Float {
        val khois = cum.map { khoiCua[it]!! }.distinct()
        val goc = khois.filter { it.chinh == null }.sortedBy { it.ms.min() }

        val tong = Manh()
        for (g in goc) {
            val m = xepKhoi(g)
            m.dich(if (tong.trong()) 0f else canDich(tong, m, ch.khoangCachNhom))
            tong.gop(m)
        }
        tong.dich(ch.leTrai - tong.traiNhat())
        val x = tong.xs
        phaiNhatCanvas = maxOf(phaiNhatCanvas, tong.phaiNhat())

        // ---- các hàng
        val gMin = cum.minOf { doi[it]!! }
        val gMax = cum.maxOf { doi[it]!! }
        fun khoangSau(g: Int) =
            if (khois.any { it.nhieuHonNhan && doi[it.ms[0]] == g && it.cho.any { c -> c.con.isNotEmpty() } })
                ch.khoangCachHangDaHonNhan else ch.khoangCachHang

        var yBang = yGoc
        if (tieuDe != null) {
            oTieuDe.add(TieuDeCum(tieuDe, ch.dem, yGoc + 8f))
            yBang = yGoc + 48f
        }
        val dinhHang = HashMap<Int, Float>()
        var yy = yBang + ch.dem
        for (g in gMin..gMax) {
            dinhHang[g] = yy
            oDai.add(DaiDoi(g, yy - ch.dem, h + if (g < gMax) khoangSau(g) else ch.dem * 2))
            yy += h + khoangSau(g)
        }
        val yCuoi = dinhHang[gMax]!! + h + ch.dem

        for (id in cum) oThe.add(TheTrenCay(id, x[id]!! - nua, dinhHang[doi[id]!!]!!, doi[id]))

        // ---- đường nối
        fun lien(x1: Float, y1: Float, x2: Float, y2: Float, dut: Boolean = false, do_: Boolean = false) {
            if (x1 == x2 && y1 == y2) return
            (if (dut) oNetDut else oNetLien).add(DoanNoi(x1, y1, x2, y2, if (dut) KieuNet.DUT else KieuNet.LIEN, do_))
        }
        fun dinh(id: Int) = dinhHang[doi[id]!!]!!

        fun xuatPhat(k: Khoi, c: Cho): Pair<Float, Float> {
            val t = dinh(k.ms[0])
            return when {
                !c.honNhanCho -> x[k.ms[c.chiSo]]!! to (t + h)
                k.ms.size == 2 -> ((x[k.ms[0]]!! + x[k.ms[1]]!!) / 2) to (t + h / 2)
                else -> x[k.ms[c.chiSo]]!! to (t + h + ch.honNhanDuoiThe)
            }
        }

        fun thanhNgang(k: Khoi, c: Cho, phu: Boolean = false): Float? {
            val tiep = dinhHang[doi[k.ms[0]]!! + 1] ?: return null
            return tiep - when {
                phu -> ch.thanhNgangNoiPhu
                c.honNhanCho -> ch.thanhNgangTruocCon
                else -> ch.thanhNgangMotCha
            }
        }

        fun dutCua(g: Gan) = g.links.all { it.loai == LoaiQuanHe.NUOI }

        for (k in khois) {
            val t = dinh(k.ms[0])
            val day = t + h

            // hôn nhân
            if (k.ms.size == 2) {
                val m = k.hns[1]!!
                val x0 = x[k.ms[0]]!! + nua
                val x1 = x[k.ms[1]]!! - nua
                lien(x0, t + h / 2, x1, t + h / 2, do_ = canHuyet(m))
                if (m.trangThai == TrangThaiHonNhan.DA_LY_HON) oDauHuy.add(Diem((x0 + x1) / 2, t + h / 2))
            } else if (k.ms.size >= 3) {
                val yM = day + ch.honNhanDuoiThe
                lien(x[k.ms[0]]!!, day, x[k.ms[0]]!!, yM)
                for (j in 1 until k.ms.size) {
                    val m = k.hns[j] ?: continue
                    val dc = canHuyet(m)
                    lien(x[k.ms[j - 1]]!!, yM, x[k.ms[j]]!!, yM, do_ = dc)
                    lien(x[k.ms[j]]!!, day, x[k.ms[j]]!!, yM, do_ = dc)
                    oCham.add(Diem(x[k.ms[j]]!!, yM))
                    if (m.trangThai == TrangThaiHonNhan.DA_LY_HON) oDauHuy.add(Diem(x[k.ms[j]]!!, day + ch.honNhanDuoiThe / 2))
                }
            }

            // cha mẹ -> con (nơi gắn chính)
            for (c in k.cho) {
                if (c.con.isEmpty()) continue
                val bar = thanhNgang(k, c) ?: continue
                val (sx, sy) = xuatPhat(k, c)
                val mucCon = c.con.map { kc ->
                    Triple(x[kc.ms[kc.chiSoChinh]]!!, dinh(kc.ms[kc.chiSoChinh]), dutCua(kc.chinh!!))
                }
                lien(sx, sy, sx, bar, dut = mucCon.all { it.third })
                for ((cx, ct, dut) in mucCon) {
                    lien(sx, bar, cx, bar, dut = dut)
                    lien(cx, bar, cx, ct, dut = dut)
                }
            }

            // nơi gắn phụ (người có cha mẹ ở hai bên, ví dụ hôn nhân giữa hai nhánh)
            for ((i, g) in k.phu) {
                val bar = thanhNgang(g.khoi, g.cho, phu = true) ?: continue
                val (sx, sy) = xuatPhat(g.khoi, g.cho)
                val cx = x[k.ms[i]]!!
                val dut = dutCua(g)
                lien(sx, sy, sx, bar, dut = dut)
                lien(sx, bar, cx, bar, dut = dut)
                lien(cx, bar, cx, dinh(k.ms[i]), dut = dut)
            }

            // nền nhóm con của từng cuộc hôn nhân (chỉ khi một người có nhiều hôn nhân)
            if (k.nhieuHonNhan) {
                var tone = 0
                for (c in k.cho) {
                    if (!c.honNhanCho || c.con.isEmpty()) continue
                    val the = c.con.flatMap { it.ms }
                    val trai = the.minOf { x[it]!! - nua } - 10f
                    val phai = the.maxOf { x[it]!! + nua } + 10f
                    oNen.add(NenNhom(trai, dinh(the[0]) - 12f, phai - trai, h + 24f, tone++))
                }
            }
        }
        return yCuoi
    }

    // ---------------------------------------------------------------- khu "Chưa rõ vị trí"

    private fun xepChuaRoViTri(yGoc: Float): Float {
        val ds = ids.filter { it !in coLienKet }.sorted()
        if (ds.isEmpty()) return yGoc
        val buoc = w + ch.khoangCachThe
        val rongCanvas = maxOf(phaiNhatCanvas, ch.leTrai + 4 * buoc - ch.khoangCachThe)
        val cot = maxOf(1, ((rongCanvas - ch.leTrai + ch.khoangCachThe) / buoc).toInt())
        oTieuDe.add(TieuDeCum("Chưa rõ vị trí", ch.dem, yGoc + 8f))
        var y = yGoc + 48f
        ds.forEachIndexed { i, id ->
            val c = i % cot
            val r = i / cot
            oThe.add(TheTrenCay(id, ch.leTrai + c * buoc, y + r * (h + ch.khoangCachThe), null))
        }
        val hang = (ds.size + cot - 1) / cot
        phaiNhatCanvas = maxOf(phaiNhatCanvas, ch.leTrai + minOf(ds.size, cot) * buoc - ch.khoangCachThe)
        y += hang * (h + ch.khoangCachThe)
        return y
    }
}