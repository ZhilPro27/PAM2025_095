package com.example.perpustakaan_app.viewmodel.peminjaman_buku

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataAnggota
import com.example.perpustakaan_app.modeldata.DataBuku
import com.example.perpustakaan_app.modeldata.DetailPeminjamanBuku
import com.example.perpustakaan_app.modeldata.UIStatePeminjamanBuku
import com.example.perpustakaan_app.modeldata.toDataUpdatePeminjamanBuku
import com.example.perpustakaan_app.modeldata.toUiStatePeminjamanBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataAnggota
import com.example.perpustakaan_app.repostitori.RepositoryDataBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiEditPeminjamanBuku
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditPeminjamanBukuViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryPeminjaman: RepositoryDataPeminjamanBuku,
    private val repositoryAnggota: RepositoryDataAnggota,
    private val repositoryBuku: RepositoryDataBuku
) : ViewModel() {

    // Mengambil ID dari argument navigasi
    private val idPeminjaman: Int = checkNotNull(savedStateHandle[DestinasiEditPeminjamanBuku.idPeminjamanBuku])

    var uiState by mutableStateOf(UIStatePeminjamanBuku())
        private set

    // State untuk list dropdown
    var listBuku by mutableStateOf<List<DataBuku>>(emptyList())
        private set
    var listAnggota by mutableStateOf<List<DataAnggota>>(emptyList())
        private set

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // 1. Load data referensi untuk Dropdown
                listBuku = repositoryBuku.getBuku()
                listAnggota = repositoryAnggota.getAnggota()

                // 2. Load data peminjaman yang akan diedit
                val data = repositoryPeminjaman.getPeminjamanBukuById(idPeminjaman)
                // Konversi data API ke UI State
                uiState = data.toUiStatePeminjamanBuku(isEntryValid = true)

            } catch (e: Exception) {
                _pesanChannel.send("Gagal memuat data: ${e.message}")
            }
        }
    }

    // Update state saat form diketik/berubah
    fun updateUiState(detail: DetailPeminjamanBuku) {
        uiState = UIStatePeminjamanBuku(
            detailPeminjamanBuku = detail,
            isEntryValid = validateInput(detail)
        )
    }

    // Saat Buku dipilih dari Dropdown
    fun onBukuSelected(buku: DataBuku) {
        val currentDetail = uiState.detailPeminjamanBuku
        // Update ID dan Judul (agar tampilan dropdown sesuai)
        updateUiState(currentDetail.copy(id_buku = buku.id_buku, judul = buku.judul))
    }

    // Saat Anggota dipilih dari Dropdown
    fun onAnggotaSelected(anggota: DataAnggota) {
        val currentDetail = uiState.detailPeminjamanBuku
        // Update ID dan Nama (agar tampilan dropdown sesuai)
        updateUiState(currentDetail.copy(id_anggota = anggota.id_anggota, nama = anggota.nama))
    }

    suspend fun updatePeminjaman(context: Context): Boolean {
        if (!uiState.isEntryValid) {
            _pesanChannel.send("Data tidak valid. Pastikan semua kolom terisi.")
            return false
        }
        return try {
            repositoryPeminjaman.putPeminjamanBuku(
                idPeminjaman,
                uiState.detailPeminjamanBuku.toDataUpdatePeminjamanBuku()
            )
            _pesanChannel.send("Berhasil memperbarui data peminjaman")
            true
        } catch (e: Exception) {
            _pesanChannel.send("Gagal update: ${e.message}")
            false
        }
    }

    private fun validateInput(detail: DetailPeminjamanBuku): Boolean {
        return with(detail) {
            id_anggota != 0 && id_buku != 0 && tanggal_pinjam?.isNotBlank() == true && tanggal_jatuh_tempo?.isNotBlank() == true
        }
    }
}