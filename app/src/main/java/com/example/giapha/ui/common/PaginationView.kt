package com.example.giapha.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.giapha.R
import com.google.android.material.button.MaterialButton

/**
 * Thanh phân trang "Trang trước | Trang n / N | Trang sau" (AdminApp, 20 dòng mỗi trang).
 * Dùng:
 *   pagination.onPageChange = { page -> tải trang đó }
 *   pagination.setPage(page = 1, totalPages = PaginationView.totalPages(soDong))
 * Trang đánh số từ 1.
 */
class PaginationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        const val PAGE_SIZE = 20
        fun totalPages(itemCount: Int) = maxOf(1, (itemCount + PAGE_SIZE - 1) / PAGE_SIZE)
    }

    private val btnPrev: MaterialButton
    private val btnNext: MaterialButton
    private val tvPage: TextView
    private var page = 1
    private var total = 1

    /** Gọi khi người dùng bấm đổi trang, nhận số trang mới. */
    var onPageChange: ((Int) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pagination, this, true)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        tvPage = findViewById(R.id.tvPage)
        setBackgroundColor(ContextCompat.getColor(context, R.color.surface))

        // Chừa chỗ cho thanh điều hướng của hệ thống ở đáy
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.setPadding(0, 0, 0, bottom)
            insets
        }

        btnPrev.setOnClickListener { if (page > 1) onPageChange?.invoke(page - 1) }
        btnNext.setOnClickListener { if (page < total) onPageChange?.invoke(page + 1) }
        render()
    }

    fun setPage(page: Int, totalPages: Int) {
        total = maxOf(1, totalPages)
        this.page = page.coerceIn(1, total)
        render()
    }

    private fun render() {
        tvPage.text = "Trang $page / $total"
        btnPrev.isEnabled = page > 1
        btnNext.isEnabled = page < total
        btnPrev.alpha = if (btnPrev.isEnabled) 1f else 0.4f
        btnNext.alpha = if (btnNext.isEnabled) 1f else 0.4f
    }
}