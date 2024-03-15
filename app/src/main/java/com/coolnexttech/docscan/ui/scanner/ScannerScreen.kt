package com.coolnexttech.docscan.ui.scanner

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.docscan.R
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(activity: ComponentActivity) {
    var startScan by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        FilledTonalButton(onClick = {
            startScan = true
        }, modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(100.dp)) {
            Text(
                text = stringResource(id = (R.string.scanner_screen_scan_button_text)),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (startScan) {
        Scan(activity,
            onComplete = {
                startScan = false
            }, onFail = {
                startScan = false
            }
        )
    }
}