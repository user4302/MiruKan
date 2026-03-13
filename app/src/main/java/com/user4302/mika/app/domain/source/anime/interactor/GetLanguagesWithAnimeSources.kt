package com.user4302.domain.source.anime.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.domain.source.anime.model.AnimeSource
import com.user4302.mika.domain.source.anime.repository.AnimeSourceRepository
import com.user4302.mika.util.system.LocaleHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.SortedMap

class GetLanguagesWithAnimeSources(
    private val repository: AnimeSourceRepository,
    private val preferences: SourcePreferences,
) {

    fun subscribe(): Flow<SortedMap<String, List<AnimeSource>>> {
        return combine(
            preferences.enabledLanguages().changes(),
            preferences.disabledAnimeSources().changes(),
            repository.getOnlineAnimeSources(),
        ) { enabledLanguage, disabledSource, onlineSources ->
            val sortedSources = onlineSources.sortedWith(
                compareBy<AnimeSource> { it.id.toString() in disabledSource }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name },
            )

            sortedSources
                .groupBy { it.lang }
                .toSortedMap(
                    compareBy<String> { it !in enabledLanguage }.then(LocaleHelper.comparator),
                )
        }
    }
}
