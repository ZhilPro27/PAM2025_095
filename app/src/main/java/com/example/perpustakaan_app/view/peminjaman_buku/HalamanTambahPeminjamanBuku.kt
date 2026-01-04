package com.example.perpustakaan_app.view.peminjaman_buku

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiTambahAnggota
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiTambahPeminjamanBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.view.widget.DynamicSelectTextField
import com.example.perpustakaan_app.view.widget.OutlinedDateField
import com.example.perpustakaan_app.view.widget.SearchableDropDownMenu
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.DetailPeminjaman
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.TambahPeminjamanViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch


// -- UI Screen --
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahPeminjamanBuku(
    onSuccess: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TambahPeminjamanViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.pesanChannel.collect { pesan ->
            snackbarHostState.showSnackbar(pesan)
        }
    }


    Scaffold(
        snackbarHost = { WidgetSnackbarKeren(hostState = snackbarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PerpustakaanTopAppBar(
                title = DestinasiTambahPeminjamanBuku.tittleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val detail = viewModel.uiState.detailPeminjaman

            SearchableDropDownMenu(
                options = viewModel.listAnggota,
                label = "Cari & Pilih Anggota",
                selectedOptionLabel = viewModel.selectedAnggotaNama, // State teks dari VM
                onOptionSelected = { anggota ->
                    viewModel.onAnggotaSelected(anggota)
                },
                itemToString = { it.nama }, // Cara ambil teks dari objek Anggota
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            SearchableDropDownMenu(
                options = viewModel.listBuku,
                label = "Cari & Pilih Buku",
                selectedOptionLabel = viewModel.selectedBukuJudul, // State teks dari VM
                onOptionSelected = { buku ->
                    viewModel.onBukuSelected(buku)
                },
                itemToString = { it.judul }, // Cara ambil teks dari objek Buku
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedDateField(
                value = detail.tanggal_pinjam,
                onValueChange = { selectedDate ->
                    viewModel.updateUiState(detail.copy(tanggal_pinjam = selectedDate))
                },
                label = "Tanggal Pinjam",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedDateField(
                value = detail.tanggal_jatuh_tempo,
                onValueChange = { selectedDate ->
                    viewModel.updateUiState(detail.copy(tanggal_jatuh_tempo = selectedDate))
                },
                label = "Tanggal Jatuh Tempo",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val isSuccess = viewModel.savePeminjaman(context)
                        if (isSuccess) {
                            onSuccess()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Data Peminjaman")
            }
        }
    }
}
