package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import logcat.LogPriority

class SetAnimeCategories(
    private val animeRepository: AnimeRepository,
) {

    suspend fun await(animeId: Long, categoryIds: List<Long>) {
        try {
            animeRepository.setAnimeCategories(animeId, categoryIds)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
