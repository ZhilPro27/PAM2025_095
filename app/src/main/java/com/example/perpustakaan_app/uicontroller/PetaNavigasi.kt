package com.example.perpustakaan_app.uicontroller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiAnggota
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiEditAnggota
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiTambahAnggota
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiBuku
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiEditBuku
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiTambahBuku
import com.example.perpustakaan_app.uicontroller.route.catatan_denda.DestinasiCatatanDenda
import com.example.perpustakaan_app.uicontroller.route.login.DestinasiLogin
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiEditPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiTambahPeminjamanBuku
import com.example.perpustakaan_app.view.PerpustakaanBottomAppBar
import com.example.perpustakaan_app.view.buku.HalamanBuku
import com.example.perpustakaan_app.view.buku.HalamanEditBuku
import com.example.perpustakaan_app.view.buku.HalamanTambahBuku
import com.example.perpustakaan_app.view.login.HalamanLogin
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import com.example.perpustakaan_app.view.anggota.HalamanAnggota
import com.example.perpustakaan_app.view.catatan_denda.HalamanCatatanDenda
import com.example.perpustakaan_app.view.peminjaman_buku.HalamanPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiProfil
import com.example.perpustakaan_app.view.anggota.HalamanEditAnggota
import com.example.perpustakaan_app.view.anggota.HalamanTambahAnggota
import com.example.perpustakaan_app.view.peminjaman_buku.HalamanEditPeminjamanBuku
import com.example.perpustakaan_app.view.peminjaman_buku.HalamanTambahPeminjamanBuku
import com.example.perpustakaan_app.view.profil.HalamanProfil

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PetaNavigasi(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val isUserLoggedIn by appViewModel.isUserLoggedIn.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (isUserLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDestinasi = if (isUserLoggedIn == true) DestinasiBuku.route else DestinasiLogin.route

        LaunchedEffect(isUserLoggedIn) {
            if (isUserLoggedIn == false) {
                if (navController.currentDestination?.route != DestinasiLogin.route) {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        Scaffold(
            bottomBar = {
                if (isUserLoggedIn == true && currentRoute in listOf(
                        DestinasiBuku.route,
                        DestinasiAnggota.route,
                        DestinasiPeminjamanBuku.route,
                        DestinasiCatatanDenda.route,
                        DestinasiProfil.route
                    )) {
                    PerpustakaanBottomAppBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = startDestinasi,
                modifier = modifier.padding(innerPadding)
            ) {
                // --- 1. RUTE LOGIN ---
                composable(DestinasiLogin.route) {
                    HalamanLogin(
                        onLoginSuccess = {
                            navController.navigate(DestinasiBuku.route) {
                                popUpTo(DestinasiLogin.route) { inclusive = true }
                            }
                        }
                    )
                }

                // --- 2. RUTE BUKU ---
                composable(DestinasiBuku.route) { backStackEntry ->
                    HalamanBuku(
                        navController = navController,
                        navigateToItemEntry = { navController.navigate(DestinasiTambahBuku.route) },
                        onEditClick = { id -> navController.navigate("${DestinasiEditBuku.route}/$id") }
                    )
                }

                composable(DestinasiTambahBuku.route) {
                    HalamanTambahBuku(// 1. JALUR BATAL (Tombol Back di atas HP)
                        navigateBack = {
                            // Cukup kembali saja, JANGAN titip pesan apapun
                            navController.popBackStack()
                        },

                        // 2. JALUR SUKSES (Tombol Simpan)
                        onSucces = {
                            // Titip Pesan Refresh
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)

                            // Titip Pesan Notifikasi Hijau
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil menambah data buku!")

                            // Baru pulang
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = DestinasiEditBuku.routeWithArgs,
                    arguments = listOf(navArgument(DestinasiEditBuku.idBukuArg) {
                        type = NavType.IntType
                    })
                ) {
                    HalamanEditBuku(
                        // 1. JALUR BATAL (Tombol Back di atas HP)
                        navigateBack = {
                            // Cukup kembali saja, JANGAN titip pesan apapun
                            navController.popBackStack()
                        },

                        // 2. JALUR SUKSES (Tombol Simpan)
                        onSuccess = {
                            // Titip Pesan Refresh
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)

                            // Titip Pesan Notifikasi Hijau
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil memperbarui data buku!")

                            // Baru pulang
                            navController.popBackStack()
                        }
                    )
                }

                // --- 3. RUTE ANGGOTA ---
                composable(DestinasiAnggota.route) {
                    HalamanAnggota(
                        navController = navController,
                        navigateToItemEntry = { navController.navigate(DestinasiTambahAnggota.route) },
                        onEditClick = { id -> navController.navigate("${DestinasiEditAnggota.route}/$id") }
                    )
                }
                composable(DestinasiTambahAnggota.route) {
                    HalamanTambahAnggota(
                        navigateBack = {
                            navController.popBackStack()
                        },
                        onSucces = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil menambah data anggota!")

                            navController.popBackStack()
                        }
                    )
                }
                composable(DestinasiEditAnggota.routeWithArgs, arguments = listOf(navArgument(DestinasiEditAnggota.idAnggotaArg) {
                    type = NavType.IntType
                })) {
                    HalamanEditAnggota(
                        navigateBack = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil memperbarui data anggota!")

                            navController.popBackStack()
                        }
                    )
                }


                // --- 4. RUTE PEMINJAMAN ---
                composable(DestinasiPeminjamanBuku.route) {
                    HalamanPeminjamanBuku(
                        navController = navController,
                        navigateToItemEntry = {
                            navController.navigate(DestinasiTambahPeminjamanBuku.route)
                        },
                        onEditClick = {
                            id -> navController.navigate("${DestinasiEditPeminjamanBuku.route}/$id")
                        }
                    )
                }
                composable(DestinasiTambahPeminjamanBuku.route){
                    HalamanTambahPeminjamanBuku(
                        navigateBack = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil menambah data peminjaman!")
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = DestinasiEditPeminjamanBuku.routeWithArgs,
                    arguments = listOf(navArgument(DestinasiEditPeminjamanBuku.idPeminjamanBuku) {
                        type = NavType.IntType
                    })
                ) {
                    HalamanEditPeminjamanBuku(
                        navigateBack = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            // Memberi sinyal refresh ke halaman sebelumnya
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_data", true)

                            // Memberi pesan sukses
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("pesan_sukses", "Berhasil memperbarui data peminjaman!")

                            navController.popBackStack()
                        }
                    )
                }

                // --- 5. RUTE DENDA ---
                composable(DestinasiCatatanDenda.route) {
                    HalamanCatatanDenda(
                        navigateBack = { navController.popBackStack() }
                    )
                }

                composable(DestinasiProfil.route) {
                    HalamanProfil()
                }
            }
        }
    }
}