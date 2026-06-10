package com.example.androidrazrab

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: String,
    val image: String? = null
)