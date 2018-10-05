package com.vvechirko.testapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.vvechirko.testapp.images.ImagesActivity
import com.vvechirko.testapp.pip.PIPActivity
import com.vvechirko.testapp.transition.MusicActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    val items: Map<String, KClass<*>> = mapOf(
            "Transitions" to MusicActivity::class,
            "Lift On Scroll" to ScrollActivity::class,
            "Images" to ImagesActivity::class,
            "ConstraintSet" to ConstraintSetActivity::class,
            "Menus" to ContextMenuActivity::class,
            "Login" to LoginActivity::class,
            "Grid" to GridActivity::class,
            "Pip" to PIPActivity::class,
            "BottomSheet" to BottomSheetActivity::class
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.adapter = Adapter()
    }

    fun start(activity: KClass<*>) {
        startActivity(Intent(this, activity.java))
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                Holder(LayoutInflater.from(parent.context)
                        .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
                )

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            items.entries.elementAt(position).let { map ->
                holder.textView.setText(map.key)
                holder.itemView.setOnClickListener { start(map.value) }
            }
        }

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val textView = itemView.findViewById<TextView>(android.R.id.text1)
        }
    }
}
