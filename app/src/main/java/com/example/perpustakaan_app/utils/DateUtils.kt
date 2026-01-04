package com.example.perpustakaan_app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Fungsi untuk mengubah format Server (UTC) ke Lokal (WIB)
@RequiresApi(Build.VERSION_CODES.O)
fun konversiTanggalServerKeLokal(tanggalServer: String?): String? {
    // Cek jika string kosong
    if (tanggalServer?.isBlank() == true || tanggalServer?.isEmpty() == true) return ""

    return try {
        // 1. Parse waktu dari server (UTC)
        // Contoh input: "2026-01-03T17:00:00.000Z"
        val instant = Instant.parse(tanggalServer)

        // 2. Dapatkan Zona Waktu HP User (Otomatis mendeteksi WIB/WITA/WIT)
        val zonaWaktuLokal = ZoneId.systemDefault()

        // 3. Konversi UTC ke Lokal
        val waktuLokal = instant.atZone(zonaWaktuLokal)

        // 4. Format menjadi String yang diinginkan (misal: dd/MM/yyyy atau yyyy-MM-dd)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        // Hasil: "2026-01-04"
        waktuLokal.format(formatter)
    } catch (e: Exception) {
        // Jika gagal parsing (misal format tidak sesuai), kembalikan string aslinya atau kosong
        tanggalServer
    }
}