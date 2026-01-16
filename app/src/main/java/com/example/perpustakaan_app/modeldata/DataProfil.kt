package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DataProfil(
    val id_pustakawan: Int,
    val email: String,
    val password: String? = null
)

data class UIStateProfil(
    val detailProfil: DetailProfil = DetailProfil(),
    val isEntryValid: Boolean = false
)

@Serializable
data class DetailProfil(
    val id_pustakawan: Int = 0,
    val email: String = "",
    val password: String = ""
)

fun DetailProfil.toDataProfil() : DataProfil = DataProfil(
    id_pustakawan = id_pustakawan,
    email = email,
    password = password
)

fun DataProfil.toUiStateProfil(isEntryValid: Boolean = false) : UIStateProfil = UIStateProfil(
    detailProfil = this.toDetailProfil(),
    isEntryValid = isEntryValid
)

fun DataProfil.toDetailProfil() : DetailProfil = DetailProfil(
    id_pustakawan = id_pustakawan,
    email = email,
    password = password ?: ""
)