package com.user4302.domain.track.anime.interactor

import com.user4302.domain.track.anime.model.toDbTrack
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.track.AnimeTracker
import com.user4302.mika.data.track.EnhancedAnimeTracker
import com.user4302.mika.domain.items.episode.interactor.GetEpisodesByAnimeId
import com.user4302.mika.domain.items.episode.interactor.UpdateEpisode
import com.user4302.mika.domain.items.episode.model.toEpisodeUpdate
import com.user4302.mika.domain.track.anime.interactor.InsertAnimeTrack
import com.user4302.mika.domain.track.anime.model.AnimeTrack
import logcat.LogPriority
import kotlin.math.max

class SyncEpisodeProgressWithTrack(
    private val updateEpisode: UpdateEpisode,
    private val insertTrack: InsertAnimeTrack,
    private val getEpisodesByAnimeId: GetEpisodesByAnimeId,
) {

    suspend fun await(
        animeId: Long,
        remoteTrack: AnimeTrack,
        service: AnimeTracker,
    ) {
        if (service !is EnhancedAnimeTracker) {
            return
        }

        val sortedEpisodes = getEpisodesByAnimeId.await(animeId)
            .sortedBy { it.episodeNumber }
            .filter { it.isRecognizedNumber }

        val episodeUpdates = sortedEpisodes
            .filter { episode -> episode.episodeNumber <= remoteTrack.lastEpisodeSeen && !episode.seen }
            .map { it.copy(seen = true).toEpisodeUpdate() }

        // only take into account continuous watching
        val localLastSeen = sortedEpisodes.takeWhile { it.seen }.lastOrNull()?.episodeNumber ?: 0F
        val lastSeen = max(remoteTrack.lastEpisodeSeen, localLastSeen.toDouble())
        val updatedTrack = remoteTrack.copy(lastEpisodeSeen = lastSeen)

        try {
            service.update(updatedTrack.toDbTrack())
            updateEpisode.awaitAll(episodeUpdates)
            insertTrack.await(updatedTrack)
        } catch (e: Throwable) {
            logcat(LogPriority.WARN, e)
        }
    }
}
