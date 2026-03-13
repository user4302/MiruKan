package com.user4302.domain.track.anime.interactor

import android.content.Context
import com.user4302.domain.track.anime.model.toDbTrack
import com.user4302.domain.track.anime.model.toDomainTrack
import com.user4302.domain.track.anime.service.DelayedAnimeTrackingUpdateJob
import com.user4302.domain.track.anime.store.DelayedAnimeTrackingStore
import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.track.anime.interactor.GetAnimeTracks
import com.user4302.mika.domain.track.anime.interactor.InsertAnimeTrack
import com.user4302.mika.util.system.isOnline
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import logcat.LogPriority

class TrackEpisode(
    private val getTracks: GetAnimeTracks,
    private val trackerManager: TrackerManager,
    private val insertTrack: InsertAnimeTrack,
    private val delayedTrackingStore: DelayedAnimeTrackingStore,
) {

    suspend fun await(context: Context, animeId: Long, episodeNumber: Double, setupJobOnFailure: Boolean = true) {
        withNonCancellableContext {
            val tracks = getTracks.await(animeId)
            if (tracks.isEmpty()) return@withNonCancellableContext

            tracks.mapNotNull { track ->
                val service = trackerManager.get(track.trackerId)
                if (service == null || !service.isLoggedIn || episodeNumber <= track.lastEpisodeSeen) {
                    return@mapNotNull null
                }

                async {
                    runCatching {
                        if (context.isOnline()) {
                            val updatedTrack = service.animeService.refresh(track.toDbTrack())
                                .toDomainTrack(idRequired = true)!!
                                .copy(lastEpisodeSeen = episodeNumber)
                            service.animeService.update(updatedTrack.toDbTrack(), true)
                            insertTrack.await(updatedTrack)
                            delayedTrackingStore.removeAnimeItem(track.id)
                        } else {
                            delayedTrackingStore.addAnime(track.id, episodeNumber)
                            if (setupJobOnFailure) {
                                DelayedAnimeTrackingUpdateJob.setupTask(context)
                            }
                        }
                    }
                }
            }
                .awaitAll()
                .mapNotNull { it.exceptionOrNull() }
                .forEach { logcat(LogPriority.INFO, it) }
        }
    }
}
