package com.user4302.mika.domain.items.season.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.anime.SeasonAnime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import logcat.LogPriority

class GetAnimeSeasonsByParentId(
    private val animeRepository: AnimeRepository,
) {
    suspend fun await(animeId: Long): List<SeasonAnime> {
        return try {
            animeRepository.getAnimeSeasonsById(animeId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }
}
