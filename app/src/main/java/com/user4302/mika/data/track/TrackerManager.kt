package com.user4302.mika.data.track

import android.content.Context
import com.user4302.mika.data.track.anilist.Anilist
import com.user4302.mika.data.track.bangumi.Bangumi
import com.user4302.mika.data.track.jellyfin.Jellyfin
import com.user4302.mika.data.track.kavita.Kavita
import com.user4302.mika.data.track.kitsu.Kitsu
import com.user4302.mika.data.track.komga.Komga
import com.user4302.mika.data.track.mangaupdates.MangaUpdates
import com.user4302.mika.data.track.myanimelist.MyAnimeList
import com.user4302.mika.data.track.shikimori.Shikimori
import com.user4302.mika.data.track.simkl.Simkl
import com.user4302.mika.data.track.suwayomi.Suwayomi
import kotlinx.coroutines.flow.combine

class TrackerManager(context: Context) {

    companion object {
        const val ANILIST = 2L
        const val KITSU = 3L
        const val KAVITA = 8L
        const val SIMKL = 101L
        const val JELLYFIN = 102L
    }

    val myAnimeList = MyAnimeList(1L)
    val aniList = Anilist(ANILIST)
    val kitsu = Kitsu(KITSU)
    val shikimori = Shikimori(4L)
    val bangumi = Bangumi(5L)
    val komga = Komga(6L)
    val mangaUpdates = MangaUpdates(7L)
    val kavita = Kavita(KAVITA)
    val suwayomi = Suwayomi(9L)
    val simkl = Simkl(SIMKL)
    val jellyfin = Jellyfin(JELLYFIN)

    val trackers = listOf(
        myAnimeList, aniList, kitsu, shikimori, bangumi,
        komga, mangaUpdates, kavita, suwayomi, simkl, jellyfin,
    )

    fun loggedInTrackers() = trackers.filter { it.isLoggedIn }

    fun loggedInTrackersFlow() = combine(trackers.map { it.isLoggedInFlow }) {
        it.mapIndexedNotNull { index, isLoggedIn ->
            if (isLoggedIn) trackers[index] else null
        }
    }

    fun get(id: Long) = trackers.find { it.id == id }

    fun getAll(ids: Set<Long>) = trackers.filter { it.id in ids }
}
