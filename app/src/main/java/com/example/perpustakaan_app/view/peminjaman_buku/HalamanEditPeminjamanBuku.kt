package com.example.perpustakaan_app.view.peminjaman_buku

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.modeldata.DataAnggota
import com.example.perpustakaan_app.modeldata.DataBuku
import com.example.perpustakaan_app.modeldata.DetailPeminjamanBuku
import com.example.perpustakaan_app.modeldata.UIStatePeminjamanBuku
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiEditPeminjamanBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.utils.konversiTanggalServerKeLokal
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.view.widget.OutlinedDateField
import com.example.perpustakaan_app.view.widget.SearchableDropDownMenu
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.EditPeminjamanBukuViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import com.example.perpustakaan_app.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditPeminjamanBuku(
    navigateBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPeminjamanBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen pesan notifikasi dari ViewModel
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
                title = DestinasiEditPeminjamanBuku.tittleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        BodyEditPeminjamanBuku(
            uiState = viewModel.uiState,
            listAnggota = viewModel.listAnggota,
            listBuku = viewModel.listBuku,
            onAnggotaSelected = viewModel::onAnggotaSelected,
            onBukuSelected = viewModel::onBukuSelected,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val isSuccess = viewModel.updatePeminjaman(context)
                    if (isSuccess) {
                        onSuccess()
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .background(colorResource(R.color.white))
        )
    }
}

@Composable
fun BodyEditPeminjamanBuku(
    uiState: UIStatePeminjamanBuku,
    listAnggota: List<DataAnggota>,
    listBuku: List<DataBuku>,
    onAnggotaSelected: (DataAnggota) -> Unit,
    onBukuSelected: (DataBuku) -> Unit,
    onValueChange: (DetailPeminjamanBuku) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormInputEditPeminjamanBuku(
            detailPeminjaman = uiState.detailPeminjamanBuku,
            listAnggota = listAnggota,
            listBuku = listBuku,
            onAnggotaSelected = onAnggotaSelected,
            onBukuSelected = onBukuSelected,
            onValueChange = onValueChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isEntryValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.navy),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text("Simpan Perubahan")
        }
    }
}

@Composable
fun FormInputEditPeminjamanBuku(
    detailPeminjaman: DetailPeminjamanBuku,
    listAnggota: List<DataAnggota>,
    listBuku: List<DataBuku>,
    onAnggotaSelected: (DataAnggota) -> Unit,
    onBukuSelected: (DataBuku) -> Unit,
    onValueChange: (DetailPeminjamanBuku) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dropdown Anggota
        SearchableDropDownMenu(
            options = listAnggota,
            label = "Pilih Anggota*",
            selectedOptionLabel = detailPeminjaman.nama, // Menampilkan nama yang sudah terpilih
            onOptionSelected = onAnggotaSelected,
            itemToString = { it.nama },
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown Buku
        SearchableDropDownMenu(
            options = listBuku,
            label = "Pilih Buku*",
            selectedOptionLabel = detailPeminjaman.judul, // Menampilkan judul yang sudah terpilih
            onOptionSelected = onBukuSelected,
            itemToString = { it.judul },
            modifier = Modifier.fillMaxWidth()
        )

        // Input Tanggal Pinjam
        OutlinedDateField(
            value = detailPeminjaman.tanggal_pinjam,
            onValueChange = { selectedDate ->
                onValueChange(detailPeminjaman.copy(tanggal_pinjam = selectedDate))
            },
            label = "Tanggal Pinjam*",
            modifier = Modifier.fillMaxWidth()
        )

        // Input Tanggal Jatuh Tempo
        OutlinedDateField(
            value = detailPeminjaman.tanggal_jatuh_tempo,
            onValueChange = { selectedDate ->
                onValueChange(detailPeminjaman.copy(tanggal_jatuh_tempo = selectedDate))
            },
            label = "Tanggal Jatuh Tempo*",
            modifier = Modifier.fillMaxWidth()
        )
    }
}