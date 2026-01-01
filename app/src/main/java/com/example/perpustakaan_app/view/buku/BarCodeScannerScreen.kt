package com.example.perpustakaan_app.view.buku

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    onIsbnScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraPreview(onIsbnScanned)
        // Tombol Batal
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                Text("Batal Scan")
            }
        }
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Izin kamera diperlukan untuk scan ISBN")
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(onIsbnFound: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview Logic
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Image Analysis (ML Kit) Logic
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val image = imageProxy.image

                    if (image != null) {
                        val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
                        val scanner = BarcodeScanning.getClient()

                        scanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { value ->
                                        // Cek apakah ISBN (biasanya 13 digit atau 10 digit)
                                        // Kita kirim data apapun yang discan, validasi nanti di form
                                        onIsbnFound(value)
                                        // Stop kamera setelah dapat 1 hasil agar tidak spam
                                        cameraProvider.unbindAll()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Scanner", "Error scanning", it)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    Log.e("Scanner", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}