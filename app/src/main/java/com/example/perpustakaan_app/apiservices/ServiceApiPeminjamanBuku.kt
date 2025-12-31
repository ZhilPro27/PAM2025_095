package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataPeminjamanBuku
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ServiceApiPeminjamanBuku{
    @GET("peminjaman")
    suspend fun getPeminjamanBuku(): List<DataPeminjamanBuku>

    @GET("peminjaman/search")
    suspend fun getPeminjamanBukuByKeyword(@Query("keyword") keyword: String): List<DataPeminjamanBuku>

    @POST("peminjaman/create")
    suspend fun postPeminjamanBuku(@Body dataPeminjamanBuku: DataPeminjamanBuku):retrofit2.Response<Void>

    @PUT("peminjaman/update/{id_peminjaman}")
    suspend fun putPeminjamanBuku(@Query("id_peminjaman") id_peminjaman: Int,  @Body dataPeminjamanBuku: DataPeminjamanBuku):retrofit2.Response<Void>

    @DELETE("peminjaman/delete/{id_peminjaman}")
    suspend fun deletePeminjamanBuku(@Query("id_peminjaman") id_peminjaman: Int):retrofit2.Response<Void>

}