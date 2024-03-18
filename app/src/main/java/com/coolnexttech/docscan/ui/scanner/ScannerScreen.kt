package com.coolnexttech.docscan.ui.scanner

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coolnexttech.docscan.R
import com.coolnexttech.docscan.ui.component.MoreActionsBottomSheet
import com.coolnexttech.docscan.ui.component.SimpleAlertDialog
import com.coolnexttech.docscan.ui.scanner.model.Doc
import com.coolnexttech.docscan.ui.scanner.model.SortOptions
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.openUri
import com.coolnexttech.docscan.util.extensions.renameUri
import com.coolnexttech.docscan.util.extensions.showToast
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(activity: ComponentActivity, viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val filteredDocs by viewModel.filteredDocs.collectAsState()
    var searchText by remember {
        mutableStateOf("")
    }
    var startScan by remember {
        mutableStateOf(false)
    }
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.fetchDocs(onCompleted = {
                pullRefreshState.endRefresh()
                context.showToast(R.string.scanner_screen_documents_fetched)
            })
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            startScan = false

            if (it.resultCode == Activity.RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)

                result?.pages?.let { pages ->
                    for (page in pages) {
                        val imageUri = page.imageUri

                        Storage.saveDoc(context, imageUri)
                        viewModel.fetchDocs()
                    }
                }
            }
        }
    )

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection))  {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            topBar = {
                SearchBar(
                    sortDropDownMenu = {
                        SortDropDownMenu(viewModel)
                    },
                    text = searchText,
                    onValueChange = {
                        searchText = it
                        viewModel.search(it)
                    }, clear = {
                        searchText = ""
                        viewModel.search(searchText)
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                        )
                ) {
                    FilledTonalButton(
                        onClick = {
                            startScan = true
                        }, modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .align(Alignment.Center)
                    ) {
                        Text(
                            text = stringResource(id = (R.string.scanner_screen_scan_button_text)),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }

            }) { padding ->
            if (!filteredDocs.isNullOrEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.padding(16.dp),
                    contentPadding = padding,
                ) {
                    items(filteredDocs!!) { doc ->
                        DocBox(doc, context, viewModel)
                    }
                }
            } else {
                val text = if (searchText.isEmpty()) {
                    stringResource(id = R.string.scanner_screen_empty_text)
                } else {
                    stringResource(id = R.string.scanner_screen_empty_search, searchText)
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (pullRefreshState.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            LinearProgressIndicator(
                progress = { pullRefreshState.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (startScan) {
        Scan(activity, start = {
            // TODO after edit not showing edited docs
            scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DocBox(doc: Doc, context: Context, viewModel: ScannerViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showRenameAlertDialog by remember { mutableStateOf(false) }
    var docName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(0.dp, Color.Transparent, RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { context.openUri(doc.uri) },
                onLongClick = { showBottomSheet = true }
            )
    ) {
        Image(
            bitmap = doc.imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
        )

        Text(
            text = doc.filename,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }

    if (showRenameAlertDialog) {
        SimpleAlertDialog(
            titleId = R.string.scanner_screen_rename_alert_dialog_title,
            description = null,
            content = {
                TextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = docName,
                    onValueChange = {
                        docName = it
                    },
                    singleLine = true
                )
            },
            onComplete = {
                context.renameUri(doc.uri, docName)
                viewModel.fetchDocs()
            },
            dismiss = {
                showRenameAlertDialog = false
            }
        )
    }

    if (showBottomSheet) {
        val bottomSheetAction = listOf(
            Triple(
                R.drawable.ic_edit,
                R.string.scanner_screen_more_action_bottom_sheet_edit
            ) {
                context.openUri(doc.uri)
            },
            Triple(
                R.drawable.ic_rename,
                R.string.scanner_screen_more_action_bottom_sheet_rename
            ) {
                showRenameAlertDialog = true
            }
        )

        MoreActionsBottomSheet(
            title = doc.filename,
            actions = bottomSheetAction,
            dismiss = { showBottomSheet = false }
        )
    }
}

@Composable
private fun SortDropDownMenu(viewModel: ScannerViewModel) {
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

@Composable
private fun SearchBar(
    sortDropDownMenu: @Composable RowScope.() -> Unit,
    text: String,
    onValueChange: (String) -> Unit,
    clear: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {

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