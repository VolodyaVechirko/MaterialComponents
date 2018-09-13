package com.vvechirko.testapp.images

import androidx.lifecycle.ViewModel
import com.vvechirko.testapp.data.Interactor
import com.vvechirko.testapp.data.RecipesResponse
import com.vvechirko.testapp.data.ResourceData

class ImagesViewModel : ViewModel() {

    val data = ResourceData<RecipesResponse>()

    fun init() {
        if (data.value == null) {
//            data.adapt(Interactor.getRecipesCache())
            data.adapt(Interactor.getSearch())
        }
    }
}