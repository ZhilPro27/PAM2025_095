package com.example.perpustakaan_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.repostitori.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    // Mengubah Flow<String?> menjadi StateFlow<Boolean?>
    // null  = Loading (sedang ngecek)
    // true  = Ada Token (Login)
    // false = Tidak Ada Token (Belum Login)
    val isUserLoggedIn: StateFlow<Boolean?> = userPreferences.authToken
        .map { token ->
            token != null && token.isNotBlank()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}