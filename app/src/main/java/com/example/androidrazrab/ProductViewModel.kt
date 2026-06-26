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

    private var selectedCategoryId: String
        get() = savedStateHandle["selected_category_id"] ?: NEW_CATEGORY_ID
        set(value) {
            savedStateHandle["selected_category_id"] = value
        }

    fun load() {

        if (_uiState.value is CatalogUiState.Success) return

        _uiState.value = CatalogUiState.Loading

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    delay(2000)
                    repository.loadCatalog()
                }

                categories = listOf(Category(NEW_CATEGORY_ID, "Новинки")) + response.categories
                allProducts = response.items

                if (categories.none { it.id == selectedCategoryId }) {
                    selectedCategoryId = NEW_CATEGORY_ID
                }

                emitSuccess()
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(
                    message = "Не удалось загрузить каталог"
                )
            }
        }
    }

    fun retry() {
        _uiState.value = CatalogUiState.Loading
        categories = emptyList()
        allProducts = emptyList()
        load()
    }

    fun selectCategory(categoryId: String) {
        selectedCategoryId = categoryId
        emitSuccess()
    }

    private fun emitSuccess() {
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
            visibleProducts = visibleProducts
        )
    }

    companion object {
        const val NEW_CATEGORY_ID = "cat_new"
    }
}