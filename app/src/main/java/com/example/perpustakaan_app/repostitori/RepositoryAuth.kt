package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiAuth
import com.example.perpustakaan_app.modeldata.LoginRequest
import com.example.perpustakaan_app.modeldata.LoginResponse


class RepositoryAuth(
    private val apiService: ServiceApiAuth,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, pass))

            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                userPreferences.saveToken(token)
                Result.success("Login Berhasil")
            } else {
                Result.failure(Exception("Login Gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}