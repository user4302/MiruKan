package com.user4302.domain.track.anime.interactor

import com.user4302.domain.track.anime.model.toDbTrack
import com.user4302.domain.track.anime.model.toDomainTrack
import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.core.common.util.lang.withIOContext
import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.database.models.anime.AnimeTrack
import com.user4302.mika.data.track.AnimeTracker
import com.user4302.mika.data.track.EnhancedAnimeTracker
import com.user4302.mika.data.track.Tracker
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.history.anime.interactor.GetAnimeHistory
import com.user4302.mika.domain.items.episode.interactor.GetEpisodesByAnimeId
import com.user4302.mika.domain.track.anime.interactor.InsertAnimeTrack
import com.user4302.mika.util.lang.convertEpochMillisZone
import logcat.LogPriority
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.time.ZoneOffset

class AddAnimeTracks(
    private val insertTrack: InsertAnimeTrack,
    private val syncChapterProgressWithTrack: SyncEpisodeProgressWithTrack,
    private val getEpisodesByAnimeId: GetEpisodesByAnimeId,
    private val trackerManager: TrackerManager,
) {

    // TODO: update all trackers based on common data
    suspend fun bind(tracker: AnimeTracker, item: AnimeTrack, animeId: Long) = withNonCancellableContext {
        withIOContext {
            val allChapters = getEpisodesByAnimeId.await(animeId)
            val hasSeenEpisodes = allChapters.any { it.seen }
            tracker.bind(item, hasSeenEpisodes)

            var track = item.toDomainTrack(idRequired = false) ?: return@withIOContext

            insertTrack.await(track)

            // TODO: merge into [SyncChapterProgressWithTrack]?
            // Update chapter progress if newer chapters marked read locally
            if (hasSeenEpisodes) {
                val latestLocalReadChapterNumber = allChapters
                    .sortedBy { it.episodeNumber }
                    .takeWhile { it.seen }
                    .lastOrNull()
                    ?.episodeNumber ?: -1.0

                if (latestLocalReadChapterNumber > track.lastEpisodeSeen) {
                    track = track.copy(
                        lastEpisodeSeen = latestLocalReadChapterNumber,
                    )
                    tracker.setRemoteLastEpisodeSeen(track.toDbTrack(), latestLocalReadChapterNumber.toInt())
                }

                if (track.startDate <= 0) {
                    val firstReadChapterDate = Injekt.get<GetAnimeHistory>().await(animeId)
                        .sortedBy { it.seenAt }
                        .firstOrNull()
                        ?.seenAt

                    firstReadChapterDate?.let {
                        val startDate = firstReadChapterDate.time.convertEpochMillisZone(
                            ZoneOffset.systemDefault(),
                            ZoneOffset.UTC,
                        )
                        track = track.copy(
                            startDate = startDate,
                        )
                        tracker.setRemoteStartDate(track.toDbTrack(), startDate)
                    }
                }
            }

            syncChapterProgressWithTrack.await(animeId, track, tracker)
        }
    }

    suspend fun bindEnhancedTrackers(anime: Anime, source: AnimeSource) = withNonCancellableContext {
        withIOContext {
            trackerManager.loggedInTrackers()
                .filterIsInstance<EnhancedAnimeTracker>()
                .filter { it.accept(source) }
                .forEach { service ->
                    try {
                        service.match(anime)?.let { track ->
                            track.anime_id = anime.id
                            (service as Tracker).animeService.bind(track)
                            insertTrack.await(track.toDomainTrack(idRequired = false)!!)

                            syncChapterProgressWithTrack.await(
                                anime.id,
                                track.toDomainTrack(idRequired = false)!!,
                                service.animeService,
                            )
                        }
                    } catch (e: Exception) {
                        logcat(
                            LogPriority.WARN,
                            e,
                        ) { "Could not match anime: ${anime.title} with service $service" }
                    }
                }
        }
    }
}
