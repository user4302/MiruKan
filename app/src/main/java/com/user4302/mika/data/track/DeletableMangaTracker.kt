package com.user4302.mika.data.track

import com.user4302.mika.domain.track.manga.model.MangaTrack

/**
 * Tracker that support deleting am entry from a user's list
 */
interface DeletableMangaTracker {

    suspend fun delete(track: MangaTrack)
}
