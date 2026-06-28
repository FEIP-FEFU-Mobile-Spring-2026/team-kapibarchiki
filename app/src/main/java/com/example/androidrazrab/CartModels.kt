package com.example.androidrazrab

data class CartDbItem(
    val productId: String,
    val sizeId: String,
    val quantity: Int
)

data class CartUiItem(
    val product: Product,
    val size: ProductSize,
    val quantity: Int
)