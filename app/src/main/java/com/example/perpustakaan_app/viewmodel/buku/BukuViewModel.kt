package com.example.perpustakaan_app.viewmodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataBuku
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Status UI: Loading, Sukses (ada data), atau Error
sealed interface BukuUiState {
    data class Success(val buku: List<DataBuku>) : BukuUiState
    object Error : BukuUiState
    object Loading : BukuUiState
}

class BukuViewModel(private val repositoryDataBuku: RepositoryDataBuku) : ViewModel() {

    var bukuUiState: BukuUiState by mutableStateOf(BukuUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun searchBuku(keyword: String = searchQuery) {
        viewModelScope.launch {
            bukuUiState = BukuUiState.Loading
            bukuUiState = try {
                if (keyword.isBlank()) {
                    BukuUiState.Success(repositoryDataBuku.getBuku())
                } else {
                    BukuUiState.Success(repositoryDataBuku.getBukuByKeyword(keyword))
                }
            } catch (e: IOException) {
                BukuUiState.Error
            } catch (e: Exception) {
                BukuUiState.Error
            }
        }
    }

    fun getBuku() {
        viewModelScope.launch {
            bukuUiState = BukuUiState.Loading
            bukuUiState = try {
                BukuUiState.Success(repositoryDataBuku.getBuku())
            } catch (e: IOException) {
                BukuUiState.Error
            } catch (e: Exception) {
                BukuUiState.Error
            }
        }
    }

    fun deleteBuku(id_buku: Int) {
        viewModelScope.launch {
            try {
                repositoryDataBuku.deleteBuku(id_buku)
                getBuku()
                _pesanChannel.send("Berhasil menghapus buku")
            } catch (e: Exception) {
                _pesanChannel.send("Gagal menghapus buku: ${e.message}")
            }
        }
    }
}