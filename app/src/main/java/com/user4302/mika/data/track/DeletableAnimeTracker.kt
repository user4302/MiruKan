package com.user4302.mika.data.track

import com.user4302.mika.domain.track.anime.model.AnimeTrack

/**
 *Tracker that support deleting am entry from a user's list
 */
interface DeletableAnimeTracker {

    suspend fun delete(track: AnimeTrack)
}
