package com.user4302.mika.domain.track.manga.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import logcat.LogPriority

class DeleteMangaTrack(
    private val trackRepository: MangaTrackRepository,
) {

    suspend fun await(mangaId: Long, trackerId: Long) {
        try {
            trackRepository.delete(mangaId, trackerId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
