package com.vvechirko.testapp.data

import com.google.gson.Gson
import com.vvechirko.testapp.TestApp
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Interactor {

    const val BASE_URL = "https://www.food2fork.com/"
    const val API_KEY = "eae37285eb7db643268b4e7c10376fb5"

    val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
//            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

    fun getRecipes() = api.getAll(API_KEY)

    fun getRecipesCache(): Call<RecipesResponse> {
        return object : Call<RecipesResponse> {
            override fun clone(): Call<RecipesResponse> {
                TODO("not implemented")
            }

            override fun isCanceled(): Boolean {
                TODO("not implemented")
            }

            override fun cancel() {
                TODO("not implemented")
            }

            override fun execute(): Response<RecipesResponse> {
                TODO("not implemented")
            }

            override fun request(): Request {
                TODO("not implemented")
            }

            override fun isExecuted(): Boolean {
                TODO("not implemented")
            }

            override fun enqueue(callback: Callback<RecipesResponse>) {
                val json = TestApp.assets().open("api_responce.json")
                        .bufferedReader().use { it.readText() }

                callback.onResponse(this, Response.success(Gson()
                        .fromJson(json, RecipesResponse::class.java)))
            }
        }
    }

    fun getRecipe(id: String) = api.get(API_KEY, id)
}