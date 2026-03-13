package com.user4302.domain.download.anime.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.data.download.anime.AnimeDownloadManager
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager

class DeleteEpisodeDownload(
    private val sourceManager: AnimeSourceManager,
    private val downloadManager: AnimeDownloadManager,
) {

    suspend fun awaitAll(anime: Anime, vararg episodes: Episode) = withNonCancellableContext {
        sourceManager.get(anime.source)?.let { source ->
            downloadManager.deleteEpisodes(episodes.toList(), anime, source)
        }
    }
}
