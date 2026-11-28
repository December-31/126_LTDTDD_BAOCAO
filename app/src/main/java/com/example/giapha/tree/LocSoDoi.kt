package com.example.giapha.tree

import com.example.giapha.model.QHeHonNhan
import com.example.giapha.model.QuanHeChaMe

/** Bộ lọc số đời: chỉ xem N đời quanh Ô mốc (QUYET_DINH_BO_SUNG mục 3). n = tổng số đời, tính cả bản thân. */
data class BoLocDoi(val idMoc: Int, val n: Int)

/**
 * Đầu vào của bộ lọc, lấy từ engine:
 * doi = số đời tuyệt đối (tính trên MỌI dòng nối, kể cả dòng ẩn);
 * links, honNhan = chỉ các đường đang hiển thị.
 */
class DuLieuLoc(
    val doi: Map<Int, Int>,
    val links: List<QuanHeChaMe>,
    val honNhan: List<QHeHonNhan>
)

object LocSoDoi {

    /**
     * Tập Ô được hiện. null nếu Ô mốc không có trong dữ liệu (khi đó vẽ đủ cây).
     *
     * Dải đời = [đời mốc − ⌊(N−1)/2⌋ , đời mốc + ⌈(N−1)/2⌉]. Một Ô chỉ hiện khi có đường nối đang hiển thị dẫn vào nó:
     * 1. Trục tổ tiên: mốc và những người đi lên được (cha/mẹ, ông bà...) còn nằm trong dải.
     * 2. Đi xuống từ mọi người trên trục (anh/chị/em, con, cháu...) trong dải. Anh/chị/em của một người chỉ hiện
     *    khi cha/mẹ họ hiện, vì họ được đi xuống từ cha/mẹ. Đi lên chỉ theo trục tổ tiên nên không kéo gia đình người ở rìa.
     * 3. Vợ/chồng của người đang hiện luôn hiện, nhưng không đi tiếp (không kéo cha mẹ hay con riêng của họ).
     */
    fun tinh(idMoc: Int, n: Int, d: DuLieuLoc): Set<Int>? {
        val doiMoc = d.doi[idMoc] ?: return null
        val soTren = (maxOf(n, 1) - 1) / 2
        val soDuoi = maxOf(n, 1) - 1 - soTren
        val tren = doiMoc - soTren
        val duoi = doiMoc + soDuoi

        val chaCua = d.links.groupBy({ it.idCon }, { it.idPhuHuynh })
        val conCua = d.links.groupBy({ it.idPhuHuynh }, { it.idCon })
        val phoiCua = HashMap<Int, MutableList<Int>>()
        for (m in d.honNhan) {
            phoiCua.getOrPut(m.idChong) { mutableListOf() }.add(m.idVo)
            phoiCua.getOrPut(m.idVo) { mutableListOf() }.add(m.idChong)
        }

        val ruot = HashSet<Int>()
        val ngan = ArrayDeque<Int>()

        // 1) trục tổ tiên
        ruot.add(idMoc); ngan.add(idMoc)
        while (ngan.isNotEmpty()) {
            val x = ngan.removeLast()
            for (p in chaCua[x].orEmpty()) {
                if ((d.doi[p] ?: Int.MIN_VALUE) >= tren && ruot.add(p)) ngan.add(p)
            }
        }
        // 2) đi xuống từ mọi người trên trục
        ngan.addAll(ruot)
        while (ngan.isNotEmpty()) {
            val x = ngan.removeLast()
            for (c in conCua[x].orEmpty()) {
                if ((d.doi[c] ?: Int.MAX_VALUE) <= duoi && ruot.add(c)) ngan.add(c)
            }
        }
        // 3) vợ/chồng
        val hien = HashSet(ruot)
        for (x in ruot) hien.addAll(phoiCua[x].orEmpty())
        return hien
    }

    /** N nhỏ nhất mà tăng thêm cũng không hiện thêm ai. null nếu Ô mốc không có trong dữ liệu. */
    fun soDoiToiDa(idMoc: Int, d: DuLieuLoc): Int? {
        val gioiHan = (d.doi.values.maxOrNull() ?: 1) * 2 + 2
        val dayDu = tinh(idMoc, gioiHan, d) ?: return null
        for (n in 1..gioiHan) if (tinh(idMoc, n, d) == dayDu) return n
        return gioiHan
    }
}