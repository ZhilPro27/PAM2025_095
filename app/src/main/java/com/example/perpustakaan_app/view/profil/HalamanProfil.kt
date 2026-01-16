import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.profil.ProfilUiState
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanProfil(
    modifier: Modifier = Modifier,
    profilUiState: ProfilUiState, // Menerima state dari NavGraph
    onLoadData: () -> Unit,       // Lambda untuk memicu pengambilan data
    onEditClick: (Int) -> Unit,
    retryAction: () -> Unit,
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Pemicu pengambilan data saat halaman pertama kali dibuka
    LaunchedEffect(Unit) {
        onLoadData()
    }

    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (profilUiState) {
                is ProfilUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfilUiState.Success -> {
                    val profil = profilUiState.profil
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Halo, ${profil.email}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Pustakawan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { onEditClick(profil.id_pustakawan) },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text("Edit Profil")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { appViewModel.logout() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text("Logout / Keluar")
                        }
                    }
                }
                is ProfilUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Gagal memuat profil pustakawan.")
                        Button(onClick = retryAction) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}