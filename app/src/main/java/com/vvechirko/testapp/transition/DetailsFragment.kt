package com.vvechirko.testapp.transition

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vvechirko.testapp.R
import com.vvechirko.testapp.onEnd
import com.vvechirko.testapp.onPreDraw
import kotlinx.android.synthetic.main.fragment_details.*
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class DetailsFragment : Fragment() {

    companion object {
        val ARG_ALBUM_IMAGE_POSITION = "arg_album_image_position"
        val ARG_STARTING_ALBUM_IMAGE_POSITION = "arg_starting_album_image_position"

        fun newInstance(position: Int, startingPosition: Int): DetailsFragment {
            val args = Bundle().apply {
                putInt(ARG_ALBUM_IMAGE_POSITION, position)
                putInt(ARG_STARTING_ALBUM_IMAGE_POSITION, startingPosition)
            }
            return DetailsFragment().apply { arguments = args }
        }
    }

    var startingPosition: Int = 0
    var albumPosition: Int = 0
    var isTransitioning: Boolean = false
    val backgroundImageFadeMillis: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
        startingPosition = arguments?.getInt(ARG_STARTING_ALBUM_IMAGE_POSITION) ?: 0
        albumPosition = arguments?.getInt(ARG_ALBUM_IMAGE_POSITION) ?: 0
        isTransitioning = savedInstanceState == null && startingPosition == albumPosition
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log("onCreateView")
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log("onViewCreated")

        val albumItem = Interactor.getItems().get(albumPosition)

        detailsAlbumTitle.text = albumItem.title
        detailsAlbumImage.setHasTransientState(false)
        detailsAlbumImage.transitionName = albumItem.title

        val albumImageRequest = Picasso.get().load(albumItem.albumImage)
        val backgroundImageRequest = Picasso.get().load(albumItem.bgImage).fit().centerCrop()

        if (isTransitioning) {
            albumImageRequest.noFade()
            backgroundImageRequest.noFade()
            detailsBackgroundImage.setAlpha(0f)

            activity?.window?.sharedElementEnterTransition?.onEnd {
                log("onTransitionEnd")
                detailsBackgroundImage.animate()
                        .setDuration(backgroundImageFadeMillis).alpha(1f)
            }
        }

        albumImageRequest.into(detailsAlbumImage, imageCallback)
        backgroundImageRequest.into(detailsBackgroundImage)
    }

    val imageCallback = object : Callback {
        override fun onSuccess() {
            startPostponedEnterTransition()
        }

        override fun onError(e: Exception?) {
            startPostponedEnterTransition()
        }
    }

    override fun startPostponedEnterTransition() {
//        super.startPostponedEnterTransition()
        log("startPostponedEnterTransition")
        if (albumPosition == startingPosition) {
            detailsAlbumImage.onPreDraw {
                activity?.startPostponedEnterTransition()
            }
        }
    }

    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on the screen.
     */
    fun getAlbumImage(): ImageView? {
        return activity?.window?.decorView?.let {
            if (isViewInBounds(it, detailsAlbumImage)) detailsAlbumImage else null
        }
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    private fun isViewInBounds(container: View, view: View): Boolean {
        val containerBounds = Rect()
        container.getHitRect(containerBounds)
        return view.getLocalVisibleRect(containerBounds)
    }

    private fun log(msg: String) = Log.d("MT_DetailsFragment", msg)
}