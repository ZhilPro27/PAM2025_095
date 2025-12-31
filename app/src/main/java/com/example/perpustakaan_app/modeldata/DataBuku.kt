package com.example.perpustakaan_app.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DataBuku(
    val id_buku: Int,
    val judul: String,
    val penulis: String,
    val penerbit: String,
    val tahun_terbit: String,
    val isbn: String,
    val nomor_panggil: String,
    val stok: Int,
    val gambar: String,
    val deskripsi: String
)

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
    val gambar: String = "",
    val deskripsi: String = ""
)

fun DetailBuku.toDataBuku() : DataBuku = DataBuku(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    penerbit = penerbit,
    tahun_terbit = tahun_terbit,
    isbn = isbn,
    nomor_panggil = nomor_panggil,
    stok = stok,
    gambar = gambar,
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
    tahun_terbit = tahun_terbit,
    isbn = isbn,
    nomor_panggil = nomor_panggil,
    stok = stok,
    gambar = gambar,
    deskripsi = deskripsi
)