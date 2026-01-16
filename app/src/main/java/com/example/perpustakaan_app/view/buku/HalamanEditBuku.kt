package com.example.perpustakaan_app.view.buku

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiEditBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.viewmodel.buku.EditBukuViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditBuku(
    navigateBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showCamera by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.onImageSelected(uri)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.pesanChannel.collect { pesan ->
            snackbarHostState.showSnackbar(pesan)
        }
    }
    if (showCamera) {
        // Tampilkan layar Scanner Full Screen
        BarcodeScannerScreen(
            onIsbnScanned = { scannedIsbn ->
                // Saat ISBN ditemukan, update state ViewModel dan tutup kamera
                viewModel.updateUiState(
                    viewModel.uiStateBuku.detailBuku.copy(isbn = scannedIsbn)
                )
                showCamera = false
            },
            onCancel = { showCamera = false }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    PerpustakaanTopAppBar(
                        title = DestinasiEditBuku.tittleRes,
                        canNavigateBack = true,
                        scrollBehavior = scrollBehavior,
                        navigateUp = navigateBack
                    )
                }
            ) { innerPadding ->
                // Kita reuse BodyTambahBuku tapi untuk edit
                BodyTambahBuku(
                    uiStateBuku = viewModel.uiStateBuku,
                    selectedImageUri = viewModel.selectedImageUri,
                    onImagePick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onValueChange = viewModel::updateUiState,
                    onSaveClick = {
                        coroutineScope.launch {
                            val isSuccess = viewModel.updateBuku(context)
                            if (isSuccess) {
                                // JIKA SUKSES: Panggil callback khusus ini
                                onSuccess()
                            }
                        }
                    },
                    onScanClick = { showCamera = true },
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                )
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