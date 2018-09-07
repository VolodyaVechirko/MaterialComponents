package com.vvechirko.testapp

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.android.synthetic.main.activity_images.*

const val STATE_NORMAL = 1
const val STATE_SELECT = 2
const val STATE_SHEET = 3

class ImagesActivity : AppCompatActivity() {

    private var state: Int = STATE_NORMAL
    private var adapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            when (state) {
                STATE_NORMAL -> onBackPressed()
                STATE_SELECT -> adapter?.clearSelection()
//                STATE_SHEET -> closeBottomSheet
            }
        }

        adapter = Adapter().apply {
            setOnItemSelectListener {
                val state = if (it.isEmpty()) STATE_NORMAL else STATE_SELECT
                notifyAppBar(state)

                if (state == STATE_NORMAL) {
                    toolbar.title = getString(R.string.app_name)
                } else {
                    toolbar.title = "Selected: ${it.size}"
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun notifyAppBar(state: Int) {
        if (this.state == state) {
            return
        }

        this.state = state
        when (state) {
            STATE_NORMAL -> {
                toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)
                appBar.setBackgroundResource(R.color.colorPrimary)
                window.statusBarColorRes(R.color.colorPrimaryDark)
            }
            STATE_SELECT -> {
//                toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp)
                toolbar.setNavigationAnim(R.drawable.animate_home)
//                (toolbar.navigationIcon as Animatable).start()
                appBar.setBackgroundResource(R.color.colorDark)
                window.statusBarColorRes(R.color.colorDarkPrimaryDark)
            }
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (state) {
            STATE_NORMAL -> menuInflater.inflate(R.menu.menu_images, menu)
            STATE_SELECT -> menuInflater.inflate(R.menu.menu_selection, menu)
            STATE_SHEET -> menu.clear()
        }
        return super.onCreateOptionsMenu(menu)
    }
}

class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

    private val data = mutableListOf<String>().apply {
        for (i in 1..40) {
            add("list item $i")
        }
    }

    var selectListener: ((selected: List<String>) -> Unit)? = null

    private val selected = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = data.get(position)
        holder.setSelected(selected.contains(item))

        holder.itemView.setOnClickListener {
            if (selected.contains(item)) {
                selected.remove(item)
            } else {
                selected.add(item)
            }
            notifyItemChanged(data.indexOf(item))

            selectListener?.invoke(selected)
        }
    }

    fun setOnItemSelectListener(listener: ((selected: List<String>) -> Unit)?) {
        selectListener = listener
    }

    fun clearSelection() {
        selected.clear()
        notifyDataSetChanged()
        selectListener?.invoke(selected)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView: AppCompatImageView = itemView.findViewById(R.id.imageView)
        val foregraundGroup: Group = itemView.findViewById(R.id.foregroundGroup)

        fun setSelected(selected: Boolean) {
            foregraundGroup.visibility = if (selected) View.VISIBLE else View.GONE
        }
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.statusBarColorRes(@ColorRes resId: Int) {
    statusBarColor = ContextCompat.getColor(context, resId)
}

fun Toolbar.setNavigationAnim(@DrawableRes resId: Int) {
    navigationIcon = AnimatedVectorDrawableCompat.create(context, resId).also { it?.start() }
}