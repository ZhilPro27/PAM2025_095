package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    @SerialName("token") val token: String,
    // Tambahkan field ini (sesuaikan nama field dengan JSON dari backend VPS)
    @SerialName("id") val id: Int? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("msg") val message: String? = null
)