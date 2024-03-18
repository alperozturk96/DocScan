package com.coolnexttech.docscan.util.extensions

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast


fun Context.showToast(textId: Int) {
    Toast.makeText(
        this,
        getString(textId),
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.openUri(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}

fun Context.renameUri(uri: Uri, filename: String) {
    val contentValues = ContentValues()
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        contentResolver.update(uri, contentValues, null)
    } else {
        contentResolver.update(uri, contentValues, null, null)
    }
}
