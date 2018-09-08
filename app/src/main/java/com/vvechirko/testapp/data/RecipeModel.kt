package com.vvechirko.testapp.data

import com.google.gson.annotations.SerializedName

data class RecipeModel(

        @SerializedName("recipe_id")
        var id: String,

        var publisher: String,

        @SerializedName("f2f_url")
        var f2fUrl: String,

        var title: String,

        @SerializedName("source_url")
        var sourceUrl: String,

        @SerializedName("image_url")
        var imageUrl: String,

        @SerializedName("social_rank")
        var social_rank: Float,

        @SerializedName("publisher_url")
        var publisher_url: String,

        var ingredients: List<String>? = null
)