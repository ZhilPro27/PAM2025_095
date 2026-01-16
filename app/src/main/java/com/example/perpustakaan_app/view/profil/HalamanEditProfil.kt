package com.example.perpustakaan_app.view.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perpustakaan_app.modeldata.DetailProfil
import com.example.perpustakaan_app.modeldata.UIStateProfil
import com.example.perpustakaan_app.uicontroller.route.profil.DestinasiEditProfil
import com.example.perpustakaan_app.view.PerpustakaanTopAppBar
import com.example.perpustakaan_app.viewmodel.profil.EditProfilViewModel
import com.example.perpustakaan_app.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditProfil(
    navigateBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfilViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.pesanChannel.collect { pesan ->
            snackbarHostState.showSnackbar(pesan)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ){
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                PerpustakaanTopAppBar(
                    title = DestinasiEditProfil.titleRes,
                    canNavigateBack = true,
                    scrollBehavior = scrollBehavior,
                    navigateUp = navigateBack
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ){
            innerPadding ->
            BodyEditProfil(
                uiStateProfil = viewModel.uiStateProfil,
                onValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        val isSuccess = viewModel.updateProfil(context)
                        if (isSuccess) {
                            onSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun BodyEditProfil(
    uiStateProfil: UIStateProfil,
    onValueChange: (DetailProfil) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormInputProfil(
            detailProfil = uiStateProfil.detailProfil,
            onValueChange = onValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = uiStateProfil.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Profil")
        }
    }
}

@Composable
fun FormInputProfil(
    modifier: Modifier = Modifier,
    detailProfil: DetailProfil,
    onValueChange: (DetailProfil) -> Unit
){
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = detailProfil.email,
            onValueChange = { onValueChange(detailProfil.copy(email = it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = detailProfil.password ?: "",
            onValueChange = { onValueChange(detailProfil.copy(password = it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}