package com.vvechirko.testapp.images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vvechirko.testapp.R
import com.vvechirko.testapp.data.RecipeModel

class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

    private val data = mutableListOf<RecipeModel>()

    private var selectListener: ((selected: List<RecipeModel>) -> Unit)? = null
    private var clickListener: ((item: RecipeModel) -> Unit)? = null

    private val selected = mutableListOf<RecipeModel>()

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
        holder.setItem(item)

        holder.imageView.apply {
            setOnLongClickListener {
                if (selected.isEmpty()) {
                    toggle(item)
                    true
                } else {
                    false
                }
            }

            setOnClickListener {
                if (selected.isEmpty()) {
                    clickListener?.invoke(item)
                } else {
                    toggle(item)
                }
            }
        }
    }

    fun toggle(item: RecipeModel) {
        if (selected.contains(item)) {
            selected.remove(item)
        } else {
            selected.add(item)
        }
        notifyItemChanged(data.indexOf(item))

        selectListener?.invoke(selected)
    }

    fun setData(list: List<RecipeModel>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemSelectListener(listener: ((selected: List<RecipeModel>) -> Unit)?) {
        selectListener = listener
    }

    fun setOnItemClickListener(listener: ((item: RecipeModel) -> Unit)?) {
        clickListener = listener
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

        fun setItem(item: RecipeModel) {
            Picasso.get().load(item.imageUrl).into(imageView)
        }
    }
}