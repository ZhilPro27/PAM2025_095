package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiCatatanDenda
import com.example.perpustakaan_app.modeldata.DataCatatanDenda

interface RepositoryDataCatatanDenda {
    suspend fun getCatatanDenda(): List<DataCatatanDenda>
    suspend fun getCatatanDendaById(id_denda: Int): DataCatatanDenda
    suspend fun getCatatanDendaByKeyword(keyword: String): List<DataCatatanDenda>
    suspend fun putCatatanDenda(id_denda: Int)
}

class JaringanRepositoryDataCatatanDenda(
    private val serviceApiCatatanDenda: ServiceApiCatatanDenda
): RepositoryDataCatatanDenda {
    override suspend fun getCatatanDenda(): List<DataCatatanDenda> = serviceApiCatatanDenda.getCatatanDenda()
    override suspend fun getCatatanDendaById(id_denda: Int): DataCatatanDenda = serviceApiCatatanDenda.getCatatanDendaById(id_denda)
    override suspend fun getCatatanDendaByKeyword(keyword: String): List<DataCatatanDenda> = serviceApiCatatanDenda.getCatatanDendaByKeyword(keyword)
    override suspend fun putCatatanDenda(id_denda: Int) {
        val requestBody = mapOf(
            "id_denda" to id_denda.toString(),
            "status" to "Lunas",
        )
        serviceApiCatatanDenda.putCatatanDenda(id_denda, requestBody)
    }
}