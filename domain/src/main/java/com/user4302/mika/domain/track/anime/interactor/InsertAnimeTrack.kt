package com.user4302.mika.domain.track.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.anime.model.AnimeTrack
import com.user4302.mika.domain.track.anime.repository.AnimeTrackRepository
import logcat.LogPriority

class InsertAnimeTrack(
    private val animetrackRepository: AnimeTrackRepository,
) {

    suspend fun await(track: AnimeTrack) {
        try {
            animetrackRepository.insertAnime(track)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    suspend fun awaitAll(tracks: List<AnimeTrack>) {
        try {
            animetrackRepository.insertAllAnime(tracks)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
