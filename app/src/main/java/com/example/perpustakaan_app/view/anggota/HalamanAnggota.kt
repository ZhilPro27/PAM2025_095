package com.example.perpustakaan_app.view.anggota

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.perpustakaan_app.modeldata.DataAnggota
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.viewmodel.AppViewModel
import com.example.perpustakaan_app.viewmodel.anggota.AnggotaUiState
import com.example.perpustakaan_app.viewmodel.anggota.AnggotaViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import com.example.perpustakaan_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanAnggota(
    navController: NavController,
    navigateToItemEntry: () -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnggotaViewModel = viewModel(factory = PenyediaViewModel.Factory),
    appViewModel: AppViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val refreshDataState = savedStateHandle?.getLiveData<Boolean>("refresh_data")?.observeAsState()
    val refreshData = refreshDataState?.value
    val pesanSuksesState = savedStateHandle?.getLiveData<String>("pesan_sukses")?.observeAsState()
    val pesanSukses = pesanSuksesState?.value
    var openDialog by remember { mutableStateOf(false) }
    var selectedAnggotaId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(refreshData) {
        if (refreshData == true) {
            viewModel.getAnggota()
            savedStateHandle.remove<Boolean>("refresh_data")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAnggota()
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
            modifier = Modifier,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToItemEntry,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = colorResource(id = R.color.navy)
                ) {
                    Icon(imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Anggota",
                        tint = colorResource(id = R.color.white))
                }
            }
        ){
            if(openDialog && selectedAnggotaId != null){
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        selectedAnggotaId?.let { id ->
                            viewModel.deleteAnggota(id)
                        }
                        openDialog = false
                        selectedAnggotaId = null
                    },
                    onDeleteCancel = {
                        openDialog = false
                        selectedAnggotaId = null
                    }
                )
            }

            BodyHalamanAnggota(
                anggotaUiState = viewModel.anggotaUiState,
                retryAction = viewModel::getAnggota,
                onEditClick = onEditClick,
                onLogout = { appViewModel.logout() },
                onDelete = { id_anggota ->
                    selectedAnggotaId = id_anggota
                    openDialog = true
                },
                searchQuery = viewModel.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = { viewModel.searchAnggota() },
                modifier = Modifier
                    .background(colorResource(R.color.white))
                    .fillMaxSize()
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
fun BodyHalamanAnggota(
    anggotaUiState: AnggotaUiState,
    retryAction: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.white))
    ) {
        SearchAnggotaBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (anggotaUiState) {
            is AnggotaUiState.Loading -> LoadingScreen(modifier)
            is AnggotaUiState.Success -> {
                if (anggotaUiState.anggota.isEmpty()) {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data anggota")
                    }
                } else {
                    ListAnggota(
                        anggotaList = anggotaUiState.anggota,
                        onDelete = onDelete,
                        onEditClick = onEditClick,
                        modifier = modifier.fillMaxSize()
                            .background(colorResource(id = R.color.white))
                    )
                }
            }
            is AnggotaUiState.Error -> {
                Column(
                    modifier = modifier.fillMaxSize()
                        .background(colorResource(id = R.color.white)),
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
fun ListAnggota(
    anggotaList: List<DataAnggota>,
    onEditClick: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 0.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(anggotaList) { anggota ->
            ItemAnggota(anggota = anggota, onDelete = onDelete, onEditClick = onEditClick)
        }
    }
}

@Composable
fun ItemAnggota(
    anggota: DataAnggota,
    onDelete: (Int) -> Unit,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 7.dp,
            draggedElevation = 7.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anggota.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Default
                )
                Text(
                    text = "Nomor HP: ${anggota.no_hp}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Default
                )
                Text(
                    text = "Alamat: ${anggota.alamat}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Default
                )
            }
            IconButton(onClick = { onEditClick(anggota.id_anggota) }) {
                Icon(Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = colorResource(id = R.color.info))
            }

            IconButton(onClick = { onDelete(anggota.id_anggota) }) {
                Icon(Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = colorResource(id = R.color.error))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAnggotaBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorResource(id = R.color.navy),
            unfocusedTextColor = colorResource(id = R.color.navy),

            focusedTrailingIconColor = colorResource(id = R.color.navy),
            unfocusedTrailingIconColor = colorResource(id = R.color.navy),

            focusedLabelColor = colorResource(id = R.color.navy),
            unfocusedLabelColor = colorResource(id = R.color.navy),

            focusedPlaceholderColor = colorResource(id = R.color.navy),
            unfocusedPlaceholderColor = colorResource(id = R.color.navy),

            focusedContainerColor = colorResource(id = R.color.white),
            unfocusedContainerColor = colorResource(id = R.color.white)
        ),
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
        text = { Text("Apakah Anda yakin ingin menghapus anggota ini? Data yang dihapus tidak dapat dikembalikan.") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(R.color.navy)
                )) {
                Text(text = "Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Ya, Hapus",
                    color = colorResource(R.color.error))
            }
        }
    )
}