package com.coolnexttech.docscan.util.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.coolnexttech.docscan.appContext

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

fun List<Uri>.toImageBitmap(): List<ImageBitmap> {
    val context = appContext.get() ?: return listOf()
    val result: ArrayList<ImageBitmap> = arrayListOf()

    forEach {
        result.add(it.toImageBitmap(context))
    }

    return result
}
