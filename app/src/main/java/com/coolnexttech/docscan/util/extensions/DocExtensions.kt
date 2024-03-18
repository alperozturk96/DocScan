package com.coolnexttech.docscan.util.extensions

import com.coolnexttech.docscan.ui.scanner.model.Doc
import com.coolnexttech.docscan.ui.scanner.model.SortOptions

fun List<Doc>.sort(sortOption: SortOptions): List<Doc> {
    return when(sortOption) {
        SortOptions.AToZ -> sortedBy { it.filename }
        SortOptions.ZToA -> sortedByDescending { it.filename }
        SortOptions.OldToNew -> sortedBy { it.dateModified }
        SortOptions.NewToOld -> sortedByDescending { it.dateModified }
    }
}
