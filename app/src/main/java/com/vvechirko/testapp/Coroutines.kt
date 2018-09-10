package com.vvechirko.testapp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

internal val Background = newFixedThreadPoolContext(2, "bg")

fun <T> LifecycleOwner.load(loader: () -> T): Deferred<T> {

    val deferred = async(
            context = Background,
            start = CoroutineStart.LAZY
    ) {
        loader()
    }

    lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun cancel() {
            if (!deferred.isCancelled) {
                deferred.cancel()
            }
        }
    })

    return deferred
}

infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
    return launch(context = UI) {
        block(this@then.await())
    }
}
