package com.user4302.mika.data.track.anime

import com.user4302.mika.domain.track.anime.model.AnimeTrack

object AnimeTrackMapper {
    fun mapTrack(
        id: Long,
        animeId: Long,
        syncId: Long,
        remoteId: Long,
        libraryId: Long?,
        title: String,
        lastEpisodeSeen: Double,
        totalEpisodes: Long,
        status: Long,
        score: Double,
        remoteUrl: String,
        startDate: Long,
        finishDate: Long,
        private: Boolean,
    ): AnimeTrack = AnimeTrack(
        id = id,
        animeId = animeId,
        trackerId = syncId,
        remoteId = remoteId,
        libraryId = libraryId,
        title = title,
        lastEpisodeSeen = lastEpisodeSeen,
        totalEpisodes = totalEpisodes,
        status = status,
        score = score,
        remoteUrl = remoteUrl,
        startDate = startDate,
        finishDate = finishDate,
        private = private,
    )
}
