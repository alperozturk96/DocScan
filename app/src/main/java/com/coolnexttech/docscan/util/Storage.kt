package com.coolnexttech.docscan.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.util.extensions.showToast
import com.coolnexttech.docscan.util.extensions.toBitmap
import java.io.OutputStream


object Storage {
    fun saveToGallery(context: Context, imageUri: Uri) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "DocScan_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DocScan")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return

        val outputStream: OutputStream = context.contentResolver.openOutputStream(uri) ?: return

        try {
            val bitmap = imageUri.toBitmap(context) ?: return
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            context.showToast(R.string.storage_save_image_to_gallery_success_message)
        } catch (e: Exception) {
            context.showToast(R.string.storage_save_image_to_gallery_error_message)
        } finally {
            outputStream.close()
        }
    }
}

