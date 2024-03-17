package com.coolnexttech.docscan.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.docscan.R

@Composable
fun SimpleAlertDialog(
    titleId: Int,
    description: String?,
    content: @Composable (() -> Unit)? = null,
    onComplete: () -> Unit,
    dismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        title = {
            Text(text = stringResource(id = titleId), color = Color.Black)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (description != null) {
                    Text(text = description, color = Color.Black)
                }

                content?.let {
                    Spacer(modifier = Modifier.height(16.dp))

                    content()
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onComplete()
                    dismiss()
                }
            ) {
                Text(
                    stringResource(id = R.string.common_ok),
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.common_cancel),
                    color = Color.Black
                )
            }
        }
    )
}
