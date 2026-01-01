package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiBuku
import com.example.perpustakaan_app.modeldata.DataBuku
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface RepositoryDataBuku{
    suspend fun getBuku(): List<DataBuku>
    suspend fun getBukuById(id_buku: Int): DataBuku
    suspend fun getBukuByKeyword(keyword: String): List<DataBuku>
    suspend fun postBuku(dataBuku: DataBuku, imageFile: File?):retrofit2.Response<Void>
    suspend fun putBuku(id_buku: Int, dataBuku: DataBuku, imageFile: File?):retrofit2.Response<Void>
    suspend fun deleteBuku(id_buku: Int):retrofit2.Response<Void>
}

class JaringanRepositoryDataBuku(
    private val serviceApiBuku: ServiceApiBuku
): RepositoryDataBuku{
    override suspend fun getBuku(): List<DataBuku> = serviceApiBuku.getBuku()
    override suspend fun getBukuById(id_buku: Int): DataBuku = serviceApiBuku.getBukuById(id_buku)
    override suspend fun getBukuByKeyword(keyword: String): List<DataBuku> = serviceApiBuku.getBukuByKeyword(keyword)
    override suspend fun postBuku(dataBuku: DataBuku, imageFile: File?): retrofit2.Response<Void> {

        // 2. Siapkan Data Teks
        val judul = dataBuku.judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val penulis = dataBuku.penulis.toRequestBody("text/plain".toMediaTypeOrNull())
        val penerbit = dataBuku.penerbit.toRequestBody("text/plain".toMediaTypeOrNull())
        val tahun = dataBuku.tahun_terbit.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val stok = dataBuku.stok.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val isbn = dataBuku.isbn.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gambar", imageFile.name, requestFile)
        } else {
            null // Kirim null jika tidak ada gambar
        }
        val nomor_panggil = dataBuku.nomor_panggil.toRequestBody("text/plain".toMediaTypeOrNull())
        val deskripsi = dataBuku.deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        // 3. Kirim
        return serviceApiBuku.postBuku(judul, penulis, penerbit, tahun, stok, isbn, multipartBody, nomor_panggil, deskripsi)
    }
    override suspend fun putBuku(id_buku: Int, dataBuku: DataBuku, imageFile: File?): retrofit2.Response<Void> {
        val id_buku = dataBuku.id_buku
        // 2. Siapkan Data Teks
        val judul = dataBuku.judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val penulis = dataBuku.penulis.toRequestBody("text/plain".toMediaTypeOrNull())
        val penerbit = dataBuku.penerbit.toRequestBody("text/plain".toMediaTypeOrNull())
        val tahun = dataBuku.tahun_terbit.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val stok = dataBuku.stok.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val isbn = dataBuku.isbn.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gambar", imageFile.name, requestFile)
        } else {
            null // Kirim null jika tidak ada gambar
        }
        val nomor_panggil = dataBuku.nomor_panggil.toRequestBody("text/plain".toMediaTypeOrNull())
        val deskripsi = dataBuku.deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        // 3. Kirim
        return serviceApiBuku.putBuku(id_buku, judul, penulis, penerbit, tahun, stok, isbn, multipartBody, nomor_panggil, deskripsi)
    }
    override suspend fun deleteBuku(id_buku: Int): retrofit2.Response<Void> = serviceApiBuku.deleteBuku(id_buku)
}