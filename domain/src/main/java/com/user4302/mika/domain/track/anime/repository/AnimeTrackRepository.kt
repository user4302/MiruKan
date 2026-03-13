package com.user4302.mika.domain.track.anime.repository

import com.user4302.mika.domain.track.anime.model.AnimeTrack
import kotlinx.coroutines.flow.Flow

interface AnimeTrackRepository {

    suspend fun getTrackByAnimeId(id: Long): AnimeTrack?

    suspend fun getTracksByAnimeId(animeId: Long): List<AnimeTrack>

    fun getAnimeTracksAsFlow(): Flow<List<AnimeTrack>>

    fun getTracksByAnimeIdAsFlow(animeId: Long): Flow<List<AnimeTrack>>

    suspend fun delete(animeId: Long, trackerId: Long)

    suspend fun insertAnime(track: AnimeTrack)

    suspend fun insertAllAnime(tracks: List<AnimeTrack>)
}
