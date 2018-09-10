package com.vvechirko.testapp.images

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.vvechirko.testapp.*
import com.vvechirko.testapp.data.Interactor
import com.vvechirko.testapp.data.RecipesResponse
import kotlinx.android.synthetic.main.activity_image.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ImageActivity : AppCompatActivity() {

    companion object {
        const val START_POINT = "START_POINT"
        const val ANIM_DURATION = 600L

        fun start(context: Context, point: Point) {
            context.startActivity(Intent(context, ImageActivity::class.java)
                    .putExtra(START_POINT, point)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
    }

    private lateinit var rootLayout: View
    private lateinit var startPoint: Point

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        startPoint = intent.getParcelableExtra(START_POINT)
        rootLayout = findViewById<View>(android.R.id.content)

        if (savedInstanceState == null) {
            rootLayout.visibility = View.INVISIBLE
            rootLayout.onGlobalLayout {
                circularRevealActivityStart()
            }
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }

        // using coroutines
        load { Interactor.getCached() } then { showItem(it) }

        // using Call<T>.observe() extension
//        Interactor.getRecipesCache().observe(
//            onSuccess = { showItem(it) }
//        )
    }

    private fun showItem(resp: RecipesResponse?) {
        resp?.recipes?.first()?.let {
            albumTitle.text = it.title
            Picasso.get().load(it.imageUrl).fit().centerCrop().into(backgroundImage)
        }
    }

    private fun circularRevealActivityStart() {
        val cx = startPoint.x //rootLayout.left + rootLayout.right
        val cy = startPoint.y //rootLayout.top + rootLayout.bottom
        // get the hypothenuse so the radius is from one corner to the other
        val radius = Math.hypot(rootLayout.right.toDouble(), rootLayout.bottom.toDouble()).toFloat()

        ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0f, radius)
                .setDuration(ANIM_DURATION)
                .addInterpolator(AccelerateInterpolator())
                .onStart { rootLayout.visibility = View.VISIBLE }
                .start()
    }

    override fun onBackPressed() {
        val cx = startPoint.x //rootLayout.left + rootLayout.right
        val cy = startPoint.y //rootLayout.top + rootLayout.bottom
        // get the hypothenuse so the radius is from one corner to the other
        val radius = Math.hypot(rootLayout.right.toDouble(), rootLayout.bottom.toDouble()).toFloat()

        ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, radius, 0f)
                .setDuration(ANIM_DURATION)
                .addInterpolator(AccelerateInterpolator())
                .onEnd {
                    rootLayout.visibility = View.INVISIBLE
                    super.onBackPressed()
                }
                .start()
    }
}