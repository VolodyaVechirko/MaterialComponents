package com.vvechirko.testapp.data

import androidx.lifecycle.LiveData
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory private constructor() : CallAdapter.Factory() {

    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = LiveDataCallAdapterFactory()
    }

    override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (LiveData::class.java != getRawType(returnType)) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException("LiveData return type must be parameterized")
        }
        val responseType = getParameterUpperBound(0, returnType)
        val rawLivaDataType = getRawType(responseType)

        return if (rawLivaDataType == Resource::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException("Resource must be parameterized")
            }
            LiveDataCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else null
    }

    private class LiveDataCallAdapter<R>(
            private val responseType: Type
    ) : CallAdapter<R, LiveData<Resource<R>>> {

        override fun responseType(): Type = responseType

        override fun adapt(call: Call<R>): LiveData<Resource<R>> {
            return object : LiveData<Resource<R>>() {
                var started = AtomicBoolean(false)

                override fun onActive() {
                    super.onActive()
                    if (started.compareAndSet(false, true)) {
                        postValue(Resource.loading())
                        call.enqueue(object : Callback<R> {
                            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                                postValue(Resource.success(response?.body()))
                            }

                            override fun onFailure(call: Call<R>, throwable: Throwable) {
                                postValue(Resource.error(throwable))
                            }
                        })
                    }
                }
            }
        }
    }
}