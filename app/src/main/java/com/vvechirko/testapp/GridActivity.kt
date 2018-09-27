package com.vvechirko.testapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var grid: GridLayoutManager
    lateinit var list: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content: FrameLayout = findViewById(android.R.id.content)

        val adapter = Adapter()
        list = LinearLayoutManager(this)
        grid = GridLayoutManager(this, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == adapter.div) 2 else 1
                }
            }
        }

        rv = RecyclerView(this)
        rv.layoutManager = grid
        rv.adapter = adapter

        content.addView(rv, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    fun toggle() {
        val pos = (rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (rv.layoutManager === grid) {
            rv.layoutManager = list
        } else {
            rv.layoutManager = grid
        }
        (rv.layoutManager as LinearLayoutManager).scrollToPosition(pos)
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

    val div = -1

    var sections: MutableList<List<String>> = mutableListOf(
            mutableListOf<String>().apply {
                for (i in 1..13) add("item $i")
            },
            mutableListOf<String>().apply {
                for (i in 1..15) add("item $i")
            },
            mutableListOf<String>().apply {
                for (i in 1..11) add("item $i")
            }
    )

    override fun getItemViewType(position: Int): Int {
        var start: Int = 0
        sections.forEachIndexed { index, list ->
            if (position == start) return div
            start += list.size + 1
            if (position < start) return index
        }
        return div
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            div -> divider(parent.context)
            0 -> view1(parent.context)
            1 -> view2(parent.context)
            else -> view3(parent.context)
        }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int = sections.size + sections.sumBy { it.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // viewType is index of section
        val viewType = getItemViewType(position)
        val correctPos = correctPosition(position)

        if (viewType == div) {
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