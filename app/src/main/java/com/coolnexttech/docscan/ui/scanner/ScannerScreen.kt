package com.coolnexttech.docscan.ui.scanner

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.util.Storage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@Composable
fun ScannerScreen(activity: ComponentActivity, viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val docs by viewModel.docs.collectAsState()
    var startScan by remember {
        mutableStateOf(false)
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            startScan = false
            if (it.resultCode == Activity.RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)

                result?.pages?.let { pages ->
                    for (page in pages) {
                        val imageUri = page.imageUri

                        Storage.saveDoc(context, imageUri)
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (docs != null) {
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(docs!!) {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(1f)
                    )
                }
            }
        } else {
            Text(
                text = stringResource(id = R.string.scanner_screen_empty_text),
                style = MaterialTheme.typography.titleLarge
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        FilledTonalButton(
            onClick = {
                startScan = true
            }, modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(100.dp)
        ) {
            Text(
                text = stringResource(id = (R.string.scanner_screen_scan_button_text)),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (startScan) {
        Scan(activity, start = {
            scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
        })
    }
}