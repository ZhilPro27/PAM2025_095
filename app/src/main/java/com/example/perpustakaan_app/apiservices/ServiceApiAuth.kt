package com.example.perpustakaan_app.apiservices

import com.example.perpustakaan_app.modeldata.LoginRequest
import com.example.perpustakaan_app.modeldata.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ServiceApiAuth {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest):Response<LoginResponse>
}