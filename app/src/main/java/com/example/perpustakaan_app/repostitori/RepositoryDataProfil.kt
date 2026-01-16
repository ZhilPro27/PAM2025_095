package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiProfil
import com.example.perpustakaan_app.modeldata.DataProfil
import com.example.perpustakaan_app.modeldata.DetailProfil
import com.example.perpustakaan_app.modeldata.toDataProfil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

interface RepositoryDataProfil {
    suspend fun getProfilbyEmail(email: String): DataProfil
    suspend fun getProfilById(id_pustakawan: Int): DataProfil
    suspend fun putProfil(id_pustakawan: Int, dataProfil: DetailProfil): retrofit2.Response<Void>
}

class JaringanRepositoryDataProfil(
    private val serviceApiProfil: ServiceApiProfil
): RepositoryDataProfil {
    override suspend fun getProfilbyEmail(email: String): DataProfil = serviceApiProfil.getProfilbyEmail(email)
    override suspend fun getProfilById(id_pustakawan: Int): DataProfil = serviceApiProfil.getProfilById(id_pustakawan)
    override suspend fun putProfil(id_pustakawan: Int, dataProfil: DetailProfil): retrofit2.Response<Void> {
        // PERBAIKAN: Konversi ke DataProfil untuk dikirim sebagai JSON
        // Jika password kosong (user tidak mau ubah), kirim sebagai null agar backend mengabaikannya
        val requestBody = if (dataProfil.password.isBlank()) {
            dataProfil.toDataProfil().copy(password = null)
        } else {
            dataProfil.toDataProfil()
        }

        return serviceApiProfil.putProfil(id_pustakawan, requestBody)
    }
}