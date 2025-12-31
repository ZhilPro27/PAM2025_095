package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiAnggota
import com.example.perpustakaan_app.modeldata.DataAnggota

interface RepositoryDataAnggota {
    suspend fun getAnggota(): List<DataAnggota>
    suspend fun getAnggotaByNama(keyword: String): List<DataAnggota>
    suspend fun postAnggota(dataAnggota: DataAnggota):retrofit2.Response<Void>
    suspend fun putAnggota(id_anggota: Int, dataAnggota: DataAnggota):retrofit2.Response<Void>
    suspend fun deleteAnggota(id_anggota: Int):retrofit2.Response<Void>
}

class JaringanRepositoryDataAnggota(
    private val serviceApiAnggota: ServiceApiAnggota
): RepositoryDataAnggota {
    override suspend fun getAnggota(): List<DataAnggota> = serviceApiAnggota.getAnggota()
    override suspend fun getAnggotaByNama(keyword: String): List<DataAnggota> = serviceApiAnggota.getAnggotaByNama(keyword)
    override suspend fun postAnggota(dataAnggota: DataAnggota): retrofit2.Response<Void> = serviceApiAnggota.postAnggota(dataAnggota)
    override suspend fun putAnggota(id_anggota: Int, dataAnggota: DataAnggota): retrofit2.Response<Void> = serviceApiAnggota.putAnggota(id_anggota, dataAnggota)
    override suspend fun deleteAnggota(id_anggota: Int): retrofit2.Response<Void> = serviceApiAnggota.deleteAnggota(id_anggota)
}