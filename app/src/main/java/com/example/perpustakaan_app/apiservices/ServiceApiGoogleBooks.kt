package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceApiGoogleBooks {
    @GET("volumes")
    suspend fun getVolumeByIsbn(@Query("q") query: String): GoogleBooksResponse
}
