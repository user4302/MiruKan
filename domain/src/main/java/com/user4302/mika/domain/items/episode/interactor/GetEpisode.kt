package com.user4302.mika.domain.items.episode.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository
import logcat.LogPriority

class GetEpisode(
    private val episodeRepository: EpisodeRepository,
) {

    suspend fun await(id: Long): Episode? {
        return try {
            episodeRepository.getEpisodeById(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }

    suspend fun await(url: String, animeId: Long): Episode? {
        return try {
            episodeRepository.getEpisodeByUrlAndAnimeId(url, animeId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }
}
