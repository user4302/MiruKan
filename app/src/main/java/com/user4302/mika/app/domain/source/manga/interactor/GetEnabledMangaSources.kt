package com.user4302.domain.source.manga.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.domain.source.manga.model.Pin
import com.user4302.mika.domain.source.manga.model.Pins
import com.user4302.mika.domain.source.manga.model.Source
import com.user4302.mika.domain.source.manga.repository.MangaSourceRepository
import com.user4302.mika.source.local.entries.manga.LocalMangaSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class GetEnabledMangaSources(
    private val repository: MangaSourceRepository,
    private val preferences: SourcePreferences,
) {

    fun subscribe(): Flow<List<Source>> {
        return combine(
            preferences.pinnedMangaSources().changes(),
            preferences.enabledLanguages().changes(),
            combine(
                preferences.disabledMangaSources().changes(),
                preferences.lastUsedMangaSource().changes(),
                // SY -->
                preferences.dataSaverExcludedSources().changes(),
                // SY <--
            ) { a, b, c -> Triple(a, b, c) },
            repository.getMangaSources(),
        ) { pinnedSourceIds, enabledLanguages, (disabledSources, lastUsedSource, excludedFromDataSaver), sources ->
            sources
                .filter { it.lang in enabledLanguages || it.id == LocalMangaSource.ID }
                .filterNot { it.id.toString() in disabledSources }
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                .flatMap {
                    val flag = if ("${it.id}" in pinnedSourceIds) Pins.pinned else Pins.unpinned
                    val source = it.copy(
                        pin = flag,
                        // SY -->
                        isExcludedFromDataSaver = it.id.toString() in excludedFromDataSaver,
                        // SY <--
                    )
                    val toFlatten = mutableListOf(source)
                    if (source.id == lastUsedSource) {
                        toFlatten.add(source.copy(isUsedLast = true, pin = source.pin - Pin.Actual))
                    }
                    toFlatten
                }
        }
            .distinctUntilChanged()
    }
}
