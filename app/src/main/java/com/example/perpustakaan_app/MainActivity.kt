package com.example.perpustakaan_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.perpustakaan_app.ui.theme.Perpustakaan_appTheme
import com.example.perpustakaan_app.uicontroller.PetaNavigasi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Perpustakaan_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding ->
                    PetaNavigasi(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}