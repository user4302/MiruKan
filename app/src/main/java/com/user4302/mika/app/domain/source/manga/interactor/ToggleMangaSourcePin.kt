package com.user4302.domain.source.manga.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.domain.source.manga.model.Source

class ToggleMangaSourcePin(
    private val preferences: SourcePreferences,
) {

    fun await(source: Source) {
        val isPinned = source.id.toString() in preferences.pinnedMangaSources().get()
        preferences.pinnedMangaSources().getAndSet { pinned ->
            if (isPinned) pinned.minus("${source.id}") else pinned.plus("${source.id}")
        }
    }
}
