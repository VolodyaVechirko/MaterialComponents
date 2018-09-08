package com.vvechirko.testapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            Resource.Status.ERROR -> error?.invoke(t.error.toString())
            Resource.Status.LOADING -> loading?.invoke()
        }
    }
}

fun <T> MutableLiveData<Resource<T>>.from(call: Call<T>) {
    postValue(Resource.loading())
    call.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>?, t: Throwable) {
            postValue(Resource.error(t))
        }

        override fun onResponse(call: Call<T>?, response: Response<T>) {
            postValue(Resource.success(response.body()))
        }
    })
}