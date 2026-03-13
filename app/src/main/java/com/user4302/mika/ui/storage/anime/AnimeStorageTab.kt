package com.user4302.mika.ui.storage.anime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.i18n.MR
import com.user4302.presentation.components.TabContent
import com.user4302.presentation.more.storage.StorageScreenContent

@Composable
fun Screen.animeStorageTab(): TabContent {
    val navigator = LocalNavigator.currentOrThrow

    val screenModel = rememberScreenModel { AnimeStorageScreenModel() }
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = MR.strings.manga,
        content = { contentPadding, _ ->
            StorageScreenContent(
                state = state,
                isManga = false,
                contentPadding = contentPadding,
                onCategorySelected = screenModel::setSelectedCategory,
                onDelete = screenModel::deleteEntry,
            )
        },
        navigateUp = navigator::pop,
    )
}
