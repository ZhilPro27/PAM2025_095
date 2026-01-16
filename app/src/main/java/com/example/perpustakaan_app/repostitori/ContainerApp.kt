package com.example.perpustakaan_app.repostitori

import android.app.Application
import android.content.Context
import com.example.perpustakaan_app.apiservices.ServiceApiAuth
import com.example.perpustakaan_app.apiservices.ServiceApiAnggota
import com.example.perpustakaan_app.apiservices.ServiceApiBuku
import com.example.perpustakaan_app.apiservices.ServiceApiCatatanDenda
import com.example.perpustakaan_app.apiservices.ServiceApiPeminjamanBuku
import com.example.perpustakaan_app.apiservices.ServiceApiProfil
import com.example.perpustakaan_app.apiservices.ServiceApiGoogleBooks
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
    val userPreferences: UserPreferences
    val repositoryAuth: RepositoryAuth
    val repositoryDataBuku: RepositoryDataBuku
    val repositoryDataAnggota: RepositoryDataAnggota
    val repositoryDataPeminjamanBuku: RepositoryDataPeminjamanBuku
    val repositoryDataCatatanDenda: RepositoryDataCatatanDenda
    val repositoryDataProfil: RepositoryDataProfil
    val repositoryGoogleBooks: RepositoryGoogleBooks
}

class DefaultContainerApp(private val context: Context) : ContainerApp {
    private val baseurl = "https://perpustakaan-pam-app.zhilalkrisna.my.id/api/"
    override val userPreferences = UserPreferences(context)

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

    val authResponseInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        // Cek jika server mengembalikan error 401 (Unauthorized)
        if (response.code == 401) {
            // Hapus session secara sinkron agar AppViewModel langsung bereaksi
            runBlocking {
                userPreferences.clearSession()
            }
        }
        response
    }

    val klien = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .addInterceptor(authResponseInterceptor)
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

    private val retrofitServiceProfil: ServiceApiProfil by lazy {
        retrofit.create(ServiceApiProfil::class.java)
    }

    override val repositoryDataProfil: RepositoryDataProfil by lazy {
        JaringanRepositoryDataProfil(retrofitServiceProfil)
    }

    private val retrofitGoogleBooks: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(OkHttpClient.Builder().addInterceptor(logging).build())
        .build()

    private val retrofitServiceGoogleBooks: ServiceApiGoogleBooks by lazy {
        retrofitGoogleBooks.create(ServiceApiGoogleBooks::class.java)
    }

    override val repositoryGoogleBooks: RepositoryGoogleBooks by lazy {
        NetworkRepositoryGoogleBooks(retrofitServiceGoogleBooks)
    }
}

class AplikasiPerpustakaan: Application() {
    lateinit var containerApp: ContainerApp
    override fun onCreate() {
        super.onCreate()
        containerApp = DefaultContainerApp(this)
    }
}

