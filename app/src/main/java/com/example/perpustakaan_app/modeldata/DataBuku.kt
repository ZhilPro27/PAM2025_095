package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.SerialName // Disarankan pakai ini untuk keamanan
import kotlinx.serialization.Serializable

@Serializable
data class DataBuku(
    val id_buku: Int,
    val judul: String,
    val penulis: String,
    val penerbit: String,

    // UBAH 1: Tipe data disesuaikan dengan JSON (Angka -> Int)
    val tahun_terbit: Int,

    val isbn: String,
    val nomor_panggil: String,
    val stok: Int,

    // UBAH 2: Tambahkan tanda tanya (?) karena JSON mengirim null
    val gambar: String?,

    val deskripsi: String
){
    override fun toString(): String = judul
}

data class UIStateBuku(
    val detailBuku: DetailBuku = DetailBuku(),
    val isEntryValid: Boolean = false
)

data class DetailBuku(
    val id_buku: Int = 0,
    val judul: String = "",
    val penulis: String = "",
    val penerbit: String = "",
    val tahun_terbit: String = "",
    val isbn: String = "",
    val nomor_panggil: String = "",
    val stok: Int = 0,
    val gambar: String = "", // Di UI kita biarkan String (kosong jika null)
    val deskripsi: String = ""
)

fun DetailBuku.toDataBuku() : DataBuku = DataBuku(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    penerbit = penerbit,
    // Konversi String UI ke Int untuk Server
    tahun_terbit = tahun_terbit.toIntOrNull() ?: 0,
    isbn = isbn,
    nomor_panggil = nomor_panggil,
    stok = stok,
    // Jika kosong, kirim null ke server (opsional) atau kirim string kosong
    gambar = if (gambar.isBlank()) null else gambar,
    deskripsi = deskripsi
)

fun DataBuku.toUiStateBuku(isEntryValid: Boolean = false) : UIStateBuku = UIStateBuku(
    detailBuku = this.toDetailBuku(),
    isEntryValid = isEntryValid
)

fun DataBuku.toDetailBuku() : DetailBuku = DetailBuku(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    penerbit = penerbit,
    // Konversi Int Server ke String UI
    tahun_terbit = tahun_terbit.toString(),
    isbn = isbn,
    nomor_panggil = nomor_panggil,
    stok = stok,
    // Handle null gambar menjadi string kosong agar aman di UI
    gambar = gambar ?: "",
    deskripsi = deskripsi
)