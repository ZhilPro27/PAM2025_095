package com.example.perpustakaan_app.viewmodel.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.repostitori.RepositoryAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(private val repositoryAuth: RepositoryAuth) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateUiState(email: String? = null, password: String? = null) {
        uiState = uiState.copy(
            email = email ?: uiState.email,
            password = password ?: uiState.password,
            errorMessage = null
        )
    }

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    fun login() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val result = repositoryAuth.login(uiState.email, uiState.password)

                if (result.isSuccess) {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                    _pesanChannel.send("Berhasil Login! Selamat Datang.")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Login Gagal"
                    uiState = uiState.copy(isLoading = false, errorMessage = errorMsg)
                    _pesanChannel.send("Gagal: $errorMsg")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Koneksi Error")
                _pesanChannel.send("Gagal: Periksa koneksi internet anda")
            }
        }
    }
}