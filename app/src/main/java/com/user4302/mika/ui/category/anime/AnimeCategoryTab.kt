package com.user4302.mika.ui.category.anime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.presentation.category.AnimeCategoryScreen
import com.user4302.presentation.category.components.CategoryCreateDialog
import com.user4302.presentation.category.components.CategoryDeleteDialog
import com.user4302.presentation.category.components.CategoryRenameDialog
import com.user4302.presentation.components.TabContent
import kotlinx.collections.immutable.toImmutableList

@Composable
fun Screen.animeCategoryTab(): TabContent {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = rememberScreenModel { AnimeCategoryScreenModel() }

    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = AYMR.strings.label_anime,
        searchEnabled = false,
        content = { contentPadding, _ ->
            if (state is AnimeCategoryScreenState.Loading) {
                LoadingScreen()
            } else {
                val successState = state as AnimeCategoryScreenState.Success

                AnimeCategoryScreen(
                    state = successState,
                    onClickCreate = { screenModel.showDialog(AnimeCategoryDialog.Create) },
                    onClickRename = { screenModel.showDialog(AnimeCategoryDialog.Rename(it)) },
                    onClickHide = screenModel::hideCategory,
                    onClickDelete = { screenModel.showDialog(AnimeCategoryDialog.Delete(it)) },
                    onChangeOrder = screenModel::changeOrder,
                )

                when (val dialog = successState.dialog) {
                    null -> {}
                    AnimeCategoryDialog.Create -> {
                        CategoryCreateDialog(
                            onDismissRequest = screenModel::dismissDialog,
                            onCreate = screenModel::createCategory,
                            categories = successState.categories.fastMap { it.name }.toImmutableList(),
                        )
                    }
                    is AnimeCategoryDialog.Rename -> {
                        CategoryRenameDialog(
                            onDismissRequest = screenModel::dismissDialog,
                            onRename = { screenModel.renameCategory(dialog.category, it) },
                            categories = successState.categories.fastMap { it.name }.toImmutableList(),
                            category = dialog.category.name,
                        )
                    }
                    is AnimeCategoryDialog.Delete -> {
                        CategoryDeleteDialog(
                            onDismissRequest = screenModel::dismissDialog,
                            onDelete = { screenModel.deleteCategory(dialog.category.id) },
                            category = dialog.category.name,
                        )
                    }
                }
            }
        },
        navigateUp = navigator::pop,
    )
}
