package com.vvechirko.testapp

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Window
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.statusBarColorRes(@ColorRes resId: Int) {
    statusBarColor = ContextCompat.getColor(context, resId)
}

fun Toolbar.setNavigationAnim(@DrawableRes resId: Int) {
    navigationIcon = AnimatedVectorDrawableCompat.create(context, resId).also { it?.start() }
}

fun AppCompatActivity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

inline fun <reified T> Context.start() {
    startActivity(Intent(this, T::class.java))
}