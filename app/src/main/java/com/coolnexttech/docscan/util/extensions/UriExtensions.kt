package com.coolnexttech.docscan.util.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun Uri.toBitmap(context: Context): Bitmap {
    return ImageDecoder.decodeBitmap(
        ImageDecoder.createSource(
            context.contentResolver,
            this
        )
    )
}

fun Uri.toImageBitmap(context: Context): ImageBitmap {
    return toBitmap(context).asImageBitmap()
}
