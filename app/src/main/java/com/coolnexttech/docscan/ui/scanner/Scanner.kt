package com.coolnexttech.docscan.ui.scanner

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult


@Composable
fun Scan(
    activity: ComponentActivity,
    onComplete: (GmsDocumentScanningResult.Pdf) -> Unit,
    onFail: () -> Unit
) {
    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(1)
        .setResultFormats(RESULT_FORMAT_PDF)
        .setScannerMode(SCANNER_MODE_FULL)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)

                result?.pdf?.let { pdf ->
                    // TODO Save
                }
            }
        }
    )

    scanner.getStartScanIntent(activity)
        .addOnSuccessListener { intentSender ->
            scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
        .addOnCanceledListener {
            onFail()
        }
        .addOnFailureListener {
            onFail()
        }
}
