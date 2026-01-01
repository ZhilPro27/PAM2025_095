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
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import com.example.perpustakaan_app.utils.uriToFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File

class TambahBukuViewModel(private val repositoryDataBuku: RepositoryDataBuku) : ViewModel() {

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