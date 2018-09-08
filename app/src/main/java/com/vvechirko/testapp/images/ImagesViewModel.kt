package com.vvechirko.testapp.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvechirko.testapp.data.Interactor
import com.vvechirko.testapp.data.RecipesResponse
import com.vvechirko.testapp.data.Resource
import com.vvechirko.testapp.data.from

class ImagesViewModel : ViewModel() {

    private val data = MutableLiveData<Resource<RecipesResponse>>()
    fun getData() = data as LiveData<Resource<RecipesResponse>>

    fun init() {
        if (data.value == null) {
            data.from(Interactor.getRecipesCache())
        }
    }
}