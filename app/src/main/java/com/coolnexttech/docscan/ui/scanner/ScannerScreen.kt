package com.coolnexttech.docscan.ui.scanner

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.toImageBitmap
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
                        viewModel.fetchDocs()
                    }
                }
            }
        }
    )

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), bottomBar = {
        FilledTonalButton(
            onClick = {
                startScan = true
            }, modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Text(
                text = stringResource(id = (R.string.scanner_screen_scan_button_text)),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }) {
        if (docs != null && docs?.isNotEmpty() == true) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = it,
            ) {
                items(docs!!) {
                    Image(
                        bitmap = it.toImageBitmap(context),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(0.dp, Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, it)
                                startActivity(context, intent, null)
                            },
                    )
                }
            }
        } else {
            Text(
                text = stringResource(id = R.string.scanner_screen_empty_text),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (startScan) {
        Scan(activity, start = {
            scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
        })
    }
}