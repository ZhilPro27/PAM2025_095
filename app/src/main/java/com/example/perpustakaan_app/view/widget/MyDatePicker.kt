package com.example.perpustakaan_app.view.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDateField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = dateFormatter.format(Date(millis))
                            onValueChange(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- PERUBAHAN UTAMA DI SINI ---
    // Gunakan Box untuk menumpuk komponen
    Box(modifier = modifier) {

        // 1. Text Field (Tampilan Visual)
        if (value != null) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                label = { Text(label) },
                readOnly = true, // Tetap readOnly agar keyboard tidak muncul
                trailingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(), // HAPUS .clickable dari sini
                singleLine = true
            )
        }

        // 2. Lapisan Transparan (Penerima Klik)
        // Box ini menutupi seluruh area TextField. Saat diklik, dia yang merespon.
        Box(
            modifier = Modifier
                .matchParentSize() // Ukurannya mengikuti Text Field
                .clickable { showDatePicker = true } // Trigger Date Picker
        )
    }
}