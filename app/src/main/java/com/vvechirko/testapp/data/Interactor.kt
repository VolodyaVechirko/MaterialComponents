package com.vvechirko.testapp.data

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.vvechirko.testapp.TestApp
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Interactor {

    const val BASE_URL = "https://www.food2fork.com/"
    const val API_KEY = "eae37285eb7db643268b4e7c10376fb5"

    val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

    fun getRecipes() = api.getAll(API_KEY)

    fun getRecipesCache(): Deferred<RecipesResponse> {
        val deferred = CompletableDeferred<RecipesResponse>()
        deferred.complete(getCached())
        return deferred
    }

    fun getRecipe(id: String) = api.get(API_KEY, id)

    fun getCached(): RecipesResponse {
        val json = TestApp.assets().open("api_responce.json")
                .bufferedReader().use { it.readText() }
        return Gson().fromJson(json, RecipesResponse::class.java)
    }

    fun getSearch() = api.search(API_KEY)
}