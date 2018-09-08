package com.vvechirko.testapp.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("api/search")
    fun search(): Call<RecipesResponse>

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