package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiAuth
import com.example.perpustakaan_app.modeldata.LoginRequest
import com.example.perpustakaan_app.modeldata.LoginResponse
import org.json.JSONObject


class RepositoryAuth(
    private val apiService: ServiceApiAuth,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, pass))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // AMBIL DATA DARI RESPONSE
                    // Pastikan null safety (jika server kadang tidak kirim id/email)
                    val token = body.token
                    val id = body.id?.toString() ?: "" // Ubah Int ke String
                    val emailUser = body.email ?: email // Pakai email inputan jika respon kosong



                    // SIMPAN KE DATASTORE
                    userPreferences.saveSession(token, id, emailUser)

                    Result.success(response.body()?.message ?: "Login Berhasil")
                } else {
                    Result.failure(Exception("Login Gagal: Respon Kosong"))
                }
            } else {
                val errorJson = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorJson).getString("msg")
                } catch (e: Exception) {
                    "Login Gagal: ${response.message()}"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
