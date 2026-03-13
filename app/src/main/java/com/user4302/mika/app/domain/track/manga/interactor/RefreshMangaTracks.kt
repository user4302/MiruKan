package com.user4302.domain.track.manga.interactor

import com.user4302.domain.track.manga.model.toDbTrack
import com.user4302.domain.track.manga.model.toDomainTrack
import com.user4302.mika.data.track.Tracker
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.track.manga.interactor.GetMangaTracks
import com.user4302.mika.domain.track.manga.interactor.InsertMangaTrack
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class RefreshMangaTracks(
    private val getTracks: GetMangaTracks,
    private val trackerManager: TrackerManager,
    private val insertTrack: InsertMangaTrack,
    private val syncChapterProgressWithTrack: SyncChapterProgressWithTrack,
) {

    /**
     * Fetches updated tracking data from all logged in trackers.
     *
     * @return Failed updates.
     */
    suspend fun await(mangaId: Long): List<Pair<Tracker?, Throwable>> {
        return supervisorScope {
            return@supervisorScope getTracks.await(mangaId)
                .map { it to trackerManager.get(it.trackerId) }
                .filter { (_, service) -> service?.isLoggedIn == true }
                .map { (track, service) ->
                    async {
                        return@async try {
                            val updatedTrack = service!!.mangaService.refresh(track.toDbTrack()).toDomainTrack()!!
                            insertTrack.await(updatedTrack)
                            syncChapterProgressWithTrack.await(mangaId, updatedTrack, service.mangaService)
                            null
                        } catch (e: Throwable) {
                            service to e
                        }
                    }
                }
                .awaitAll()
                .filterNotNull()
        }
    }
}
