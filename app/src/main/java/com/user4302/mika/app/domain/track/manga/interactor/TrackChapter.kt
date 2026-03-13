package com.user4302.domain.track.manga.interactor

import android.content.Context
import com.user4302.domain.track.manga.model.toDbTrack
import com.user4302.domain.track.manga.model.toDomainTrack
import com.user4302.domain.track.manga.service.DelayedMangaTrackingUpdateJob
import com.user4302.domain.track.manga.store.DelayedMangaTrackingStore
import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.track.manga.interactor.GetMangaTracks
import com.user4302.mika.domain.track.manga.interactor.InsertMangaTrack
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import logcat.LogPriority

class TrackChapter(
    private val getTracks: GetMangaTracks,
    private val trackerManager: TrackerManager,
    private val insertTrack: InsertMangaTrack,
    private val delayedTrackingStore: DelayedMangaTrackingStore,
) {

    suspend fun await(context: Context, mangaId: Long, chapterNumber: Double, setupJobOnFailure: Boolean = true) {
        withNonCancellableContext {
            val tracks = getTracks.await(mangaId)
            if (tracks.isEmpty()) return@withNonCancellableContext

            tracks.mapNotNull { track ->
                val service = trackerManager.get(track.trackerId)
                if (service == null || !service.isLoggedIn || chapterNumber <= track.lastChapterRead) {
                    if (service == null || !service.isLoggedIn || chapterNumber <= track.lastChapterRead) {
                        return@mapNotNull null
                    }
                }

                async {
                    runCatching {
                        try {
                            val updatedTrack = service.mangaService.refresh(track.toDbTrack())
                                .toDomainTrack(idRequired = true)!!
                                .copy(lastChapterRead = chapterNumber)
                            service.mangaService.update(updatedTrack.toDbTrack(), true)
                            insertTrack.await(updatedTrack)
                            delayedTrackingStore.removeMangaItem(track.id)
                        } catch (e: Exception) {
                            delayedTrackingStore.addManga(track.id, chapterNumber)
                            if (setupJobOnFailure) {
                                DelayedMangaTrackingUpdateJob.setupTask(context)
                            }
                            throw e
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
