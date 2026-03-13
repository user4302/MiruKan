package com.user4302.domain.track.anime.interactor

import com.user4302.domain.track.anime.model.toDbTrack
import com.user4302.domain.track.anime.model.toDomainTrack
import com.user4302.mika.data.track.Tracker
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.track.anime.interactor.GetAnimeTracks
import com.user4302.mika.domain.track.anime.interactor.InsertAnimeTrack
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class RefreshAnimeTracks(
    private val getTracks: GetAnimeTracks,
    private val trackerManager: TrackerManager,
    private val insertTrack: InsertAnimeTrack,
    private val syncEpisodeProgressWithTrack: SyncEpisodeProgressWithTrack,
) {

    /**
     * Fetches updated tracking data from all logged in trackers.
     *
     * @return Failed updates.
     */
    suspend fun await(animeId: Long): List<Pair<Tracker?, Throwable>> {
        return supervisorScope {
            return@supervisorScope getTracks.await(animeId)
                .map { it to trackerManager.get(it.trackerId) }
                .filter { (_, service) -> service?.isLoggedIn == true }
                .map { (track, service) ->
                    async {
                        return@async try {
                            val updatedTrack = service!!.animeService.refresh(track.toDbTrack()).toDomainTrack()!!
                            insertTrack.await(updatedTrack)
                            syncEpisodeProgressWithTrack.await(animeId, updatedTrack, service.animeService)
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
