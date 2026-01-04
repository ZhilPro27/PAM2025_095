package com.example.perpustakaan_app.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.perpustakaan_app.repostitori.AplikasiPerpustakaan
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.anggota.AnggotaViewModel
import com.example.perpustakaan_app.viewmodel.anggota.EditAnggotaViewModel
import com.example.perpustakaan_app.viewmodel.anggota.TambahAnggotaViewModel
import com.example.perpustakaan_app.viewmodel.buku.TambahBukuViewModel
import com.example.perpustakaan_app.viewmodel.buku.BukuViewModel
import com.example.perpustakaan_app.viewmodel.buku.EditBukuViewModel
import com.example.perpustakaan_app.viewmodel.login.LoginViewModel
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.EditPeminjamanViewModel
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.PeminjamanBukuViewModel
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.TambahPeminjamanViewModel

fun CreationExtras.aplikasiPerpustakaan(): AplikasiPerpustakaan =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiPerpustakaan)

object PenyediaViewModel {
    val Factory = viewModelFactory {
        // App
        initializer {
            AppViewModel(
                aplikasiPerpustakaan().containerApp.repositoryAuth.let {
                    aplikasiPerpustakaan().containerApp.userPreferences
                }
            )
        }

        // Login
        initializer {
            LoginViewModel(
                aplikasiPerpustakaan().containerApp.repositoryAuth
            )
        }

        // Buku
        initializer {
            TambahBukuViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataBuku
            )
        }
        initializer {
            BukuViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataBuku
            )
        }
        initializer {
            EditBukuViewModel(
                this.createSavedStateHandle(),
                aplikasiPerpustakaan().containerApp.repositoryDataBuku
            )
        }

        // Anggota
        initializer {
            AnggotaViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataAnggota
            )
        }
        initializer {
            TambahAnggotaViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataAnggota
            )
        }
        initializer{
            EditAnggotaViewModel(
                this.createSavedStateHandle(),
                aplikasiPerpustakaan().containerApp.repositoryDataAnggota
            )
        }

        // Peminjaman Buku
        initializer {
            PeminjamanBukuViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataPeminjamanBuku
            )
        }
        initializer {
            TambahPeminjamanViewModel(
                aplikasiPerpustakaan().containerApp.repositoryDataPeminjamanBuku,
                aplikasiPerpustakaan().containerApp.repositoryDataAnggota,
                aplikasiPerpustakaan().containerApp.repositoryDataBuku
            )
        }
        initializer {
            EditPeminjamanViewModel(
                this.createSavedStateHandle(),
                aplikasiPerpustakaan().containerApp.repositoryDataPeminjamanBuku,
                aplikasiPerpustakaan().containerApp.repositoryDataBuku,
                aplikasiPerpustakaan().containerApp.repositoryDataAnggota
            )
        }

        // Nanti kamu bisa tambahkan initializer untuk ViewModel lain di sini
        // (Contoh: AnggotaViewModel, LoginViewModel, dll)
    }
}
