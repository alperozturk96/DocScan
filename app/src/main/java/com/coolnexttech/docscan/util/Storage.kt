package com.coolnexttech.docscan.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.appContext
import com.coolnexttech.docscan.ui.scanner.Doc
import com.coolnexttech.docscan.util.extensions.showToast
import com.coolnexttech.docscan.util.extensions.toBitmap
import com.coolnexttech.docscan.util.extensions.toImageBitmap
import java.io.OutputStream


object Storage {
    private const val DIRECTORY = "DocScan"

    fun saveDoc(context: Context, imageUri: Uri) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "DocScan_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/" + DIRECTORY
            )
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return

        val outputStream: OutputStream = context.contentResolver.openOutputStream(uri) ?: return

        try {
            val bitmap = imageUri.toBitmap(context)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            context.showToast(R.string.storage_save_image_to_gallery_success_message)
        } catch (e: Exception) {
            context.showToast(R.string.storage_save_image_to_gallery_error_message)
        } finally {
            outputStream.close()
        }
    }

    fun readDocs(): List<Doc> {
        val context = appContext.get() ?: return listOf()
        val contentResolver = context.contentResolver ?: return listOf()
        val result: ArrayList<Doc> = arrayListOf()

        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%/$DIRECTORY/%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            uriExternal,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val filenameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val filename = cursor.getString(filenameColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)
                val uri = ContentUris.withAppendedId(uriExternal, id)

                val imageBitmap = uri.toImageBitmap(context)
                result.add(Doc(id, filename, imageBitmap, uri, dateModified))
            }
        }

        return result
    }
}

