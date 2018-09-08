package com.vvechirko.testapp.transition

import android.annotation.TargetApi
import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vvechirko.testapp.R
import kotlinx.android.synthetic.main.activity_music.*

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class MusicActivity : AppCompatActivity() {

    companion object {
        val EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position"
        val EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position"
    }

    var reenterState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        setExitSharedElementCallback(sharedElementCallback)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView.adapter = Adapter().apply {
            setItemClickListener(::openItem)
            setData(Interactor.getItems())
        }
    }

    private fun openItem(position: Int, view: View) {
        val intent = Intent(this, DetailsActivity::class.java)
                .putExtra(EXTRA_STARTING_ALBUM_POSITION, position)

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                this, view, view.transitionName).toBundle())
    }

    private val sharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (reenterState != null) {
                val startingPosition = reenterState?.getInt(EXTRA_STARTING_ALBUM_POSITION) ?: 0
                val currentPosition = reenterState?.getInt(EXTRA_CURRENT_ALBUM_POSITION) ?: 0
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    val newTransitionName = Interactor.getItems().get(currentPosition).title
                    val newSharedElement = recyclerView.findViewWithTag<View>(newTransitionName)
                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)
                        sharedElements.clear()
                        sharedElements.put(newTransitionName, newSharedElement)
                    }
                }

                reenterState = null
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                val navigationBar = findViewById<View>(android.R.id.navigationBarBackground)
                val statusBar = findViewById<View>(android.R.id.statusBarBackground)
                if (navigationBar != null) {
                    names.add(navigationBar.transitionName)
                    sharedElements.put(navigationBar.transitionName, navigationBar)
                }
                if (statusBar != null) {
                    names.add(statusBar.transitionName)
                    sharedElements.put(statusBar.transitionName, statusBar)
                }
            }
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        reenterState = Bundle(data.extras)
        val startingPosition = reenterState?.getInt(EXTRA_STARTING_ALBUM_POSITION) ?: 0
        val currentPosition = reenterState?.getInt(EXTRA_CURRENT_ALBUM_POSITION) ?: 0
        if (startingPosition != currentPosition) {
            recyclerView.scrollToPosition(currentPosition)
        }

        postponeEnterTransition()
        recyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                recyclerView.requestLayout()
                startPostponedEnterTransition()
                return true
            }
        })
    }

    class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        private val data: MutableList<ItemModel> = mutableListOf()
        private var itemClickListener: ((position: Int, view: View) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_card, parent, false))
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.apply {
                bind(data.get(position))
                itemView.setOnClickListener {
                    itemClickListener?.invoke(adapterPosition, albumImage)
                }
            }
        }

        fun setData(list: List<ItemModel>) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

        fun setItemClickListener(listener: ((position: Int, view: View) -> Unit)?) {
            itemClickListener = listener
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {

            val albumImage = itemView.findViewById<AppCompatImageView>(R.id.main_card_album_image)

            fun bind(item: ItemModel) {
                Picasso.get().load(item.albumImage).into(albumImage)
                albumImage.transitionName = item.title
                albumImage.tag = item.title
            }
        }
    }
}