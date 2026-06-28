package com.example.androidrazrab

import android.content.Context
import com.google.gson.Gson

class ProductRepository(context: Context) {

    private val api = RetrofitClient.productApiService
    private val databaseHelper = CatalogDatabaseHelper(context.applicationContext)
    private val gson = Gson()

    fun getCachedCatalog(): ProductResponse? {
        val json = databaseHelper.getCatalogJson() ?: return null
        return gson.fromJson(json, ProductResponse::class.java)
    }

    suspend fun refreshCatalogFromApi(): ProductResponse {
        val response = api.getCatalog()
        val json = gson.toJson(response)

        databaseHelper.saveCatalog(json)

        return response
    }
}