package com.coolnexttech.docscan.ui.scanner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coolnexttech.docscan.R

@Composable
fun SearchBar(
    sortDropDownMenu: @Composable RowScope.() -> Unit,
    text: String,
    onValueChange: (String) -> Unit,
    clear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Black.copy(alpha = 0.5f),
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            value = text,
            placeholder = {
                Text(text = stringResource(id = R.string.scanner_screen_search_bar_placeholder))
            },
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier
                .fillMaxWidth(0.85f),
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            clear()
                        }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                        )
                    }
                }
            }
        )

        sortDropDownMenu()
    }
}