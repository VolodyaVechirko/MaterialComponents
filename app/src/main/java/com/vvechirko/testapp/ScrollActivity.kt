package com.vvechirko.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TwoLineListItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scroll.*

class ScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)
        setSupportActionBar(toolbar)

        recyclerView.adapter = Adapter()
        recyclerView.isNestedScrollingEnabled = false

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val data = mutableListOf<String>().apply {
            for (i in 1..40) {
                add("list item $i")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)) {

            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val s = data.get(position)
            with(holder.itemView as TwoLineListItem) {
                text1.setText(s.toUpperCase())
                text2.setText(s)
            }
        }
    }
}