package com.example.androidrazrab

sealed class CatalogUiState {
    object Loading : CatalogUiState()

    data class Success(
        val categories: List<Category>,
        val allProducts: List<Product>,
        val selectedCategoryId: String,
        val visibleProducts: List<Product>
    ) : CatalogUiState()

    data class Error(
        val message: String
    ) : CatalogUiState()
}