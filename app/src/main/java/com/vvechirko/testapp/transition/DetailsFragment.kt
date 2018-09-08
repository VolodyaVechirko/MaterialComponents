package com.vvechirko.testapp.transition

import android.annotation.TargetApi
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vvechirko.testapp.R
import kotlinx.android.synthetic.main.fragment_details.*
import java.lang.Exception

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        startingPosition = arguments?.getInt(ARG_STARTING_ALBUM_IMAGE_POSITION) ?: 0
        albumPosition = arguments?.getInt(ARG_ALBUM_IMAGE_POSITION) ?: 0
        isTransitioning = savedInstanceState == null && startingPosition == albumPosition
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumItem = Interactor.getItems().get(albumPosition)

        detailsAlbumTitle.text = albumItem.title
        detailsAlbumImage.transitionName = albumItem.title

        val albumImageRequest = Picasso.get().load(albumItem.albumImage)
        val backgroundImageRequest = Picasso.get().load(albumItem.bgImage).fit().centerCrop()

        if (isTransitioning) {
            albumImageRequest.noFade()
            backgroundImageRequest.noFade()
            detailsBackgroundImage.setAlpha(0f)

            activity?.window?.sharedElementEnterTransition?.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition?) {
                    detailsBackgroundImage?.let {
                        it.animate().setDuration(backgroundImageFadeMillis).alpha(1f)
                    }
                }

                override fun onTransitionResume(transition: Transition?) {}

                override fun onTransitionPause(transition: Transition?) {}

                override fun onTransitionCancel(transition: Transition?) {}

                override fun onTransitionStart(transition: Transition?) {}
            })
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
        if (albumPosition == startingPosition) {
            detailsAlbumImage.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    detailsAlbumImage.viewTreeObserver.removeOnPreDrawListener(this)
                    activity?.startPostponedEnterTransition()
                    return true
                }
            })
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
}