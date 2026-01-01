package com.example.perpustakaan_app.view.buku

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.perpustakaan_app.modeldata.DataBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.viewmodel.buku.BukuUiState
import com.example.perpustakaan_app.viewmodel.buku.BukuViewModel
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanBuku(
    navController: NavHostController,
    navigateToItemEntry: () -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BukuViewModel = viewModel(factory = PenyediaViewModel.Factory),
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val refreshDataState = savedStateHandle?.getLiveData<Boolean>("refresh_data")?.observeAsState()
    val refreshData = refreshDataState?.value
    val pesanSuksesState = savedStateHandle?.getLiveData<String>("pesan_sukses")?.observeAsState()
    val pesanSukses = pesanSuksesState?.value
    var openDialog by remember { mutableStateOf(false) }
    var selectedBukuId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(refreshData) {
        if (refreshData == true) {
            viewModel.getBuku()
            savedStateHandle.remove<Boolean>("refresh_data")
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getBuku()
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
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToItemEntry,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Buku")
                }
            },
        ) { innerPadding ->
            if (openDialog && selectedBukuId != null) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        // Jika user yakin: Hapus buku berdasarkan ID yang disimpan
                        selectedBukuId?.let { id ->
                            viewModel.deleteBuku(id)
                        }
                        openDialog = false // Tutup dialog
                        selectedBukuId = null // Reset ID
                    },
                    onDeleteCancel = {
                        // Jika user batal: Cukup tutup dialog
                        openDialog = false
                        selectedBukuId = null
                    }
                )
            }

            BodyHalamanBuku(
                bukuUiState = viewModel.bukuUiState,
                retryAction = viewModel::getBuku,
                onEditClick = onEditClick,
                onLogout = { appViewModel.logout() },
                onDelete = { id_buku ->
                    selectedBukuId = id_buku
                    openDialog = true
                },
                searchQuery = viewModel.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = { viewModel.searchBuku() },
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

@Composable
fun BodyHalamanBuku(
    bukuUiState: BukuUiState,
    retryAction: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .statusBarsPadding()
        .fillMaxSize()
    ) {
        SearchBukuBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(5.dp)
        )
        when (bukuUiState) {
            is BukuUiState.Loading -> LoadingScreen(modifier)
            is BukuUiState.Success -> {
                if (bukuUiState.buku.isEmpty()) {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data buku")
                    }
                } else {
                    ListBuku(
                        bukuList = bukuUiState.buku,
                        onDelete = onDelete,
                        onEditClick = onEditClick,
                        modifier = modifier
                    )
                }
            }
            is BukuUiState.Error -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Gagal memuat data. Token mungkin kadaluarsa.")
                    Button(onClick = retryAction) { Text("Coba Lagi") }
                    OutlinedButton(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Logout / Keluar")
                    }
                }
            }
        }
    }
}

@Composable
fun ListBuku(
    bukuList: List<DataBuku>,
    onEditClick: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bukuList) { buku ->
            ItemBuku(buku = buku, onDelete = onDelete, onEditClick = onEditClick)
        }
    }
}

@Composable
fun ItemBuku(
    buku: DataBuku,
    onDelete: (Int) -> Unit,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = buku.gambar?.let { img ->
                if (img.startsWith("http")) img else "https://perpustakaan-pam-app.zhilalkrisna.my.id/uploads/$img"
            } ?: ""

            AsyncImage(
                model = imageUrl,
                contentDescription = buku.judul,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                //error = painterResource(id = R.drawable.ic_broken_image),
                //placeholder = painterResource(id = R.drawable.ic_loading)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buku.judul,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Penulis: ${buku.penulis}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Stok: ${buku.stok}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onEditClick(buku.id_buku) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(buku.id_buku) }) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gagal memuat data")
        Button(onClick = retryAction) { Text("Coba Lagi") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBukuBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Cari judul atau penulis...") },
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
        text = { Text("Apakah Anda yakin ingin menghapus buku ini? Data yang dihapus tidak dapat dikembalikan.") },
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