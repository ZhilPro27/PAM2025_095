package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DataAnggota (
    val id_anggota: Int,
    val nama: String,
    val no_hp: String,
    val alamat: String
) {
    override fun toString(): String = nama
}

data class UIStateAnggota (
    val detailAnggota: DetailAnggota = DetailAnggota(),
    val isEntryValid: Boolean = false
)

data class DetailAnggota (
    val id_anggota: Int = 0,
    val nama: String = "",
    val no_hp: String = "",
    val alamat: String = ""
)

fun DetailAnggota.toDataAnggota() : DataAnggota = DataAnggota(
    id_anggota = id_anggota,
    nama = nama,
    no_hp = no_hp,
    alamat = alamat
)

fun DataAnggota.toUiStateAnggota(isEntryValid: Boolean = false) : UIStateAnggota = UIStateAnggota(
    detailAnggota = this.toDetailAnggota(),
    isEntryValid = isEntryValid
)

fun DataAnggota.toDetailAnggota() : DetailAnggota = DetailAnggota(
    id_anggota = id_anggota,
    nama = nama,
    no_hp = no_hp,
    alamat = alamat
)