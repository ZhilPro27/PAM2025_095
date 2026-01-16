package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DataCatatanDenda(
    val id_denda: Int,
    val id_peminjaman: Int,
    val jumlah: Int,
    val hari_terlambat: Int,
    val status: String,
    val tanggal_dibayar: String? = null,
    val nama: String? = null
)

data class UIStateCatatanDenda(
    val detailCatatanDenda: DetailCatatanDenda = DetailCatatanDenda(),
    val isEntryValid: Boolean = false
)

data class DetailCatatanDenda(
    val id_denda: Int = 0,
    val id_peminjaman: Int = 0,
    val jumlah: Int = 0,
    val hari_terlambat: Int = 0,
    val status: String = "",
    val tanggal_dibayar: String = "",
    val nama: String = ""
)

fun DetailCatatanDenda.toDataCatatanDenda() : DataCatatanDenda = DataCatatanDenda(
    id_denda = id_denda,
    id_peminjaman = id_peminjaman,
    jumlah = jumlah,
    hari_terlambat = hari_terlambat,
    status = status,
    tanggal_dibayar = if (tanggal_dibayar.isBlank()) null else tanggal_dibayar,
    nama = nama
)

