package com.coolnexttech.docscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.coolnexttech.docscan.ui.scanner.ScannerScreen
import com.coolnexttech.docscan.ui.scanner.ScannerViewModel
import com.coolnexttech.docscan.ui.theme.DocScanTheme
import com.coolnexttech.docscan.util.PermissionManager

class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private val viewModel = ScannerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this, onComplete = {
            viewModel.readDocs()
        })

        setContent {
            LaunchedEffect(Unit) {
                permissionManager.requestForStoragePermissions()
            }

            DocScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScannerScreen(activity = this, viewModel = viewModel)
                }
            }
        }
    }
}