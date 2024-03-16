package com.coolnexttech.docscan.util.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(textId: Int) {
    Toast.makeText(
        this,
        getString(textId),
        Toast.LENGTH_SHORT
    ).show()
}