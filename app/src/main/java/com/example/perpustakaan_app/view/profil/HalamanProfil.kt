import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.profil.ProfilUiState
import com.example.perpustakaan_app.viewmodel.profil.ProfilViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanProfil(
    navController: NavController,
    modifier: Modifier = Modifier,
    profilUiState: ProfilUiState, // Menerima state dari NavGraph
    onLoadData: () -> Unit,       // Lambda untuk memicu pengambilan data
    onEditClick: (Int) -> Unit,
    retryAction: () -> Unit,
    viewModel: ProfilViewModel = viewModel(factory = PenyediaViewModel.Factory),
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val pesanSuksesState = savedStateHandle?.getLiveData<String>("pesan_sukses")?.observeAsState()
    val pesanSukses = pesanSuksesState?.value
    // Pemicu pengambilan data saat halaman pertama kali dibuka
    LaunchedEffect(Unit) {
        onLoadData()
    }

    LaunchedEffect(pesanSukses) {
        if (!pesanSukses.isNullOrBlank()) {
            snackbarHostState.showSnackbar(pesanSukses)
            savedStateHandle?.remove<String>("pesan_sukses")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.pesanChannel.collect { pesan ->
            snackbarHostState.showSnackbar(pesan)
        }
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

            WidgetSnackbarKeren(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 10.dp)
            )
        }
    }
}