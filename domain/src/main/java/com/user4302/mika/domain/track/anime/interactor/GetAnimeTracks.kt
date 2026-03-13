package com.user4302.mika.domain.track.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.anime.model.AnimeTrack
import com.user4302.mika.domain.track.anime.repository.AnimeTrackRepository
import kotlinx.coroutines.flow.Flow
import logcat.LogPriority

class GetAnimeTracks(
    private val animetrackRepository: AnimeTrackRepository,
) {

    suspend fun awaitOne(id: Long): AnimeTrack? {
        return try {
            animetrackRepository.getTrackByAnimeId(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }

    suspend fun await(animeId: Long): List<AnimeTrack> {
        return try {
            animetrackRepository.getTracksByAnimeId(animeId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }

    fun subscribe(animeId: Long): Flow<List<AnimeTrack>> {
        return animetrackRepository.getTracksByAnimeIdAsFlow(animeId)
    }
}
