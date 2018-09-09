package com.vvechirko.testapp

import android.app.Application
import com.squareup.picasso.Picasso

class TestApp : Application() {

    companion object {
        lateinit var instance: TestApp

        fun assets() = instance.assets
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Picasso.setSingletonInstance(Picasso.Builder(this).build())
    }
}