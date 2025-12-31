package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val token: String,
    val userid: String,
    val email: String
)