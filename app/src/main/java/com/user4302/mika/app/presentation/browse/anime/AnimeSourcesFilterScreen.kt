package com.user4302.presentation.browse.anime

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.user4302.mika.domain.source.anime.model.AnimeSource
import com.user4302.mika.presentation.core.components.FastScrollLazyColumn
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.screens.EmptyScreen
import com.user4302.mika.ui.browse.anime.source.AnimeSourcesFilterScreenModel
import com.user4302.mika.util.system.LocaleHelper
import com.user4302.presentation.browse.anime.components.BaseAnimeSourceItem
import com.user4302.presentation.components.AppBar
import com.user4302.presentation.more.settings.widget.SwitchPreferenceWidget
import com.user4302.presentation.util.animateItemFastScroll

@Composable
fun AnimeSourcesFilterScreen(
    navigateUp: () -> Unit,
    state: AnimeSourcesFilterScreenModel.State.Success,
    onClickLanguage: (String) -> Unit,
    onClickSource: (AnimeSource) -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(AYMR.strings.label_sources),
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        if (state.isEmpty) {
            EmptyScreen(
                stringRes = AYMR.strings.source_filter_empty_screen,
                modifier = Modifier.padding(contentPadding),
            )
            return@Scaffold
        }
        AnimeSourcesFilterContent(
            contentPadding = contentPadding,
            state = state,
            onClickLanguage = onClickLanguage,
            onClickSource = onClickSource,
        )
    }
}

@Composable
private fun AnimeSourcesFilterContent(
    contentPadding: PaddingValues,
    state: AnimeSourcesFilterScreenModel.State.Success,
    onClickLanguage: (String) -> Unit,
    onClickSource: (AnimeSource) -> Unit,
) {
    FastScrollLazyColumn(
        contentPadding = contentPadding,
    ) {
        state.items.forEach { (language, sources) ->
            val enabled = language in state.enabledLanguages
            item(
                key = language,
                contentType = "source-filter-header",
            ) {
                AnimeSourcesFilterHeader(
                    modifier = Modifier.animateItemFastScroll(),
                    language = language,
                    enabled = enabled,
                    onClickItem = onClickLanguage,
                )
            }
            if (enabled) {
                items(
                    items = sources,
                    key = { "source-filter-${it.key()}" },
                    contentType = { "source-filter-item" },
                ) { source ->
                    AnimeSourcesFilterItem(
                        modifier = Modifier.animateItemFastScroll(),
                        source = source,
                        isEnabled = "${source.id}" !in state.disabledSources,
                        onClickItem = onClickSource,
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeSourcesFilterHeader(
    language: String,
    enabled: Boolean,
    onClickItem: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SwitchPreferenceWidget(
        modifier = modifier,
        title = LocaleHelper.getSourceDisplayName(language, LocalContext.current),
        checked = enabled,
        onCheckedChanged = { onClickItem(language) },
    )
}

@Composable
private fun AnimeSourcesFilterItem(
    source: AnimeSource,
    isEnabled: Boolean,
    onClickItem: (AnimeSource) -> Unit,
    modifier: Modifier = Modifier,
) {
    BaseAnimeSourceItem(
        modifier = modifier,
        source = source,
        showLanguageInContent = false,
        onClickItem = { onClickItem(source) },
        action = {
            Checkbox(checked = isEnabled, onCheckedChange = null)
        },
    )
}
