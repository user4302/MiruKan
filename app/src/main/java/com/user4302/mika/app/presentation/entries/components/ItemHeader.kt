package com.user4302.presentation.entries.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.user4302.mika.animesource.model.FetchType
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.material.SECONDARY_ALPHA
import com.user4302.mika.presentation.core.components.material.padding
import com.user4302.mika.presentation.core.i18n.pluralStringResource
import com.user4302.mika.presentation.core.i18n.stringResource

@Composable
fun ItemHeader(
    enabled: Boolean,
    itemCount: Int?,
    missingItemsCount: Int,
    onClick: () -> Unit,
    isManga: Boolean,
    modifier: Modifier = Modifier,
    fetchType: FetchType = FetchType.Episodes,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
    ) {
        Text(
            text = if (itemCount == null) {
                val count = if (isManga) AYMR.strings.chapters else AYAYMR.strings.episodes
                stringResource(count)
            } else {
                val pluralCount = if (isManga) {
                    AYMR.plurals.manga_num_chapters
                } else {
                    when (fetchType) {
                        FetchType.Seasons -> AYAYMR.plurals.anime_num_seasons
                        FetchType.Episodes -> AYAYMR.plurals.anime_num_episodes
                    }
                }
                pluralStringResource(pluralCount, count = itemCount, itemCount)
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        MissingItemsWarning(missingItemsCount)
    }
}

@Composable
private fun MissingItemsWarning(count: Int) {
    if (count == 0) {
        return
    }

    Text(
        text = pluralStringResource(AYAYMR.plurals.missing_items, count = count, count),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error.copy(alpha = SECONDARY_ALPHA),
    )
}
