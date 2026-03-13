package com.user4302.domain.source.anime.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.domain.source.anime.model.AnimeSource

class ToggleAnimeSource(
    private val preferences: SourcePreferences,
) {

    fun await(source: AnimeSource, enable: Boolean = isEnabled(source.id)) {
        await(source.id, enable)
    }

    fun await(sourceId: Long, enable: Boolean = isEnabled(sourceId)) {
        preferences.disabledAnimeSources().getAndSet { disabled ->
            if (enable) disabled.minus("$sourceId") else disabled.plus("$sourceId")
        }
    }

    fun await(sourceIds: List<Long>, enable: Boolean) {
        val transformedSourceIds = sourceIds.map { it.toString() }
        preferences.disabledAnimeSources().getAndSet { disabled ->
            if (enable) {
                disabled.minus(transformedSourceIds)
            } else {
                disabled.plus(
                    transformedSourceIds,
                )
            }
        }
    }

    private fun isEnabled(sourceId: Long): Boolean {
        return sourceId.toString() in preferences.disabledAnimeSources().get()
    }
}
