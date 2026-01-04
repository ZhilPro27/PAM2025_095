package com.example.perpustakaan_app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Fungsi Ekstensi untuk String (Bisa Null)
@RequiresApi(Build.VERSION_CODES.O)
fun String?.formatTanggal(): String {
    if (this.isNullOrEmpty()) return "-"

    return try {
        val parsedDate = ZonedDateTime.parse(this.take(10))

        // Format output yang diinginkan (Contoh: "dd/MM/yyyy")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())

        parsedDate.format(formatter)

    } catch (e: Exception) {
        this.take(10)
    }
}