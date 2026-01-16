package com.example.perpustakaan_app.viewmodel.profil

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DetailProfil
import com.example.perpustakaan_app.modeldata.UIStateProfil
import com.example.perpustakaan_app.modeldata.toUiStateProfil
import com.example.perpustakaan_app.repostitori.RepositoryDataProfil
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiEditProfil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditProfilViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoryDataProfil: RepositoryDataProfil
) : ViewModel() {
    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    private val idPustakawan: Int = checkNotNull(savedStateHandle[DestinasiEditProfil.idPustakawanArg])

    var uiStateProfil: UIStateProfil by mutableStateOf(UIStateProfil())
        private set

    init {
        getProfil()
    }

    private fun getProfil(){
        viewModelScope.launch {
            try {
                val dataProfil = repositoryDataProfil.getProfilById(idPustakawan)
                uiStateProfil = dataProfil.toUiStateProfil(isEntryValid = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUiState(detailProfil: DetailProfil) {
        uiStateProfil = UIStateProfil(
            detailProfil = detailProfil,
            isEntryValid = validasiInput(detailProfil)
        )
    }

    suspend fun updateProfil(context: Context): Boolean {
        if (!uiStateProfil.isEntryValid) {
            _pesanChannel.send("Gagal: Data tidak lengkap")
            return false
        }
        return try{
            repositoryDataProfil.putProfil(
                idPustakawan,
                uiStateProfil.detailProfil
            )
            _pesanChannel.send("Berhasil memperbarui data profil")
            true
        } catch (e: Exception) {
            _pesanChannel.send("Gagal update: ${e.message}")
            false
        }
    }

    private fun validasiInput(uiState: DetailProfil = uiStateProfil.detailProfil): Boolean {
        return with(uiState) {
            email.isNotBlank()
        }
    }
}