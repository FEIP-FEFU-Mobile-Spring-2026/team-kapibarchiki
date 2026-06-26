package com.example.androidrazrab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class ProductViewModel(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableLiveData<CatalogUiState>()
    val uiState: LiveData<CatalogUiState> = _uiState

    private var categories: List<Category> = emptyList()
    private var allProducts: List<Product> = emptyList()
    private var lastIsOffline: Boolean = false
    internal var selectedCategoryId: String
        get() = savedStateHandle["selected_category_id"] ?: NEW_CATEGORY_ID
        set(value) {
            savedStateHandle["selected_category_id"] = value
        }

    fun load() {
        _uiState.value = CatalogUiState.Loading

        viewModelScope.launch {

                val cachedCatalog = withContext(Dispatchers.IO) {
                    repository.getCachedCatalog()
                }

                if (cachedCatalog != null) {
                    applyCatalog(cachedCatalog, isOffline = false)
                }

                try {
                    val apiCatalog = withContext(Dispatchers.IO) {
                        repository.refreshCatalogFromApi()
                    }

                    applyCatalog(apiCatalog, isOffline = false)
                } catch (e: Exception) {
                    if (cachedCatalog != null) {
                        applyCatalog(cachedCatalog, isOffline = true)
                    } else {
                        _uiState.value = CatalogUiState.Error(
                            message = "Нет сети и кэш пустой"
                        )
                    }
                }
            }
        }


    fun retry() {
        load()
    }
    private fun applyCatalog(
        response: ProductResponse,
        isOffline: Boolean
    ) {
        lastIsOffline = isOffline
        categories = listOf(Category(NEW_CATEGORY_ID, "Новинки")) + response.categories
        allProducts = response.items

        if (categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = NEW_CATEGORY_ID
        }

        emitSuccess(isOffline)
    }
    fun selectCategory(categoryId: String) {
        selectedCategoryId = categoryId
        emitSuccess(lastIsOffline)
    }
    fun saveScrollPosition(categoryId: String, position: Int) {
        savedStateHandle["scroll_$categoryId"] = position
    }

    fun getScrollPosition(categoryId: String): Int {
        return savedStateHandle["scroll_$categoryId"] ?: 0
    }
    private fun emitSuccess(isOffline: Boolean) {
        val visibleProducts = if (selectedCategoryId == NEW_CATEGORY_ID) {
            allProducts.filter { product ->
                product.tags.any { it.equals("New", ignoreCase = true) }
            }
        } else {
            allProducts.filter { it.categoryId == selectedCategoryId }
        }

        _uiState.value = CatalogUiState.Success(
            categories = categories,
            allProducts = allProducts,
            selectedCategoryId = selectedCategoryId,
            visibleProducts = visibleProducts,
            isOffline = isOffline
        )
    }


    companion object {
        const val NEW_CATEGORY_ID = "cat_new"
    }
}