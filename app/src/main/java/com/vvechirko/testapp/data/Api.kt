package com.vvechirko.testapp.data

import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("api/search")
    fun search(
            @Query("key") key: String
    ): Deferred<RecipesResponse>

    @GET("api/search")
    fun getAll(
            @Query("key") key: String
    ): Call<RecipesResponse>

    @GET("api/get")
    fun get(
            @Query("key") key: String,
            @Query("rId") id: String
    ): Call<RecipeResponse>
}