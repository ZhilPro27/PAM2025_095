package com.example.perpustakaan_app.modeldata

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.perpustakaan_app.utils.konversiTanggalServerKeLokal
import kotlinx.serialization.Serializable

@Serializable
data class DataPeminjamanBuku(
    val id_peminjaman: Int,
    val id_anggota: Int,
    val id_buku: Int,
    val tanggal_pinjam: String?,
    val tanggal_jatuh_tempo: String?,
    val tanggal_kembali: String?,
    val status: String,
    val nama: String? = null,
    val judul: String? = null
)

data class UIStatePeminjamanBuku(
    val detailPeminjamanBuku: DetailPeminjamanBuku = DetailPeminjamanBuku(),
    val isEntryValid: Boolean = false
)

data class DetailPeminjamanBuku(
    val id_peminjaman: Int = 0,
    val id_anggota: Int = 0,
    val id_buku: Int = 0,
    val tanggal_pinjam: String? = "",
    val tanggal_jatuh_tempo: String? = "",
    val tanggal_kembali: String? = "",
    val status: String = "",
    val nama: String = "",
    val judul: String = ""
)

fun DetailPeminjamanBuku.toDataPeminjamanBuku() : DataPeminjamanBuku = DataPeminjamanBuku (
    id_peminjaman = id_peminjaman,
    id_anggota = id_anggota,
    id_buku = id_buku,
    tanggal_pinjam = tanggal_pinjam,
    tanggal_jatuh_tempo = tanggal_jatuh_tempo,
    tanggal_kembali = tanggal_kembali?.ifBlank { null },
    status = status,
    nama = nama,
    judul = judul
)

@RequiresApi(Build.VERSION_CODES.O)
fun DataPeminjamanBuku.toUiStatePeminjamanBuku(isEntryValid: Boolean = false) : UIStatePeminjamanBuku =
    UIStatePeminjamanBuku (
        detailPeminjamanBuku = this.toDetailPeminjamanBuku(),
        isEntryValid = isEntryValid
)

@RequiresApi(Build.VERSION_CODES.O)
fun DataPeminjamanBuku.toDetailPeminjamanBuku() : DetailPeminjamanBuku = DetailPeminjamanBuku (
    id_peminjaman = id_peminjaman,
    id_anggota = id_anggota,
    id_buku = id_buku,
    tanggal_pinjam = konversiTanggalServerKeLokal(tanggal_pinjam),
    tanggal_jatuh_tempo = konversiTanggalServerKeLokal(tanggal_jatuh_tempo),
    tanggal_kembali = konversiTanggalServerKeLokal( tanggal_kembali ?: ""),
    status = status,
    nama = nama ?: "",
    judul = judul ?: ""
)

@Serializable
data class DataUpdatePeminjamanBuku(
    val id_anggota: Int,
    val id_buku: Int,
    val tanggal_pinjam: String?,
    val tanggal_jatuh_tempo: String?
)

fun DetailPeminjamanBuku.toDataUpdatePeminjamanBuku(): DataUpdatePeminjamanBuku = DataUpdatePeminjamanBuku(
    id_anggota = id_anggota,
    id_buku = id_buku,
    tanggal_pinjam = tanggal_pinjam,
    tanggal_jatuh_tempo = tanggal_jatuh_tempo
)