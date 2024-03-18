package com.coolnexttech.docscan.ui.scanner.model

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class Doc(
    val id: Long,
    val filename: String,
    val imageBitmap: ImageBitmap,
    val uri: Uri,
    val dateModified: Long
)