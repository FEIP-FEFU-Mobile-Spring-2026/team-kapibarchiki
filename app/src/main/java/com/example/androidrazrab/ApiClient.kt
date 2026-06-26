package com.example.androidrazrab

import retrofit2.http.GET
import retrofit2.http.Header

interface ProductApiService {

    @GET("catalog")
    suspend fun getCatalog(
        @Header("Authorization") token: String = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    ): ProductResponse
}