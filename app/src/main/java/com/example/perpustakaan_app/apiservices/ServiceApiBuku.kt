package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataBuku
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApiBuku {
    @GET("buku")
    suspend fun getBuku(): List<DataBuku>

    @GET("buku/{id_buku}")
    suspend fun getBukuById(@Path("id_buku") id_buku: Int): DataBuku

    @GET("buku/search")
    suspend fun getBukuByKeyword(@Query("keyword") keyword: String): List<DataBuku>

    @Multipart
    @POST("buku/create")
    suspend fun postBuku(
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("penerbit") penerbit: RequestBody,
        @Part("tahun_terbit") tahunTerbit: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("isbn") isbn: RequestBody,
        @Part file: MultipartBody.Part? = null,
        @Part("nomor_panggil") nomor_panggil: RequestBody,
        @Part("deskripsi")deskripsi: RequestBody
    ): retrofit2.Response<Void>

    @Multipart
    @PUT("buku/update/{id_buku}")
    suspend fun putBuku(
        @Path("id_buku") id_buku: Int,
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("penerbit") penerbit: RequestBody,
        @Part("tahun_terbit") tahunTerbit: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("isbn") isbn: RequestBody,
        @Part file: MultipartBody.Part? = null,
        @Part("nomor_panggil") nomor_panggil: RequestBody,
        @Part("deskripsi")deskripsi: RequestBody
    ): retrofit2.Response<Void>


    @DELETE("buku/delete/{id_buku}")
    suspend fun deleteBuku(@Path("id_buku") id_buku: Int):retrofit2.Response<Void>
}


