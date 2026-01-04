package com.example.perpustakaan_app.view.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropDownMenu(
    options: List<T>,             // Data mentah
    label: String,
    selectedOptionLabel: String,  // Teks dari ViewModel
    onOptionSelected: (T) -> Unit,// Callback saat item dipilih
    itemToString: (T) -> String,  // Konversi Object ke String
    modifier: Modifier = Modifier
) {
    // State untuk kontrol menu (Buka/Tutup)
    var expanded by remember { mutableStateOf(false) }

    // State teks lokal untuk input pengguna
    // Kita inisialisasi dengan nilai dari VM agar saat pertama buka terisi
    var searchQuery by remember { mutableStateOf(selectedOptionLabel) }

    // Sinkronisasi: Jika data di ViewModel berubah (misal setelah simpan/reset),
    // update teks di UI.
    LaunchedEffect(selectedOptionLabel) {
        searchQuery = selectedOptionLabel
    }

    val focusManager = LocalFocusManager.current

    // Filter opsi berdasarkan ketikan
    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) {
            options
        } else {
            options.filter {
                itemToString(it).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            // Logika standar: Toggle expanded saat icon diklik
            expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                // PENTING: Saat mengetik, paksa menu terbuka agar user lihat hasil filter
                expanded = true

                // Opsional: Jika user menghapus teks sampai habis, bisa reset pilihan di VM
                // (Tergantung kebutuhan, disini kita biarkan saja)
            },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor() // Wajib ada agar menu menempel di sini
                .fillMaxWidth(),
            singleLine = true,
            // HAPUS readOnly = true (Agar bisa diketik)
            readOnly = false
        )

        // Menu Dropdown
        // Kita cek if (expanded) agar menu tidak merender jika tertutup
        if (expanded) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 200.dp) // Batasi tinggi agar tidak menutupi layar
            ) {
                if (filteredOptions.isEmpty()) {
                    // Tampilkan info jika tidak ada hasil
                    DropdownMenuItem(
                        text = { Text(text = "Data tidak ditemukan", color = MaterialTheme.colorScheme.error) },
                        onClick = { }
                    )
                } else {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = itemToString(option)) },
                            onClick = {
                                // 1. Update teks jadi nama item
                                searchQuery = itemToString(option)
                                // 2. Tutup menu
                                expanded = false
                                // 3. Lepas fokus keyboard
                                focusManager.clearFocus()
                                // 4. Kirim object terpilih ke ViewModel
                                onOptionSelected(option)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}