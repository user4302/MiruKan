package com.user4302.presentation.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.user4302.mika.domain.entries.EntryCover
import com.user4302.presentation.library.components.CommonEntryItemDefaults
import com.user4302.presentation.library.components.EntryComfortableGridItem

@Composable
fun GlobalSearchCard(
    title: String,
    cover: EntryCover,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Box(modifier = Modifier.width(96.dp)) {
        EntryComfortableGridItem(
            title = title,
            coverData = cover,
            coverBadgeStart = {
                InLibraryBadge(enabled = isFavorite)
            },
            coverAlpha = if (isFavorite) CommonEntryItemDefaults.BrowseFavoriteCoverAlpha else 1f,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }
}
