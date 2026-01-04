package com.example.perpustakaan_app.viewmodel.peminjaman_buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataPeminjamanBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataPeminjamanBuku
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface PeminjamanBukuUiState {
    data class Success(val peminjaman: List<DataPeminjamanBuku>) : PeminjamanBukuUiState
    object Error : PeminjamanBukuUiState
    object Loading : PeminjamanBukuUiState
}

class PeminjamanBukuViewModel (private val repositoryDataPeminjamanBuku: RepositoryDataPeminjamanBuku) : ViewModel(){
    var peminjamanBukuUiState: PeminjamanBukuUiState by mutableStateOf(PeminjamanBukuUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    init {
        getPeminjaman()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun searchPeminjaman(keyword: String = searchQuery) {
        viewModelScope.launch {
            peminjamanBukuUiState = PeminjamanBukuUiState.Loading
            peminjamanBukuUiState = try {
                if (keyword.isBlank()) {
                    PeminjamanBukuUiState.Success(repositoryDataPeminjamanBuku.getPeminjamanBuku())
                } else {
                    PeminjamanBukuUiState.Success(repositoryDataPeminjamanBuku.getPeminjamanBukuByKeyword(keyword))
                }
            } catch (e: IOException) {
                PeminjamanBukuUiState.Error
            } catch (e: Exception) {
                PeminjamanBukuUiState.Error
            }
        }
    }

    fun getPeminjaman() {
        viewModelScope.launch {
            peminjamanBukuUiState = PeminjamanBukuUiState.Loading
            peminjamanBukuUiState = try {
                PeminjamanBukuUiState.Success(repositoryDataPeminjamanBuku.getPeminjamanBuku())
            } catch (e: IOException) {
                PeminjamanBukuUiState.Error
            } catch (e: Exception) {
                PeminjamanBukuUiState.Error
            }
        }
    }

    fun deletePeminjaman(id: Int) {
        viewModelScope.launch {
            try {
                repositoryDataPeminjamanBuku.deletePeminjamanBuku(id)
                getPeminjaman()
                _pesanChannel.send("Berhasil menghapus peminjaman")
            } catch (e: Exception) {
                _pesanChannel.send("Gagal menghapus peminjaman: ${e.message}")
            }
        }
    }

    fun returnBook(id: Int) {
        viewModelScope.launch {
            try {
                repositoryDataPeminjamanBuku.putStatusPeminjamanBuku(id)
                getPeminjaman()
                _pesanChannel.send("Berhasil mengembalikan buku")
            } catch (e: Exception) {
                _pesanChannel.send("Gagal mengembalikan buku: ${e.message}")
            }
        }
    }
}