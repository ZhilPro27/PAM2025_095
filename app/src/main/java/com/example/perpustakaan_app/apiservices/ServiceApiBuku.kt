package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataBuku
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ServiceApiBuku {
    @GET("buku")
    suspend fun getBuku(): List<DataBuku>

    @GET("buku/search")
    suspend fun getBukuByKeyword(@Query("keyword") keyword: String): List<DataBuku>

    @POST("buku/create")
    suspend fun postBuku(@Body dataBuku: DataBuku):retrofit2.Response<Void>

    @PUT("buku/update/{id_buku}")
    suspend fun putBuku(@Query("id_buku") id_buku: Int, @Body dataBuku: DataBuku):retrofit2.Response<Void>

    @DELETE("buku/delete/{id_buku}")
    suspend fun deleteBuku(@Query("id_buku") id_buku: Int):retrofit2.Response<Void>
}


