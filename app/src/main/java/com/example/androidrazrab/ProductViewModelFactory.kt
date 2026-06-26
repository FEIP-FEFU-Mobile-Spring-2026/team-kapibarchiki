package com.example.androidrazrab

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class ProductViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val context: Context
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val repository = ProductRepository(context.applicationContext)
        return ProductViewModel(repository, handle) as T
    }
}