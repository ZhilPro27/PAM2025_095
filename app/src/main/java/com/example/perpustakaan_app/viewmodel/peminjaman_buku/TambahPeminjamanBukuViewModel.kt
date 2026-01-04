package com.example.perpustakaan_app.viewmodel.peminjaman_buku

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataPeminjamanBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataPeminjamanBuku
import com.example.perpustakaan_app.repostitori.RepositoryDataAnggota
import com.example.perpustakaan_app.repostitori.RepositoryDataBuku
import com.example.perpustakaan_app.modeldata.DataAnggota
import com.example.perpustakaan_app.modeldata.DataBuku
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TambahPeminjamanViewModel(
    private val repository: RepositoryDataPeminjamanBuku,
    private val repositoryAnggota: RepositoryDataAnggota,
    private val repositoryBuku: RepositoryDataBuku
) : ViewModel() {
    var uiState by mutableStateOf(InsertPeminjamanUiState())
        private set
    var listBuku by mutableStateOf<List<DataBuku>>(emptyList())
        private set
    var listAnggota by mutableStateOf<List<DataAnggota>>(emptyList())
        private set

    var selectedBukuJudul by mutableStateOf("")
        private set
    var selectedAnggotaNama by mutableStateOf("")
        private set

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    init {
        loadDataReferensi()
    }

    private fun loadDataReferensi() {
        viewModelScope.launch {
            try {
                listBuku = repositoryBuku.getBuku()
                listAnggota = repositoryAnggota.getAnggota()
            } catch (e: Exception) {
                _pesanChannel.send("Gagal memuat data referensi: ${e.message}")
            }
        }
    }

    fun updateUiState(detail: DetailPeminjaman) {
        uiState = InsertPeminjamanUiState(detailPeminjaman = detail)
    }

    fun onBukuSelected(buku: DataBuku) {
        selectedBukuJudul = buku.judul // Tampilkan Judul
        updateUiState(uiState.detailPeminjaman.copy(id_buku = buku.id_buku.toString())) // Simpan ID
    }

    fun onAnggotaSelected(anggota: DataAnggota) {
        selectedAnggotaNama = anggota.nama // Tampilkan Nama
        updateUiState(uiState.detailPeminjaman.copy(id_anggota = anggota.id_anggota.toString())) // Simpan ID
    }

    suspend fun savePeminjaman(context : Context): Boolean {
        if (validateInput()) {
            repository.postPeminjamanBuku(uiState.detailPeminjaman.toDataPeminjaman())
            _pesanChannel.send("Berhasil menambahkan peminjaman")
            return true
        } else {
            _pesanChannel.send("Gagal menambahkan peminjaman")
            return false
        }
    }

    private fun validateInput(): Boolean {
        return with(uiState.detailPeminjaman) {
            id_anggota.isNotBlank() && id_anggota != "0" &&
            id_buku.isNotBlank() && id_buku != "0" &&
            tanggal_pinjam.isNotBlank() &&
            tanggal_jatuh_tempo.isNotBlank()
        }
    }


}

data class InsertPeminjamanUiState(
    val detailPeminjaman: DetailPeminjaman = DetailPeminjaman()
)

data class DetailPeminjaman(
    val id_anggota: String = "",
    val id_buku: String = "",
    val tanggal_pinjam: String = "",
    val tanggal_jatuh_tempo: String = "",
    val status: String = "Dipinjam"
)

fun DetailPeminjaman.toDataPeminjaman(): DataPeminjamanBuku = DataPeminjamanBuku(
    id_peminjaman = 0, // Auto Generated oleh Backend
    id_anggota = id_anggota.toIntOrNull() ?: 0,
    id_buku = id_buku.toIntOrNull() ?: 0,
    tanggal_pinjam = tanggal_pinjam,
    tanggal_jatuh_tempo = tanggal_jatuh_tempo,
    tanggal_kembali = null,
    status = status
)