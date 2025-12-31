package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiBuku
import com.example.perpustakaan_app.modeldata.DataBuku

interface RepositoryDataBuku{
    suspend fun getBuku(): List<DataBuku>
    suspend fun getBukuByKeyword(keyword: String): List<DataBuku>
    suspend fun postBuku(dataBuku: DataBuku):retrofit2.Response<Void>
    suspend fun putBuku(id_buku: Int, dataBuku: DataBuku):retrofit2.Response<Void>
    suspend fun deleteBuku(id_buku: Int):retrofit2.Response<Void>
}

class JaringanRepositoryDataBuku(
    private val serviceApiBuku: ServiceApiBuku
): RepositoryDataBuku{
    override suspend fun getBuku(): List<DataBuku> = serviceApiBuku.getBuku()
    override suspend fun getBukuByKeyword(keyword: String): List<DataBuku> = serviceApiBuku.getBukuByKeyword(keyword)
    override suspend fun postBuku(dataBuku: DataBuku): retrofit2.Response<Void> = serviceApiBuku.postBuku(dataBuku)
    override suspend fun putBuku(id_buku: Int, dataBuku: DataBuku): retrofit2.Response<Void> = serviceApiBuku.putBuku(id_buku, dataBuku)
    override suspend fun deleteBuku(id_buku: Int): retrofit2.Response<Void> = serviceApiBuku.deleteBuku(id_buku)
}