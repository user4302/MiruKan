package com.user4302.mika.util.episode

import com.user4302.mika.data.download.anime.AnimeDownloadCache
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.source.local.entries.anime.isLocal
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Returns a copy of the list with not downloaded chapters removed.
 */
fun List<Episode>.filterDownloadedEpisodes(anime: Anime): List<Episode> {
    if (anime.isLocal()) return this

    val downloadCache: AnimeDownloadCache = Injekt.get()

    return filter {
        downloadCache.isEpisodeDownloaded(
            it.name,
            it.scanlator,
            anime.title,
            anime.source,
            false,
        )
    }
}
