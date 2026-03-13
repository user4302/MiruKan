package com.user4302.mika.domain.track.anime.interactor

import com.user4302.mika.domain.track.anime.model.AnimeTrack
import com.user4302.mika.domain.track.anime.repository.AnimeTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTracksPerAnime(
    private val trackRepository: AnimeTrackRepository,
) {

    fun subscribe(): Flow<Map<Long, List<AnimeTrack>>> {
        return trackRepository.getAnimeTracksAsFlow().map { tracks -> tracks.groupBy { it.animeId } }
    }
}
