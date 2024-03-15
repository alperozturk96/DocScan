package com.coolnexttech.docscan.ui.scanner

import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning


@Composable
fun Scan(
    activity: ComponentActivity,
    start: (IntentSender) -> Unit
) {
    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(1)
        .setResultFormats(RESULT_FORMAT_PDF)
        .setScannerMode(SCANNER_MODE_FULL)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)

    scanner.getStartScanIntent(activity)
        .addOnSuccessListener { intentSender ->
            start(intentSender)
        }
}
