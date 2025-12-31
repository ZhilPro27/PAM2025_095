package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataCatatanDenda
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface ServiceApiCatatanDenda {
    @GET("denda")
    suspend fun getCatatanDenda(): List<DataCatatanDenda>

    @GET("denda/search")
    suspend fun getCatatanDendaByKeyword(@Query("keyword") keyword: String): List<DataCatatanDenda>

    @PUT("denda/update/{id_denda}")
    suspend fun putCatatanDenda(@Query("id_denda") id_denda: Int, @Body dataCatatanDenda: DataCatatanDenda)
}