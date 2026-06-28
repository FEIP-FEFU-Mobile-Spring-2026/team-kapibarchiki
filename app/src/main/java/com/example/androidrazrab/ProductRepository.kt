package com.example.androidrazrab

import android.content.Context
import com.google.gson.Gson

class ProductRepository(
    private val context: Context
) {
    fun loadCatalog(): ProductResponse {
        val json = context.assets
            .open("products.json")
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(json, ProductResponse::class.java)
    }
}