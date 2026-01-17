package com.example.perpustakaan_app.view.buku

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.modeldata.DetailBuku
import com.example.perpustakaan_app.modeldata.UIStateBuku
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiNavigasiBuku
import com.example.perpustakaan_app.uicontroller.route.buku.DestinasiTambahBuku
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.viewmodel.buku.TambahBukuViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import coil.compose.AsyncImage
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahBuku(
    navigateBack: () -> Unit,
    onSucces: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TambahBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

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

    // State untuk mengontrol tampilan kamera
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        // Tampilkan layar Scanner Full Screen
        BarcodeScannerScreen(
            onIsbnScanned = { scannedIsbn ->
                // Saat ISBN ditemukan, cari data buku di Google Books
                viewModel.getGoogleBook(scannedIsbn)
                showCamera = false
            },
            onCancel = { showCamera = false }
        )
    } else {
        Scaffold(
            snackbarHost = { WidgetSnackbarKeren(hostState = snackbarHostState) },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(colorResource(R.color.white)),
            topBar = {
                PerpustakaanTopAppBar(
                    title = DestinasiTambahBuku.tittleRes,
                    canNavigateBack = true,
                    scrollBehavior = scrollBehavior,
                    navigateUp = navigateBack,
                    modifier = Modifier.background(colorResource(R.color.white))
                )
            }
        ) { innerPadding ->
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
                        val isSuccess = viewModel.simpanBuku(context)
                        if (isSuccess) {
                            onSucces()
                        }
                    }
                },
                onScanClick = { showCamera = true },
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(colorResource(R.color.white))
            )

        }
    }
}

@Composable
fun BodyTambahBuku(
    uiStateBuku: UIStateBuku,
    onValueChange: (DetailBuku) -> Unit,
    selectedImageUri: android.net.Uri?,
    onImagePick: () -> Unit,
    onSaveClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
            .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()

        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Preview Gambar",
                    modifier = Modifier.size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onImagePick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.navy),
                    contentColor = colorResource(R.color.white)
                )) {
                Text("Pilih Gambar Buku")
            }
        }
        FormInputBuku(
            detailBuku = uiStateBuku.detailBuku,
            onValueChange = onValueChange,
            onScanClick = onScanClick
        )
        Button(
            onClick = onSaveClick,
            enabled = uiStateBuku.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.navy),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text("Simpan Buku")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputBuku(
    detailBuku: DetailBuku,
    onValueChange: (DetailBuku) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = detailBuku.isbn,
                onValueChange = { onValueChange(detailBuku.copy(isbn = it)) },
                label = { Text("ISBN*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            IconButton(
                onClick = onScanClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan ISBN",
                    tint = colorResource(R.color.navy))
            }
        }

        OutlinedTextField(
            value = detailBuku.judul,
            onValueChange = { onValueChange(detailBuku.copy(judul = it)) },
            label = { Text("Judul Buku*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.penulis,
            onValueChange = { onValueChange(detailBuku.copy(penulis = it)) },
            label = { Text("Penulis*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.penerbit,
            onValueChange = { onValueChange(detailBuku.copy(penerbit = it)) },
            label = { Text("Penerbit*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.tahun_terbit,
            onValueChange = { inputBaru ->
                if (inputBaru.all { char -> char.isDigit() }) {
                    if (inputBaru.length <= 4) {
                        onValueChange(detailBuku.copy(tahun_terbit = inputBaru))
                    }
                }
            },
            label = { Text("Tahun Terbit*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = detailBuku.stok.toString(),
            onValueChange = {
                val stokBaru = it.toIntOrNull() ?: 0
                onValueChange(detailBuku.copy(stok = stokBaru))
            },
            label = { Text("Stok*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = detailBuku.nomor_panggil,
            onValueChange = { onValueChange(detailBuku.copy(nomor_panggil = it)) },
            label = { Text("Nomor Panggil") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.deskripsi,
            onValueChange = { onValueChange(detailBuku.copy(deskripsi = it)) },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}