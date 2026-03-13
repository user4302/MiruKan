package com.user4302.presentation.more.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.category.visualName

/**
 * Returns a string of categories name for settings subtitle
 */
@ReadOnlyComposable
@Composable
fun getCategoriesLabel(
    allCategories: List<Category>,
    included: Set<String>,
    excluded: Set<String>,
): String {
    val context = LocalContext.current

    val includedCategories = included
        .mapNotNull { id -> allCategories.find { it.id == id.toLong() } }
        .sortedBy { it.order }
    val excludedCategories = excluded
        .mapNotNull { id -> allCategories.find { it.id == id.toLong() } }
        .sortedBy { it.order }
    val allExcluded = excludedCategories.size == allCategories.size

    val includedItemsText = when {
        // Some selected, but not all
        includedCategories.isNotEmpty() &&
            includedCategories.size != allCategories.size ->
            includedCategories.joinToString {
                it.visualName(
                    context,
                )
            }
        // All explicitly selected
        includedCategories.size == allCategories.size -> stringResource(AYMR.strings.all)
        allExcluded -> stringResource(AYMR.strings.none)
        else -> stringResource(AYMR.strings.all)
    }
    val excludedItemsText = when {
        excludedCategories.isEmpty() -> stringResource(AYMR.strings.none)
        allExcluded -> stringResource(AYMR.strings.all)
        else -> excludedCategories.joinToString { it.visualName(context) }
    }
    return stringResource(AYMR.strings.include, includedItemsText) + "\n" +
        stringResource(AYMR.strings.exclude, excludedItemsText)
}
