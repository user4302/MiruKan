package com.user4302.mika.domain.items.episode.interactor

import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository

class GetEpisodeByUrlAndAnimeId(
    private val episodeRepository: EpisodeRepository,
) {

    suspend fun await(url: String, sourceId: Long): Episode? {
        return try {
            episodeRepository.getEpisodeByUrlAndAnimeId(url, sourceId)
        } catch (e: Exception) {
            null
        }
    }
}
