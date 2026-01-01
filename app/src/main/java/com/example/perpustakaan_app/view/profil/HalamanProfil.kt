package com.example.perpustakaan_app.view.profil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanProfil(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon Profil Besar
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Halo, Admin!",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Selamat bekerja kembali.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Logout
        Button(
            onClick = { appViewModel.logout() }, // Panggil fungsi logout dari AppViewModel
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Logout / Keluar")
        }
    }
}