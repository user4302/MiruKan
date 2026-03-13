package com.user4302.mika.domain.track.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.anime.repository.AnimeTrackRepository
import logcat.LogPriority

class DeleteAnimeTrack(
    private val trackRepository: AnimeTrackRepository,
) {

    suspend fun await(animeId: Long, trackerId: Long) {
        try {
            trackRepository.delete(animeId, trackerId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
