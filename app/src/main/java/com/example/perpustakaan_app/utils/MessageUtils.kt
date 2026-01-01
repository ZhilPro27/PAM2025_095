package com.example.perpustakaan_app.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class TipePesan {
    SUKSES, ERROR, INFO
}

data class PesanNotifikasi(
    val pesan: String,
    val tipe: TipePesan = TipePesan.INFO
)

@Composable
fun ModernSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        val (containerColor, contentColor) = when (data.visuals.message) {
            "SUKSES" -> Pair(Color(0xFF4CAF50), Color.White)
            "ERROR" -> Pair(Color(0xFFE53935), Color.White)
            else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(12.dp),
            action = {
                data.visuals.actionLabel?.let { actionLabel ->
                    Text(
                        text = actionLabel,
                        color = contentColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        ) {
            Text(text = data.visuals.actionLabel ?: "Notifikasi")
            // Trik: Kita akan membalik penggunaan parameter nanti di ViewModel
            // Agar lebih fleksibel, kita pakai cara standar di bawah ini saja:
        }
    }
}

@Composable
fun WidgetSnackbarKeren(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(hostState,
        modifier = modifier) { data ->
        val isError = data.visuals.message.startsWith("Gagal") || data.visuals.message.contains("Error")
        val isSuccess = data.visuals.message.startsWith("Berhasil") || data.visuals.message.startsWith("Sukses")

        val bgColor = when {
            isError -> MaterialTheme.colorScheme.errorContainer
            isSuccess -> Color(0xFF2E7D32)
            else -> MaterialTheme.colorScheme.inverseSurface
        }

        val txtColor = when {
            isError -> MaterialTheme.colorScheme.onErrorContainer
            isSuccess -> Color.White
            else -> MaterialTheme.colorScheme.inverseOnSurface
        }

        Snackbar(
            containerColor = bgColor,
            contentColor = txtColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}