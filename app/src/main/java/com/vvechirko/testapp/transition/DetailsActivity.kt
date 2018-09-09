package com.vvechirko.testapp.transition

import android.app.SharedElementCallback
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.vvechirko.testapp.R
import com.vvechirko.testapp.transition.MusicActivity.Companion.EXTRA_CURRENT_ALBUM_POSITION
import com.vvechirko.testapp.transition.MusicActivity.Companion.EXTRA_STARTING_ALBUM_POSITION
import kotlinx.android.synthetic.main.activity_details.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class DetailsActivity : AppCompatActivity() {

    companion object {
        val STATE_CURRENT_PAGE_POSITION = "state_current_page_position"
    }

    private var currentDetailsFragment: DetailsFragment? = null
    private var currentPosition: Int = 0
    private var startingPosition: Int = 0
    private var isReturning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setupWindowAnimations()
        log("onCreate")
        postponeEnterTransition()
        setEnterSharedElementCallback(sharedElementCallback)

        startingPosition = intent.getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0)
        if (savedInstanceState == null) {
            currentPosition = startingPosition
        } else {
            currentPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION)
        }

        viewPager.apply {
            adapter = DetailsFragmentPagerAdapter(supportFragmentManager)
            currentItem = currentPosition
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    currentPosition = position
                }
            })
        }
    }

    private fun setupWindowAnimations() {
        window.enterTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.details_window_enter_transition)
        window.returnTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.details_window_return_transition)
    }

    val sharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            log("onMapSharedElements $names, $sharedElements")
            if (isReturning) {
                val sharedElement = currentDetailsFragment?.getAlbumImage()
                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear()
                    sharedElements.clear()
                } else if (startingPosition != currentPosition) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear()
                    names.add(sharedElement.transitionName)
                    sharedElements.clear()
                    sharedElements.put(sharedElement.transitionName, sharedElement)
                }
            }
        }
    }

    override fun finishAfterTransition() {
        log("finishAfterTransition")
        isReturning = true
        val data = Intent()
                .putExtra(EXTRA_STARTING_ALBUM_POSITION, startingPosition)
                .putExtra(EXTRA_CURRENT_ALBUM_POSITION, currentPosition)
        setResult(RESULT_OK, data)
        super.finishAfterTransition()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_PAGE_POSITION, currentPosition)
    }

    inner class DetailsFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return DetailsFragment.newInstance(position, startingPosition)
        }

        override fun getCount(): Int {
            return Interactor.getItems().size
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
            super.setPrimaryItem(container, position, obj)
            currentDetailsFragment = obj as DetailsFragment
        }
    }

    private fun log(msg: String) = Log.d("MT_DetailsActivity", msg)
}