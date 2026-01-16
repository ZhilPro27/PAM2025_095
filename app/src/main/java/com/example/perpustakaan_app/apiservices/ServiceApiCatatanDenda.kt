package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataCatatanDenda
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApiCatatanDenda {
    @GET("denda")
    suspend fun getCatatanDenda(): List<DataCatatanDenda>

    @GET("denda/{id_denda}")
    suspend fun getCatatanDendaById(@Path("id_denda") id_denda: Int): DataCatatanDenda

    @GET("denda/search/{keyword}")
    suspend fun getCatatanDendaByKeyword(@Path("keyword") keyword: String): List<DataCatatanDenda>

    @POST("denda/status/{id_denda}")
    suspend fun putCatatanDenda(@Path("id_denda") id_denda: Int, @Body dataCatatanDenda: Map<String, String>)
}