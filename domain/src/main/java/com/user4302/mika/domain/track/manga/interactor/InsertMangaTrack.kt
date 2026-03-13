package com.user4302.mika.domain.track.manga.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.track.manga.model.MangaTrack
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import logcat.LogPriority

class InsertMangaTrack(
    private val trackRepository: MangaTrackRepository,
) {

    suspend fun await(track: MangaTrack) {
        try {
            trackRepository.insertManga(track)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    suspend fun awaitAll(tracks: List<MangaTrack>) {
        try {
            trackRepository.insertAllManga(tracks)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
