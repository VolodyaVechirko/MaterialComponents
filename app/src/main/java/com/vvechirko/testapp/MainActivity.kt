package com.vvechirko.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vvechirko.testapp.images.ImagesActivity
import com.vvechirko.testapp.transition.MusicActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTransitions.setOnClickListener { start<MusicActivity>() }
        btnScroll.setOnClickListener { start<ScrollActivity>() }
        btnImages.setOnClickListener { start<ImagesActivity>() }
    }
}
