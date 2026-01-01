package com.example.perpustakaan_app.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiAnggota
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiBuku
import com.example.perpustakaan_app.uicontroller.route.catatan_denda.DestinasiCatatanDenda
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiProfil

// 1. Definisi Item Menu
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// 2. Daftar Menu Utama
val listMenu = listOf(
    BottomNavItem("Buku", Icons.Default.Book, DestinasiBuku.route),
    BottomNavItem("Anggota", Icons.Default.AccountCircle, DestinasiAnggota.route),
    BottomNavItem("Peminjaman", Icons.Default.DateRange, DestinasiPeminjamanBuku.route),
    BottomNavItem("Denda", Icons.Default.Warning, DestinasiCatatanDenda.route),
    BottomNavItem("Profil", Icons.Default.Person, DestinasiProfil.route)
)

// 3. Komponen Bottom Bar
@Composable
fun PerpustakaanBottomAppBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        listMenu.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Navigasi ke tab yang dipilih
                    navController.navigate(item.route) {
                        // Agar tidak menumpuk halaman saat bolak-balik (Pop Up ke halaman awal)
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Hindari membuat salinan halaman yang sama jika diklik lagi
                        launchSingleTop = true
                        // Restore state (scroll position, dll)
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}