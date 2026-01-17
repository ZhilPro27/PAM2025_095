package com.example.perpustakaan_app.view.anggota

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.R
import com.example.perpustakaan_app.modeldata.DetailAnggota
import com.example.perpustakaan_app.modeldata.UIStateAnggota
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiTambahAnggota
import com.example.perpustakaan_app.utils.WidgetSnackbarKeren
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.viewmodel.anggota.TambahAnggotaViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahAnggota(
    navigateBack: () -> Unit,
    onSucces: () -> Unit,
    modifier: Modifier = Modifier ,
    viewModel: TambahAnggotaViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
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
                title = DestinasiTambahAnggota.tittleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack,
                modifier = Modifier.background(colorResource(R.color.white))
            )
        }
    ) {innerPadding ->
        BodyTambahAnggota(
            uiStateAnggota = viewModel.uiStateAnggota,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val isSuccess = viewModel.simpanAnggota(context)
                    if (isSuccess) {
                        onSucces()
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(colorResource(R.color.white))
        )
    }
}

@Composable
fun BodyTambahAnggota(
    uiStateAnggota: UIStateAnggota,
    onValueChange: (DetailAnggota) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
            .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormInputAnggota(
            detailAnggota = uiStateAnggota.detailAnggota,
            onValueChange = onValueChange,
        )
        Button(
            onClick = onSaveClick,
            enabled = uiStateAnggota.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.navy),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Text("Simpan Anggota")
        }
    }
}

@Composable
fun FormInputAnggota(
    detailAnggota: DetailAnggota,
    onValueChange: (DetailAnggota) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = detailAnggota.nama,
            onValueChange = { onValueChange(detailAnggota.copy(nama = it)) },
            label = { Text("Nama*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = detailAnggota.no_hp,
            onValueChange = { inputNoHp ->
                if (inputNoHp.all { char -> char.isDigit() }) {
                    if (inputNoHp.length <= 15) {
                        onValueChange(detailAnggota.copy(no_hp = inputNoHp))
                    }
                }
            },
            label = { Text("No HP*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        OutlinedTextField(
            value = detailAnggota.alamat,
            onValueChange = { onValueChange(detailAnggota.copy(alamat = it)) },
            label = { Text("Alamat*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
