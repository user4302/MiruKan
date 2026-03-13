package com.user4302.mika.domain.track.manga.interactor

import com.user4302.mika.domain.track.manga.model.MangaTrack
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTracksPerManga(
    private val trackRepository: MangaTrackRepository,
) {

    fun subscribe(): Flow<Map<Long, List<MangaTrack>>> {
        return trackRepository.getMangaTracksAsFlow().map { tracks -> tracks.groupBy { it.mangaId } }
    }
}
