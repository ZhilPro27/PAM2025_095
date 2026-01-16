package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.DataProfil
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServiceApiProfil {
    @GET("pustakawan/email/{email}")
    suspend fun getProfilbyEmail(@Path("email") email: String): DataProfil

    @GET("pustakawan/{id_pustakawan}")
    suspend fun getProfilById(@Path("id_pustakawan") id_pustakawan: Int): DataProfil

    @PUT("pustakawan/{id_pustakawan}")
    suspend fun putProfil(
        @Path("id_pustakawan") id_pustakawan: Int,
        @Body dataProfil: DataProfil
    ): retrofit2.Response<Void>
}

