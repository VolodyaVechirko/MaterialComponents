package com.vvechirko.testapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.transition.Transition
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

fun View.onPreDraw(action: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            action.invoke()
            return true
        }
    })
}

var View.visible: Boolean
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
    get() {
        return visibility == View.VISIBLE
    }

fun View.onGlobalLayout(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action.invoke()
        }
    })
}

fun View.centerLocation(): Point {
    return Point(x.toInt() + width / 2, y.toInt() + height / 2)
}

fun Transition.onEnd(action: () -> Unit) {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition?) {
            removeListener(this)
            action.invoke()
        }

        override fun onTransitionResume(transition: Transition?) {}

        override fun onTransitionPause(transition: Transition?) {}

        override fun onTransitionCancel(transition: Transition?) {}

        override fun onTransitionStart(transition: Transition?) {}
    })
}

fun Animator.addInterpolator(value: TimeInterpolator): Animator {
    interpolator = value
    return this
}

fun Animator.onStart(action: () -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            action.invoke()
        }
    })
    return this
}

fun Animator.onEnd(action: () -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            action.invoke()
        }
    })
    return this
}

inline fun <reified T> Context.start() {
    startActivity(Intent(this, T::class.java))
}

fun <T> Call<T>.observe(
        onStart: (() -> Unit)? = null,
        onSuccess: ((data: T?) -> Unit)? = null,
        onError: ((t: Throwable) -> Unit)? = null
) {
    onStart?.invoke()
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            onSuccess?.invoke(response?.body())
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            onError?.invoke(throwable)
        }
    })
}