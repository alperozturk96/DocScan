package com.coolnexttech.docscan

import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

    private lateinit var onBackInvokedCallback: OnBackInvokedCallback
    private lateinit var permissionManager: PermissionManager
    private val viewModel = ScannerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleBackPress()

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

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.unregisterOnBackInvokedCallback(onBackInvokedCallback)
        }
    }

    private fun handleBackPress() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedCallback = OnBackInvokedCallback {
                finish()
            }

            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                onBackInvokedCallback
            )
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }
}