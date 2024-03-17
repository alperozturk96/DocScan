package com.coolnexttech.docscan.ui.scanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.getFileName
import com.coolnexttech.docscan.util.extensions.toImageBitmap
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult


@Composable
fun ScannerScreen(activity: ComponentActivity, viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val docs by viewModel.docs.collectAsState()
    var searchText by remember {
        mutableStateOf("")
    }
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
        .padding(16.dp),
        topBar = {
            if (!docs.isNullOrEmpty()) {
                SearchBar(
                    text = searchText,
                    onValueChange = {
                        searchText = it
                    }, clear = {
                        searchText = ""
                    }
                )
            }
        },
        bottomBar = {
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
        }) { padding ->
        if (!docs.isNullOrEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = padding,
            ) {
                items(docs!!) { doc ->
                    DocBox(doc, context)
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

@Composable
private fun DocBox(doc: Uri, context: Context) {
    Column {
        Image(
            bitmap = doc.toImageBitmap(context),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(0.dp, Color.Transparent, RoundedCornerShape(16.dp))
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, doc)
                    startActivity(context, intent, null)
                },
        )

        Text(
            text = context.getFileName(doc) ?: "",
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }
}

@Composable
private fun SearchBar(
    text: String,
    onValueChange: (String) -> Unit,
    clear: () -> Unit
) {
    TextField(
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        value = text,
        onValueChange = {
            onValueChange(it)
        },
        modifier = Modifier
            .fillMaxWidth(),
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        clear()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                    )
                }
            }
        }
    )
}