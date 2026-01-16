package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataProfil
import com.example.perpustakaan_app.modeldata.DetailProfil
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApiProfil {
    @GET("pustakawan/email/{email}")
    suspend fun getProfilbyEmail(@Path("email") email: String): DataProfil

    @GET("pustakawan/{id_pustakawan}")
    suspend fun getProfilById(@Path("id_pustakawan") id_pustakawan: Int): DataProfil

    @Multipart
    @PUT("pustakawan/{id_pustakawan}")
    suspend fun putProfil(
        @Path("id_pustakawan") id_pustakawan: Int,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody? = null
    ): retrofit2.Response<Void>
}

