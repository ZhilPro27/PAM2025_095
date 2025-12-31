package com.example.perpustakaan_app.repostitori

import android.app.Application
import android.content.Context
import com.example.perpustakaan_app.apiservices.ServiceApiAuth
import com.example.perpustakaan_app.apiservices.ServiceApiAnggota
import com.example.perpustakaan_app.apiservices.ServiceApiBuku
import com.example.perpustakaan_app.apiservices.ServiceApiCatatanDenda
import com.example.perpustakaan_app.apiservices.ServiceApiPeminjamanBuku
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface ContainerApp{
    val repositoryAuth: RepositoryAuth
    val repositoryDataBuku: RepositoryDataBuku
    val repositoryDataAnggota: RepositoryDataAnggota
    val repositoryDataPeminjamanBuku: RepositoryDataPeminjamanBuku
    val repositoryDataCatatanDenda: RepositoryDataCatatanDenda
}

class DefaultContainerApp(private val context: Context) : ContainerApp {
    private val baseurl = "http://43.159.44.188/api/"
    private val userPreferences = UserPreferences(context)

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking {
            userPreferences.authToken.first()
        }

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    val klien = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(klien)
        .build()

    private val retrofitServiceAuth: ServiceApiAuth by lazy {
        retrofit.create(ServiceApiAuth::class.java)
    }

    override val repositoryAuth: RepositoryAuth by lazy {
        RepositoryAuth(retrofitServiceAuth, userPreferences)
    }
    private val retrofitService: ServiceApiBuku by lazy {
        retrofit.create(ServiceApiBuku::class.java)
    }

    override val repositoryDataBuku: RepositoryDataBuku by lazy {
        JaringanRepositoryDataBuku(retrofitService)
    }

    private val retrofitServiceAnggota: ServiceApiAnggota by lazy {
        retrofit.create(ServiceApiAnggota::class.java)
    }

    override val repositoryDataAnggota: RepositoryDataAnggota by lazy {
        JaringanRepositoryDataAnggota(retrofitServiceAnggota)
    }

    private val retrofitServicePeminjamanBuku: ServiceApiPeminjamanBuku by lazy {
        retrofit.create(ServiceApiPeminjamanBuku::class.java)
    }

    override val repositoryDataPeminjamanBuku: RepositoryDataPeminjamanBuku by lazy {
        JaringanRepositoryDataPeminjamanBuku(retrofitServicePeminjamanBuku)
    }

    private val retrofitServiceCatatanDenda: ServiceApiCatatanDenda by lazy {
        retrofit.create(ServiceApiCatatanDenda::class.java)
    }

    override val repositoryDataCatatanDenda: RepositoryDataCatatanDenda by lazy {
        JaringanRepositoryDataCatatanDenda(retrofitServiceCatatanDenda)
    }
}

class AplikasiPerpustakaan: Application() {
    lateinit var containerApp: ContainerApp
    override fun onCreate() {
        super.onCreate()
        containerApp = DefaultContainerApp(this)
    }
}

