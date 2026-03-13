package com.user4302.domain.source.anime.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.domain.source.anime.model.AnimeSource

class ToggleAnimeSourcePin(
    private val preferences: SourcePreferences,
) {

    fun await(source: AnimeSource) {
        val isPinned = source.id.toString() in preferences.pinnedAnimeSources().get()
        preferences.pinnedAnimeSources().getAndSet { pinned ->
            if (isPinned) pinned.minus("${source.id}") else pinned.plus("${source.id}")
        }
    }
}
