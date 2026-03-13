package com.user4302.mika.app.domain.source.manga.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.domain.source.manga.model.Source

class ToggleExcludeFromMangaDataSaver(
    private val preferences: SourcePreferences,
) {

    fun await(source: Source) {
        preferences.dataSaverExcludedSources().getAndSet {
            if (source.id.toString() in it) {
                it - source.id.toString()
            } else {
                it + source.id.toString()
            }
        }
    }
}
