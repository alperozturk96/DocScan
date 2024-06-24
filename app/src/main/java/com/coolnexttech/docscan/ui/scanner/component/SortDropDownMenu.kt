package com.coolnexttech.docscan.ui.scanner.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.ui.scanner.ScannerViewModel
import com.coolnexttech.docscan.ui.scanner.model.SortOptions


@Composable
fun SortDropDownMenu(viewModel: ScannerViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOptions.NewToOld) }

    LaunchedEffect(sortOption) {
        expanded = false
        viewModel.sort(sortOption)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.scanner_screen_filter_a_to_z)) },
                onClick = {
                    sortOption = SortOptions.AToZ
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.scanner_screen_filter_z_to_a)) },
                onClick = {
                    sortOption = SortOptions.ZToA
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.scanner_screen_filter_new_to_old)) },
                onClick = {
                    sortOption = SortOptions.NewToOld
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.scanner_screen_filter_old_to_new)) },
                onClick = {
                    sortOption = SortOptions.OldToNew
                }
            )
        }
    }
}
