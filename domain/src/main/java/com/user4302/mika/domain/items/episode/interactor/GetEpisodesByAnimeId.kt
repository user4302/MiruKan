package com.user4302.mika.domain.items.episode.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository
import logcat.LogPriority

class GetEpisodesByAnimeId(
    private val episodeRepository: EpisodeRepository,
) {

    suspend fun await(animeId: Long): List<Episode> {
        return try {
            episodeRepository.getEpisodeByAnimeId(animeId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }
}
