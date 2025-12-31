package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiPeminjamanBuku
import com.example.perpustakaan_app.modeldata.DataPeminjamanBuku

interface RepositoryDataPeminjamanBuku {
    suspend fun getPeminjamanBuku(): List<DataPeminjamanBuku>
    suspend fun getPeminjamanBukuByKeyword(keyword: String): List<DataPeminjamanBuku>
    suspend fun postPeminjamanBuku(dataPeminjamanBuku: DataPeminjamanBuku):retrofit2.Response<Void>
    suspend fun putPeminjamanBuku(id_peminjaman: Int, dataPeminjamanBuku: DataPeminjamanBuku):retrofit2.Response<Void>
    suspend fun deletePeminjamanBuku(id_peminjaman: Int):retrofit2.Response<Void>
}

class JaringanRepositoryDataPeminjamanBuku(
    private val serviceApiPeminjamanBuku: ServiceApiPeminjamanBuku
): RepositoryDataPeminjamanBuku {
    override suspend fun getPeminjamanBuku(): List<DataPeminjamanBuku> = serviceApiPeminjamanBuku.getPeminjamanBuku()
    override suspend fun getPeminjamanBukuByKeyword(keyword: String): List<DataPeminjamanBuku> = serviceApiPeminjamanBuku.getPeminjamanBukuByKeyword(keyword)
    override suspend fun postPeminjamanBuku(dataPeminjamanBuku: DataPeminjamanBuku): retrofit2.Response<Void> = serviceApiPeminjamanBuku.postPeminjamanBuku(dataPeminjamanBuku)
    override suspend fun putPeminjamanBuku(id_peminjaman: Int, dataPeminjamanBuku: DataPeminjamanBuku): retrofit2.Response<Void> = serviceApiPeminjamanBuku.putPeminjamanBuku(id_peminjaman, dataPeminjamanBuku)
    override suspend fun deletePeminjamanBuku(id_peminjaman: Int): retrofit2.Response<Void> = serviceApiPeminjamanBuku.deletePeminjamanBuku(id_peminjaman)
}