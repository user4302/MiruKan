package com.user4302.presentation.more.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.SelectItem
import com.user4302.mika.presentation.core.i18n.stringResource

@Composable
fun SelectStorageCategory(
    selectedCategory: Category,
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategorySelected: (Category) -> Unit,
) {
    val all = stringResource(AYAYMR.strings.label_all)
    val default = stringResource(AYMR.strings.label_default)
    val mappedCategories = remember(categories) {
        categories.map {
            when (it.id) {
                -1L -> it.copy(name = all)
                Category.UNCATEGORIZED_ID -> it.copy(name = default)
                else -> it
            }
        }.toTypedArray()
    }

    SelectItem(
        modifier = modifier,
        label = stringResource(AYAYMR.strings.label_category),
        selectedIndex = mappedCategories.indexOfFirst { it.id == selectedCategory.id },
        options = mappedCategories,
        onSelect = { index ->
            onCategorySelected(mappedCategories[index])
        },
        toString = { it.name },
    )
}
