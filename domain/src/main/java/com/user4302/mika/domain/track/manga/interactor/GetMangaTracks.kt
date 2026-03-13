package com.user4302.mika.domain.track.manga.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.manga.model.MangaTrack
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import kotlinx.coroutines.flow.Flow
import logcat.LogPriority

class GetMangaTracks(
    private val trackRepository: MangaTrackRepository,
) {

    suspend fun awaitOne(id: Long): MangaTrack? {
        return try {
            trackRepository.getTrackByMangaId(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }

    suspend fun await(mangaId: Long): List<MangaTrack> {
        return try {
            trackRepository.getTracksByMangaId(mangaId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }

    fun subscribe(mangaId: Long): Flow<List<MangaTrack>> {
        return trackRepository.getTracksByMangaIdAsFlow(mangaId)
    }
}
