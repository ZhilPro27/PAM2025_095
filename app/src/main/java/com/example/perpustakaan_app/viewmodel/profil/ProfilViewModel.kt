package com.example.perpustakaan_app.viewmodel.profil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataProfil
import com.example.perpustakaan_app.repostitori.RepositoryDataProfil
import com.example.perpustakaan_app.repostitori.UserPreferences
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiProfil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okio.IOException

sealed interface ProfilUiState {
    data class Success(val profil: DataProfil): ProfilUiState
    object Error: ProfilUiState
    object Loading: ProfilUiState
}


class ProfilViewModel(
    private val repositoryDataProfil: RepositoryDataProfil,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    var profilUiState: ProfilUiState by mutableStateOf(ProfilUiState.Loading)
        private set

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()
    var email by mutableStateOf("")
    var isLoading by mutableStateOf(true)

    init {
        viewModelScope.launch {
            userPreferences.userEmail.collect { savedEmail ->
                email = savedEmail
                isLoading = false
            }
        }
    }

    fun getProfil() {
        viewModelScope.launch {
            profilUiState = ProfilUiState.Loading
            try {
                // Ambil email terbaru dari DataStore secara sinkron (suspend)
                val currentEmail = userPreferences.userEmail.first()

                if (currentEmail.isNotEmpty() && currentEmail != "Email tidak ditemukan") {
                    val result = repositoryDataProfil.getProfilbyEmail(currentEmail)
                    profilUiState = ProfilUiState.Success(result)
                } else {
                    profilUiState = ProfilUiState.Error
                }
            } catch (e: Exception) {
                profilUiState = ProfilUiState.Error
            }
        }
    }
}