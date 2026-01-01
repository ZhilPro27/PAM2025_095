package com.example.perpustakaan_app.viewmodel.buku

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DetailBuku
import com.example.perpustakaan_app.modeldata.UIStateBuku
import com.example.perpustakaan_app.modeldata.toDataBuku
import com.example.perpustakaan_app.modeldata.toUiStateBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataBuku
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiEditBuku
import com.example.perpustakaan_app.utils.uriToFile
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EditBukuViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataBuku: RepositoryDataBuku
) : ViewModel() {
    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    // Mengambil id_buku dari argumen navigasi
    private val idBuku: Int = checkNotNull(savedStateHandle[DestinasiEditBuku.idBukuArg])

    var uiStateBuku by mutableStateOf(UIStateBuku())
        private set

    init {
        // Ambil data buku saat ViewModel dibuat
        ambilBuku()
    }

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onImageSelected(uri: Uri) {
        selectedImageUri = uri
    }

    private fun ambilBuku() {
        viewModelScope.launch {
            try {
                val dataBuku = repositoryDataBuku.getBukuById(idBuku)
                uiStateBuku = dataBuku.toUiStateBuku(isEntryValid = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUiState(detailBuku: DetailBuku) {
        uiStateBuku = UIStateBuku(
            detailBuku = detailBuku,
            isEntryValid = validasiInput(detailBuku)
        )
    }

    suspend fun updateBuku(context: Context): Boolean {
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
            repositoryDataBuku.putBuku(
                idBuku,
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
            judul.isNotBlank() && penulis.isNotBlank() && isbn.isNotBlank()
        }
    }
}