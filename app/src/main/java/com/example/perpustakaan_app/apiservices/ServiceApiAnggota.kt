package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataAnggota
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ServiceApiAnggota {
    @GET("anggota")
    suspend fun getAnggota(): List<DataAnggota>

    @GET("anggota/search")
    suspend fun getAnggotaByNama(@Query("keyword") keyword: String): List<DataAnggota>

    @POST("anggota/create")
    suspend fun postAnggota(@Body dataAnggota: DataAnggota):retrofit2.Response<Void>

    @PUT("anggota/update/{id_anggota}")
    suspend fun putAnggota(@Query("id_anggota") id_anggota: Int, @Body dataAnggota: DataAnggota):retrofit2.Response<Void>

    @DELETE("anggota/delete/{id_anggota}")
    suspend fun deleteAnggota(@Query("id_anggota") id_anggota: Int):retrofit2.Response<Void>
}