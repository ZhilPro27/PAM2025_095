package com.example.perpustakaan_app.viewmodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DetailBuku
import com.example.perpustakaan_app.modeldata.UIStateBuku
import com.example.perpustakaan_app.modeldata.toDataBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataBuku
import com.example.perpustakaan_app.repostitori.RepositoryGoogleBooks
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import com.example.perpustakaan_app.utils.uriToFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File

class TambahBukuViewModel(
    private val repositoryDataBuku: RepositoryDataBuku,
    private val repositoryGoogleBooks: RepositoryGoogleBooks
) : ViewModel() {

    var uiStateBuku by mutableStateOf(UIStateBuku())
        private set

    fun updateUiState(detailBuku: DetailBuku) {
        uiStateBuku = UIStateBuku(
            detailBuku = detailBuku,
            isEntryValid = validasiInput(detailBuku)
        )
    }

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onImageSelected(uri: Uri) {
        selectedImageUri = uri
    }

    fun getGoogleBook(isbn: String) {
        viewModelScope.launch {
            try {
                // Reset form but keep ISBN
                updateUiState(DetailBuku(isbn = isbn))
                
                _pesanChannel.send("Mencari data buku...")
                val response = repositoryGoogleBooks.getBukuByIsbn(isbn)
                
                if (response.totalItems != null && response.totalItems > 0 && !response.items.isNullOrEmpty()) {
                    val item = response.items.first()
                    val info = item.volumeInfo
                    
                    if (info != null) {
                        val judul = info.title ?: ""
                        val penulis = info.authors?.joinToString(", ") ?: ""
                        val penerbit = info.publisher ?: ""
                        val deskripsi = info.description ?: ""
                        // Ambil tahun saja dari publishedDate (format: "YYYY-MM-DD" atau "YYYY")
                        val tahun = info.publishedDate?.take(4) ?: ""
                        
                        updateUiState(
                            uiStateBuku.detailBuku.copy(
                                isbn = isbn,
                                judul = judul,
                                penulis = penulis,
                                penerbit = penerbit,
                                tahun_terbit = tahun,
                                deskripsi = deskripsi
                            )
                        )
                        _pesanChannel.send("Data buku ditemukan!")
                    }
                } else {
                    _pesanChannel.send("Data buku tidak ditemukan di Google Books")
                }
            } catch (e: Exception) {
                _pesanChannel.send("Gagal mengambil data: ${e.message}")
                // Tetap set ISBN
                updateUiState(uiStateBuku.detailBuku.copy(isbn = isbn))
            }
        }
    }

    suspend fun simpanBuku(context: Context): Boolean {
        if (!uiStateBuku.isEntryValid) {
            _pesanChannel.send("Gagal: Data tidak lengkap")
            return false
        }
        return try {
            // 1. Proses Gambar (URI -> File)
            val file: File? = if (selectedImageUri != null) {
                uriToFile(selectedImageUri!!, context)
            } else {
                null
            }
            repositoryDataBuku.postBuku(
                uiStateBuku.detailBuku.toDataBuku(),
                file
            )

            _pesanChannel.send("Berhasil memperbarui data buku")
            true

        } catch (e: Exception) {
            _pesanChannel.send("Gagal update: ${e.message}")
            false
        }
    }

    private fun validasiInput(uiState: DetailBuku = uiStateBuku.detailBuku): Boolean {
        return with(uiState) {
            judul.isNotBlank() &&
                    penulis.isNotBlank() &&
                    isbn.isNotBlank() &&
                    penerbit.isNotBlank() &&
                    stok != 0 &&
                    (tahun_terbit.toIntOrNull() ?: 0) > 1900
        }
    }
}