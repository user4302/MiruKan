package com.user4302.mika.util.episode

import com.user4302.domain.items.episode.model.applyFilters
import com.user4302.mika.data.download.anime.AnimeDownloadManager
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.ui.entries.anime.EpisodeList

/**
 * Gets next unseen episode with filters and sorting applied
 */
fun List<Episode>.getNextUnseen(anime: Anime, downloadManager: AnimeDownloadManager): Episode? {
    return applyFilters(anime, downloadManager).let { episodes: List<Episode> ->
        if (anime.sortDescending()) {
            episodes.findLast { episode: Episode -> !episode.seen }
        } else {
            episodes.find { episode: Episode -> !episode.seen }
        }
    }
}

/**
 * Gets next unseen episode with filters and sorting applied
 */
fun List<EpisodeList.Item>.getNextUnseen(anime: Anime): Episode? {
    return applyFilters(anime).let { episodes: List<Episode> ->
        if (anime.sortDescending()) {
            episodes.findLast { episode: Episode -> !episode.seen }
        } else {
            episodes.find { episode: Episode -> !episode.seen }
        }
    }?.episode
}
