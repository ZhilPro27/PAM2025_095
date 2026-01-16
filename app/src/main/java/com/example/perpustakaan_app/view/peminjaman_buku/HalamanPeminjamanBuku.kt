package com.example.perpustakaan_app.view.peminjaman_buku

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.perpustakaan_app.modeldata.DataPeminjamanBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.utils.konversiTanggalServerKeLokal
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.PeminjamanBukuUiState
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.PeminjamanBukuViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPeminjamanBuku(
    navController: NavController,
    navigateToItemEntry: () -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeminjamanBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val refreshDataState = savedStateHandle?.getLiveData<Boolean>("refresh_data")?.observeAsState()
    val refreshData = refreshDataState?.value
    val pesanSuksesState = savedStateHandle?.getLiveData<String>("pesan_sukses")?.observeAsState()
    val pesanSukses = pesanSuksesState?.value
    var openDialog by remember { mutableStateOf(false) }
    var selectedPeminjamanId by remember { mutableStateOf<Int?>(null) }
    var openDialogPengembalian by remember { mutableStateOf(false) }

    LaunchedEffect(refreshData) {
        if (refreshData == true) {
            viewModel.getPeminjaman()
            savedStateHandle.remove<Boolean>("refresh_data")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPeminjaman()
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

    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = navigateToItemEntry) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Peminjaman")
                }
            }
        ) { innerPadding ->
            if(openDialog && selectedPeminjamanId != null){
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        selectedPeminjamanId?.let { id ->
                            viewModel.deletePeminjaman(id)
                        }
                        openDialog = false
                        selectedPeminjamanId = null
                    },
                    onDeleteCancel = {
                        openDialog = false
                        selectedPeminjamanId = null
                    }
                )
            }

            if(openDialogPengembalian && selectedPeminjamanId != null){
                PengembalianConfirmationDialog(
                    onPengembalianConfirm = {
                        selectedPeminjamanId?.let { id ->
                            viewModel.returnBook(id)
                        }
                        openDialogPengembalian = false
                        selectedPeminjamanId = null
                    },
                    onPengembalianCancel = {
                        openDialogPengembalian = false
                        selectedPeminjamanId = null
                    }
                )
            }
            BodyHalamanPeminjamanBuku(
                peminjamanBukuUiState = viewModel.peminjamanBukuUiState,
                searchQuery = viewModel.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = { viewModel.searchPeminjaman() },
                onDelete = { id_peminjaman ->
                    selectedPeminjamanId = id_peminjaman
                    openDialog = true
                },
                onReturn = { id_peminjaman ->
                    selectedPeminjamanId = id_peminjaman
                    openDialogPengembalian = true
                },
                onEditClick = onEditClick,
                modifier = Modifier.padding(innerPadding)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BodyHalamanPeminjamanBuku(
    peminjamanBukuUiState: PeminjamanBukuUiState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDelete: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onReturn: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeminjamanBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    Column(modifier = Modifier
        .fillMaxSize()) {
        SearchPeminjamanBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        )
        when (peminjamanBukuUiState) {
            is PeminjamanBukuUiState.Loading -> LoadingScreen(modifier)
            is PeminjamanBukuUiState.Success -> {
                if (peminjamanBukuUiState.peminjaman.isEmpty()) {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data peminjaman")
                    }
                } else {
                    ListPeminjaman(
                        listPeminjaman = peminjamanBukuUiState.peminjaman,
                        onReturn = onReturn,
                        onDelete = onDelete,
                        onEditClick = onEditClick,
                        modifier = modifier
                    )
                }
            }
            is PeminjamanBukuUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Terjadi kesalahan saat memuat data")
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListPeminjaman(
    listPeminjaman: List<DataPeminjamanBuku>,
    onDelete: (Int) -> Unit,
    onReturn: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 0.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(listPeminjaman) { peminjaman ->
            ItemPeminjamanCard(
                peminjaman = peminjaman,
                onDelete = onDelete,
                onReturn = onReturn,
                onEditClick = onEditClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemPeminjamanCard(
    peminjaman: DataPeminjamanBuku,
    onDelete: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onReturn: (Int) -> Unit
) {
    // Cek Status
    val isReturned = peminjaman.status == "kembali"
    val tanggalPinjam = konversiTanggalServerKeLokal(peminjaman.tanggal_pinjam)
    val tanggalJatuhTempo = konversiTanggalServerKeLokal(peminjaman.tanggal_jatuh_tempo)
    val tanggalKembali = konversiTanggalServerKeLokal(peminjaman.tanggal_kembali)


    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isReturned) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Peminjam: ${peminjaman.nama}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Badge Status
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isReturned) Color(0xFF4CAF50) else Color(0xFFFF9800)
                ) {
                    Text(
                        text = peminjaman.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Judul Buku       : ${peminjaman.judul}")
            Text("Tgl Pinjam   : ${tanggalPinjam}")
            Text("Jatuh Tempo: ${tanggalJatuhTempo}")

            if (isReturned) {
                Text(
                    "Dikembalikan: ${tanggalKembali ?: "-"}",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {onEditClick(peminjaman.id_peminjaman)}) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(onClick = { onDelete(peminjaman.id_peminjaman) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (!isReturned) {
                    Button(
                        onClick = { onReturn(peminjaman.id_peminjaman) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Kembalikan")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPeminjamanBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Cari nama") },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Cari")
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Jangan lakukan apa-apa jika klik di luar */ },
        title = { Text("Hapus Data") },
        text = { Text("Apakah Anda yakin ingin menghapus peminjaman ini ini? Data yang dihapus tidak dapat dikembalikan.") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = "Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Ya, Hapus", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
private fun PengembalianConfirmationDialog(
    onPengembalianConfirm: () -> Unit,
    onPengembalianCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Jangan lakukan apa-apa jika klik di luar */ },
        title = { Text("Pengembalian") },
        text = { Text("Apakah Anda yakin ingin mengembalikan buku ini?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onPengembalianCancel) {
                Text(text = "Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onPengembalianConfirm) {
                Text(text = "Ya, Kembalikan", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
