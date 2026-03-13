package com.user4302.domain.extension.anime.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.extension.anime.model.AnimeExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAnimeExtensionSources(
    private val preferences: SourcePreferences,
) {

    fun subscribe(extension: AnimeExtension.Installed): Flow<List<AnimeExtensionSourceItem>> {
        val isMultiSource = extension.sources.size > 1
        val isMultiLangSingleSource =
            isMultiSource && extension.sources.map { it.name }.distinct().size == 1

        return preferences.disabledAnimeSources().changes().map { disabledSources ->
            fun AnimeSource.isEnabled() = id.toString() !in disabledSources

            extension.sources
                .map { source ->
                    AnimeExtensionSourceItem(
                        source = source,
                        enabled = source.isEnabled(),
                        labelAsName = isMultiSource && !isMultiLangSingleSource,
                    )
                }
        }
    }
}

data class AnimeExtensionSourceItem(
    val source: AnimeSource,
    val enabled: Boolean,
    val labelAsName: Boolean,
)
