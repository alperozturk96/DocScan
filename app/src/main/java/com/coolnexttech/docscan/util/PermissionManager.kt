package com.coolnexttech.docscan.util

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker


class PermissionManager(private val activity: ComponentActivity, private val onComplete: () -> Unit) {

    private val storagePermissionCode = 44

    private val requestPermissions =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results[READ_MEDIA_VISUAL_USER_SELECTED] == true || results[READ_MEDIA_IMAGES] == true) {
                onComplete()
            }
        }

    fun requestForStoragePermissions() {
        if (checkStoragePermissions()) {
            onComplete()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_VISUAL_USER_SELECTED, READ_MEDIA_IMAGES))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES))
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                storagePermissionCode
            )
        }
    }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val mediaVisualUserSelectedPermission = ContextCompat.checkSelfPermission(
                activity, READ_MEDIA_VISUAL_USER_SELECTED
            ) == PermissionChecker.PERMISSION_GRANTED

            val readMediaImagesPermission = ContextCompat.checkSelfPermission(
                activity, READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED

            return mediaVisualUserSelectedPermission || readMediaImagesPermission
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity, READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            val writePermission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
            val readPermission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED

            return writePermission && readPermission
        }
    }
}
