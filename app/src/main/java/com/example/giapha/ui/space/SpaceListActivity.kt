package com.example.giapha.ui.space

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giapha.R
import com.example.giapha.data.Kq
import com.example.giapha.data.KhongGianCuaToi
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivitySpaceListBinding
import com.example.giapha.databinding.DialogCreateSpaceBinding
import com.example.giapha.databinding.ItemSpaceBinding
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applySystemBarsPadding
import com.example.giapha.ui.common.initialOf
import com.example.giapha.ui.common.setRoleBadge
import com.example.giapha.ui.common.setup

/** GD-03 — Danh sách Không gian của tôi. */
class SpaceListActivity : AppCompatActivity() {

    private lateinit var b: ActivitySpaceListBinding
    private val repo get() = Services.repository
    private val adapter = SpaceAdapter { Routes.moKhongGian(this, it.khongGian.idKhongGian) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySpaceListBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Thanh tiêu đề: dùng nút bên trái làm nút mở sidebar
        b.appBar.setup(this, "Không gian của tôi")
        b.appBar.btnBack.setImageResource(R.drawable.ic_menu)
        b.appBar.btnBack.contentDescription = "Mở menu"
        b.appBar.btnBack.setOnClickListener { b.drawer.openDrawer(GravityCompat.START) }

        b.drawerPanel.applySystemBarsPadding()
        keepFabAboveNavBar()

        b.rvSpaces.layoutManager = LinearLayoutManager(this)
        b.rvSpaces.adapter = adapter
        b.etSearch.doAfterTextChanged { load() }

        b.fab.setOnClickListener { showCreateDialog() }
        setupDrawer()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (b.drawer.isDrawerOpen(GravityCompat.START)) {
                    b.drawer.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    /** Màn trước/sau quay về đều tải lại toàn bộ. */
    override fun onResume() {
        super.onResume()
        bindAccount()
        load()
    }

    private fun load() {
        val query = b.etSearch.text.toString()
        when (val r = repo.danhSachKhongGian(query)) {
            is Kq.Loi -> {
                adapter.submit(emptyList())
                b.stateView.showError(r.thongBao) { load() }
            }
            is Kq.Ok -> {
                adapter.submit(r.giaTri)
                when {
                    r.giaTri.isNotEmpty() -> b.stateView.hide()
                    query.isBlank() -> b.stateView.showEmpty(
                        "Bạn chưa tham gia Không gian nào — tạo mới hoặc nhập mã mời để bắt đầu"
                    )
                    else -> b.stateView.showEmpty("Không tìm thấy kết quả")
                }
            }
        }
    }

    private fun bindAccount() {
        val tk = repo.taiKhoanHienTai() ?: return Routes.dangXuat(this)
        b.tvAvatar.text = initialOf(tk.tenHienThi)
        b.tvAccountName.text = tk.tenHienThi
        b.tvAccountPhone.text = tk.sdt
    }

    private fun setupDrawer() {
        fun closeThen(action: () -> Unit) = View.OnClickListener {
            b.drawer.closeDrawer(GravityCompat.START)
            action()
        }
        b.headerProfile.setOnClickListener(closeThen { Routes.moHoSo(this) })
        b.itemSpaces.setOnClickListener(closeThen { })
        b.itemFilter.setOnClickListener(closeThen {
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        })
        b.itemTrash.setOnClickListener(closeThen {
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        })
        b.itemJoinCode.setOnClickListener(closeThen { Routes.moNhapMa(this) })
        b.itemLogout.setOnClickListener(closeThen { Routes.dangXuat(this) })
    }

    /** Nút nổi không bị thanh điều hướng che. */
    private fun keepFabAboveNavBar() {
        val base = (16 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(b.fab) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val lp = v.layoutParams as ViewGroup.MarginLayoutParams
            lp.bottomMargin = base + bottom
            v.layoutParams = lp
            insets
        }
    }

    private fun showCreateDialog() {
        val d = DialogCreateSpaceBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(d.root).create()

        d.etName.doAfterTextChanged {
            d.etName.isActivated = false
            d.tvError.visibility = View.GONE
        }
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnCreate.setOnClickListener {
            when (val r = repo.taoKhongGian(d.etName.text.toString())) {
                is Kq.Loi -> {
                    d.etName.isActivated = true
                    d.tvError.text = r.thongBao
                    d.tvError.visibility = View.VISIBLE
                }
                is Kq.Ok -> {
                    dialog.dismiss()
                    load()
                    Routes.moKhongGian(this, r.giaTri.idKhongGian)   // tạo xong vào thẳng cây
                }
            }
        }

        dialog.show()
        val margin = (24 * resources.displayMetrics.density).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), margin, 0, margin, 0))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        d.etName.requestFocus()
    }
}

private class SpaceAdapter(
    private val onClick: (KhongGianCuaToi) -> Unit
) : RecyclerView.Adapter<SpaceAdapter.Holder>() {

    private var items: List<KhongGianCuaToi> = emptyList()

    fun submit(list: List<KhongGianCuaToi>) {
        items = list
        notifyDataSetChanged()
    }

    class Holder(val b: ItemSpaceBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemSpaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: Holder, position: Int) {
        val item = items[position]
        h.b.tvName.text = item.khongGian.tenKhongGian
        h.b.ivLock.visibility = if (item.khongGian.cheDoRiengTu) View.VISIBLE else View.GONE
        h.b.badge.setRoleBadge(item.vaiTro.db)
        h.b.root.setOnClickListener { onClick(item) }
    }
}