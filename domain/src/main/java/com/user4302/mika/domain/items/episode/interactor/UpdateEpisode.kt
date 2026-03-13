package com.user4302.mika.domain.items.episode.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.items.episode.model.EpisodeUpdate
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository
import logcat.LogPriority

class UpdateEpisode(
    private val episodeRepository: EpisodeRepository,
) {

    suspend fun await(episodeUpdate: EpisodeUpdate) {
        try {
            episodeRepository.updateEpisode(episodeUpdate)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    suspend fun awaitAll(episodeUpdates: List<EpisodeUpdate>) {
        try {
            episodeRepository.updateAllEpisodes(episodeUpdates)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
