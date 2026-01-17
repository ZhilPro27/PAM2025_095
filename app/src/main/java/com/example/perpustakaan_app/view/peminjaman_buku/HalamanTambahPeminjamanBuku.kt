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
import com.example.perpustakaan_app.uicontroller.route.peminjaman_buku.DestinasiTambahPeminjamanBuku
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.view.widget.OutlinedDateField
import com.example.perpustakaan_app.view.widget.SearchableDropDownMenu
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.DetailPeminjaman
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.InsertPeminjamanUiState
import com.example.perpustakaan_app.viewmodel.peminjaman_buku.TambahPeminjamanViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import com.example.perpustakaan_app.R
// 1. Komponen Utama Halaman
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahPeminjamanBuku(
    onSuccess: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TambahPeminjamanViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(colorResource(R.color.white)),
        topBar = {
            PerpustakaanTopAppBar(
                title = DestinasiTambahPeminjamanBuku.tittleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        BodyTambahPeminjamanBuku(
            uiState = viewModel.uiState,
            listAnggota = viewModel.listAnggota,
            listBuku = viewModel.listBuku,
            selectedAnggotaNama = viewModel.selectedAnggotaNama,
            selectedBukuJudul = viewModel.selectedBukuJudul,
            onAnggotaSelected = viewModel::onAnggotaSelected,
            onBukuSelected = viewModel::onBukuSelected,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val isSuccess = viewModel.savePeminjaman(context)
                    if (isSuccess) {
                        onSuccess()
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(colorResource(R.color.white))
                .fillMaxSize()
        )
    }
}

// 2. Komponen Body (Layout Konten & Tombol Simpan)
@Composable
fun BodyTambahPeminjamanBuku(
    uiState: InsertPeminjamanUiState,
    listAnggota: List<DataAnggota>,
    listBuku: List<DataBuku>,
    selectedAnggotaNama: String,
    selectedBukuJudul: String,
    onAnggotaSelected: (DataAnggota) -> Unit,
    onBukuSelected: (DataBuku) -> Unit,
    onValueChange: (DetailPeminjaman) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp).background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormInputPeminjamanBuku(
            detailPeminjaman = uiState.detailPeminjaman,
            listAnggota = listAnggota,
            listBuku = listBuku,
            selectedAnggotaNama = selectedAnggotaNama,
            selectedBukuJudul = selectedBukuJudul,
            onAnggotaSelected = onAnggotaSelected,
            onBukuSelected = onBukuSelected,
            onValueChange = onValueChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.navy),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Text("Simpan Data Peminjaman")
        }
    }
}

// 3. Komponen Form Input (Dropdown & Tanggal)
@Composable
fun FormInputPeminjamanBuku(
    detailPeminjaman: DetailPeminjaman,
    listAnggota: List<DataAnggota>,
    listBuku: List<DataBuku>,
    selectedAnggotaNama: String,
    selectedBukuJudul: String,
    onAnggotaSelected: (DataAnggota) -> Unit,
    onBukuSelected: (DataBuku) -> Unit,
    onValueChange: (DetailPeminjaman) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dropdown Anggota
        SearchableDropDownMenu(
            options = listAnggota,
            label = "Cari & Pilih Anggota*",
            selectedOptionLabel = selectedAnggotaNama,
            onOptionSelected = onAnggotaSelected,
            itemToString = { it.nama },
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown Buku
        SearchableDropDownMenu(
            options = listBuku,
            label = "Cari & Pilih Buku*",
            selectedOptionLabel = selectedBukuJudul,
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
