package com.example.androidrazrab

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val productApiService: ProductApiService =
        Retrofit.Builder()
            .baseUrl("https://fefu2026spring.deploy.feip.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
}