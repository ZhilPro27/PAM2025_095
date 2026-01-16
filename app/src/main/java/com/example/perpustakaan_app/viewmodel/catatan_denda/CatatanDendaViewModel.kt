package com.example.perpustakaan_app.viewmodel.catatan_denda

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataCatatanDenda
import com.example.perpustakaan_app.repostitori.RepositoryDataCatatanDenda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// State untuk UI (Loading, Success, Error)
sealed interface CatatanDendaUiState {
    data class Success(val catatanDenda: List<DataCatatanDenda>) : CatatanDendaUiState
    object Error : CatatanDendaUiState
    object Loading : CatatanDendaUiState
}

class CatatanDendaViewModel(
    private val repositoryCatatanDenda: RepositoryDataCatatanDenda
) : ViewModel() {

    // Menggunakan StateFlow untuk state list
    var dendaUiState: CatatanDendaUiState by mutableStateOf(CatatanDendaUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // State untuk pesan feedback (Snackbar)
    private val _pesanBar = MutableStateFlow<String?>(null)
    val pesanBar: StateFlow<String?> = _pesanBar.asStateFlow()

    init {
        getCatatanDenda()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getCatatanDenda() {
        viewModelScope.launch {
            dendaUiState = CatatanDendaUiState.Loading
            dendaUiState = try {
                CatatanDendaUiState.Success(repositoryCatatanDenda.getCatatanDenda())
            } catch (e: IOException) {
                CatatanDendaUiState.Error
            } catch (e: Exception) {
                CatatanDendaUiState.Error
            }
        }
    }

    fun searchDenda(keyword: String = searchQuery) {
        viewModelScope.launch {
            dendaUiState = CatatanDendaUiState.Loading
            dendaUiState = try {
                if (keyword.isBlank()) {
                    CatatanDendaUiState.Success(repositoryCatatanDenda.getCatatanDenda())
                } else {
                    CatatanDendaUiState.Success(
                        repositoryCatatanDenda.getCatatanDendaByKeyword(
                            keyword
                        )
                    )
                }
            } catch (e: IOException) {
                CatatanDendaUiState.Error
            } catch (e: Exception) {
                CatatanDendaUiState.Error
            }
        }
    }

    // Fungsi update status
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatusDenda(denda: Int) {
        viewModelScope.launch {
            try {

                // Kirim ke repository
                repositoryCatatanDenda.putCatatanDenda(denda)

                _pesanBar.value = "Status berhasil diperbarui"

                // Refresh data list
                getCatatanDenda()
            } catch (e: Exception) {
                _pesanBar.value = "Gagal update status: ${e.message}"
            }
        }
    }

    // Reset pesan setelah ditampilkan
    fun resetPesanBar() {
        _pesanBar.value = null
    }
}