package com.example.perpustakaan_app.repostitori

import com.example.perpustakaan_app.apiservices.ServiceApiGoogleBooks
import com.example.perpustakaan_app.modeldata.GoogleBooksResponse

interface RepositoryGoogleBooks {
    suspend fun getBukuByIsbn(isbn: String): GoogleBooksResponse
}

class NetworkRepositoryGoogleBooks(
    private val serviceApiGoogleBooks: ServiceApiGoogleBooks
) : RepositoryGoogleBooks {
    override suspend fun getBukuByIsbn(isbn: String): GoogleBooksResponse {
        return serviceApiGoogleBooks.getVolumeByIsbn("isbn:$isbn")
    }
}
