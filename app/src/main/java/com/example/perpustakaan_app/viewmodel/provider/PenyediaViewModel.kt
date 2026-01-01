package com.example.perpustakaan_app.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.perpustakaan_app.repostitori.AplikasiPerpustakaan
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.buku.TambahBukuViewModel
import com.example.perpustakaan_app.viewmodel.buku.BukuViewModel
import com.example.perpustakaan_app.viewmodel.buku.EditBukuViewModel
import com.example.perpustakaan_app.viewmodel.login.LoginViewModel

// Fungsi ekstensi untuk mengambil instance Aplikasi dengan mudah
fun CreationExtras.aplikasiPerpustakaan(): AplikasiPerpustakaan =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiPerpustakaan)

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            // Karena AppViewModel hanya butuh UserPreferences, kita ambil dari container
            AppViewModel(
                aplikasiPerpustakaan().containerApp.repositoryAuth.let {
                    // Trik: Karena RepositoryAuth punya akses ke UserPreferences,
                    // tapi lebih bersih jika UserPreferences diekspos di ContainerApp.
                    // Jika ContainerApp kamu belum mengekspos userPreferences secara public,
                    // Kita perlu update ContainerApp sedikit atau ambil via context aplikasi.
                    // CARA AMAN: Update ContainerApp dulu (Lihat Langkah 2.5)
                    aplikasiPerpustakaan().containerApp.userPreferences
                }
            )
        }
        initializer {
            LoginViewModel(
                aplikasiPerpustakaan().containerApp.repositoryAuth
            )
        }
        // Mendaftarkan TambahBukuViewModel
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

        // Nanti kamu bisa tambahkan initializer untuk ViewModel lain di sini
        // (Contoh: AnggotaViewModel, LoginViewModel, dll)
    }
}
