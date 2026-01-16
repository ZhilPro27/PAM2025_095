package com.example.perpustakaan_app.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import com.example.perpustakaan_app.R
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiAnggota
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiBuku
import com.example.perpustakaan_app.uicontroller.route.catatan_denda.DestinasiCatatanDenda
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiPeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiProfil

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listMenu = listOf(
    BottomNavItem("Buku", Icons.Default.Book, DestinasiBuku.route),
    BottomNavItem("Anggota", Icons.Default.Person, DestinasiAnggota.route),
    BottomNavItem("Peminjaman", Icons.Default.DateRange, DestinasiPeminjamanBuku.route),
    BottomNavItem("Denda", Icons.Default.Warning, DestinasiCatatanDenda.route),
    BottomNavItem("Profil", Icons.Default.AccountCircle, DestinasiProfil.route)
)

@Composable
fun PerpustakaanBottomAppBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.navy)
    ) {
        listMenu.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.navy),
                    selectedTextColor = colorResource(id = R.color.info),
                    indicatorColor = colorResource(id = R.color.info),
                    unselectedIconColor = colorResource(id = R.color.white),
                    unselectedTextColor = colorResource(id = R.color.white)
                ),
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
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