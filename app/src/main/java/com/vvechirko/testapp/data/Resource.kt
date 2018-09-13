package com.vvechirko.testapp.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.vvechirko.testapp.observe
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import retrofit2.Call

class Resource<T>(

        var status: Status,

        var data: T? = null,

        var error: Throwable? = null
) {

    companion object {
        fun <T> success(data: T?) = Resource<T>(Status.SUCCESS, data)

        fun <T> error(error: Throwable?) = Resource<T>(Status.ERROR, null, error)

        fun <T> loading() = Resource<T>(Status.LOADING)
    }

    enum class Status {
        SUCCESS,
        LOADING,
        ERROR
    }
}

class ResourceObserver<K, T : Resource<K>>(

        val success: ((data: K?) -> Unit)? = null,

        val error: ((error: String) -> Unit)? = null,

        val loading: (() -> Unit)? = null

) : Observer<Resource<K>> {

    override fun onChanged(t: Resource<K>?) {
        when (t?.status) {
            Resource.Status.SUCCESS -> success?.invoke(t.data)
            Resource.Status.ERROR -> error?.invoke(t.error?.message ?: t.error.toString())
            Resource.Status.LOADING -> loading?.invoke()
        }
    }
}

fun <T> MutableLiveData<Resource<T>>.from(call: Call<T>) {
    call.observe(
            onStart = { postValue(Resource.loading()) },
            onSuccess = { postValue(Resource.success(it)) },
            onError = { postValue(Resource.error(it)) }
    )
}

//fun <T> LiveData<Resource<T>>.from(call: Call<T>) {
//    object : LiveData<Resource<T>>() {
//
//        override fun onActive() {
//            super.onActive()
//            call.observe(
//                    onStart = { postValue(Resource.loading()) },
//                    onSuccess = { postValue(Resource.success(it)) },
//                    onError = { postValue(Resource.error(it)) }
//            )
//        }
//    }
//}

class ResourceData<T> : LiveData<Resource<T>>() {

    fun observeResource(
            owner: LifecycleOwner,
            success: ((data: T?) -> Unit)? = null,
            error: ((error: String) -> Unit)? = null,
            loading: (() -> Unit)? = null

    ) {
//        observe(owner, ResourceObserver(success, error, loading))
        observe(owner, Observer {
            when (it?.status) {
                Resource.Status.SUCCESS -> success?.invoke(it.data)
                Resource.Status.ERROR -> error?.invoke(it.error.toString())
                Resource.Status.LOADING -> loading?.invoke()
            }
        })
    }

    fun adapt(call: Call<T>) {
        call.observe(
                onStart = { postValue(Resource.loading()) },
                onSuccess = { postValue(Resource.success(it)) },
                onError = { postValue(Resource.error(it)) }
        )
    }

    fun adapt(deferred: Deferred<T>) {
        postValue(Resource.loading())
        GlobalScope.launch {
            try {
                val result = deferred.await()
                postValue(Resource.success(result))
            } catch (ex: Exception) {
                postValue(Resource.error(ex))
            }
        }
    }
}