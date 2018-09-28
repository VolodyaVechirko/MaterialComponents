package com.vvechirko.testapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager

class GridActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var adapter: Adapter

    val SPANS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content: FrameLayout = findViewById(android.R.id.content)

        adapter = Adapter()
        val grid = GridLayoutManager(this, 1)

//        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        rv = RecyclerView(this)
//        rv.layoutAnimation = animation
        rv.layoutManager = grid
        rv.adapter = adapter
        spanCount(SPANS)

        content.addView(rv, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun onStart() {
        super.onStart()
        adapter.setSections(
                mutableListOf<String>().apply {
                    for (i in 1..5) add("item $i")
                },
                mutableListOf<String>().apply {
                    for (i in 1..8) add("item $i")
                },
                mutableListOf<String>().apply {
                    for (i in 1..7) add("item $i")
                }
        )
    }

    fun spanCount(span: Int) {
        with(rv.layoutManager as GridLayoutManager) {
//            TransitionManager.beginDelayedTransition(rv)
            spanCount = span
            spanSizeLookup = adapter.spanSizeLookup(span)
        }
    }

    fun toggle() {
        with(rv.layoutManager as GridLayoutManager) {
            val pos = findFirstCompletelyVisibleItemPosition()
            spanCount(if (spanCount == 1) SPANS else 1)
            scrollToPosition(pos)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Switch").apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            setOnMenuItemClickListener {
                toggle()
                true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }
}

class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIVIDER = -1

    private var sections: MutableList<List<String>> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        var start: Int = 0
        sections.forEachIndexed { index, list ->
            if (position == start) return DIVIDER
            start += list.size + 1
            if (position < start) return index
        }
        return DIVIDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // viewType is index of section or divider -1
        val view = when (viewType) {
            0 -> view1(parent.context)
            1 -> view2(parent.context)
            2 -> view3(parent.context)
//            DIVIDER -> divider(parent.context)
            else -> divider(parent.context)
        }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int = sections.size + sections.sumBy { it.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // viewType is index of section or divider -1
        val viewType = getItemViewType(position)
        val correctPos = correctPosition(position)

        if (viewType == DIVIDER) {
            (holder.itemView as TextView).setText("Section ${correctPos + 1}")
        } else {
            sections.get(viewType).get(correctPos).let {
                (holder.itemView as TextView).setText(it)
            }
        }
    }

    private fun correctPosition(position: Int): Int {
        var start: Int = 0
        sections.forEachIndexed { index, list ->
            if (position == start) return index
            start += list.size + 1
            if (position < start) return position + list.size - start
        }
        return position
    }

    fun spanSizeLookup(span: Int) =
            if (span == 1) GridLayoutManager.DefaultSpanSizeLookup()
            else object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position) == DIVIDER) span else 1
                }
            }

    fun setSections(vararg items: List<String>) {
        sections.clear()
        sections.addAll(items)
        notifyDataSetChanged()
    }

//  =========================================

    fun view1(context: Context) = TextView(context).apply {

        minimumHeight = 200
        setBackgroundColor(Color.BLUE)
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(20, 20, 20, 20)
        }
    }

    fun view2(context: Context) = TextView(context).apply {
        minimumHeight = 400
        setBackgroundColor(Color.GREEN)
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(20, 20, 20, 20)
        }
    }

    fun view3(context: Context) = TextView(context).apply {
        minimumHeight = 300
        setBackgroundColor(Color.RED)
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(20, 20, 20, 20)
        }
    }

    fun divider(context: Context) = TextView(context).apply {
        minimumHeight = 80
        setBackgroundColor(Color.GRAY)
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
}