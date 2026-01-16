package com.example.perpustakaan_app.view.catatan_denda

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.modeldata.DataCatatanDenda
import com.example.perpustakaan_app.utils.konversiTanggalServerKeLokal
import com.example.perpustakaan_app.viewmodel.catatan_denda.CatatanDendaUiState
import com.example.perpustakaan_app.viewmodel.catatan_denda.CatatanDendaViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCatatanDenda(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatatanDendaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val pesanBar by viewModel.pesanBar.collectAsState()
    var selectedDendaId by remember { mutableStateOf<Int?>(null) }
    var openDialogPengembalian by remember { mutableStateOf(false) }

    // Tampilkan Snackbar jika ada pesan
    LaunchedEffect(pesanBar) {
        pesanBar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetPesanBar()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getCatatanDenda()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (openDialogPengembalian && selectedDendaId != null) {
            DendaConfirmationDialog(
                onDendaConfirm = {
                    selectedDendaId?.let { id ->
                        viewModel.updateStatusDenda(id)
                    }
                    openDialogPengembalian = false
                    selectedDendaId = null
                },
                onDendaCancel = {
                    openDialogPengembalian = false
                    selectedDendaId = null
                }
            )
        }
        BodyCatatanDenda(
            dendaUiState = viewModel.dendaUiState,
            retryAction = { viewModel.getCatatanDenda() },
            onUpdateStatus = { id_denda ->
                selectedDendaId = id_denda
                openDialogPengembalian = true
            },
            searchQuery = viewModel.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onSearch = { viewModel.searchDenda() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BodyCatatanDenda(
    dendaUiState: CatatanDendaUiState,
    retryAction: () -> Unit,
    onUpdateStatus: (Int) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ){
        SearchDendaBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(5.dp)
        )
        when (dendaUiState) {
            is CatatanDendaUiState.Loading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatatanDendaUiState.Success -> {
                if (dendaUiState.catatanDenda.isEmpty()) {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada catatan denda.")
                    }
                } else {
                    ListCatatanDenda(
                        listDenda = dendaUiState.catatanDenda,
                        onUpdateStatus = onUpdateStatus,
                        modifier = modifier
                    )
                }
            }
            is CatatanDendaUiState.Error -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Gagal memuat data")
                    Button(onClick = retryAction) { Text("Coba Lagi") }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListCatatanDenda(
    listDenda: List<DataCatatanDenda>,
    onUpdateStatus: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listDenda) { denda ->
            ItemDenda(denda = denda, onUpdateStatus = onUpdateStatus)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemDenda(
    denda: DataCatatanDenda,
    onUpdateStatus: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Format Rupiah
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val isLunas = denda.status.equals("Lunas", ignoreCase = true)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLunas) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nama: ${denda.nama}",
                    style = MaterialTheme.typography.titleMedium
                )
                // Badge Status
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isLunas) Color(0xFF4CAF50) else Color(0xFFE53935)
                ) {
                    Text(
                        text = denda.status,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Terlambat", value = "${denda.hari_terlambat} Hari")
            InfoRow(label = "Total Denda", value = formatRupiah.format(denda.jumlah))

            if (isLunas && denda.tanggal_dibayar != null) {
                InfoRow(
                    label = "Dibayar Tgl",
                    value = konversiTanggalServerKeLokal(denda.tanggal_dibayar) // Tidak perlu safe call ?. karena sudah dicek di if
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Aksi (Hanya muncul jika BELUM lunas)
            if (!isLunas) {
                Button(
                    onClick = { onUpdateStatus(denda.id_denda) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tandai Lunas")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDendaBar(
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
private fun DendaConfirmationDialog(
    onDendaConfirm: () -> Unit,
    onDendaCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Jangan lakukan apa-apa jika klik di luar */ },
        title = { Text("Pelunasan") },
        text = { Text("Apakah Anda yakin ingin anggota sudah melunaskan denda ini?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDendaCancel) {
                Text(text = "Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onDendaConfirm) {
                Text(text = "Ya, Kembalikan", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}