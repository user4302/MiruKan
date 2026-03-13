package com.user4302.mika.util

import com.user4302.domain.entries.anime.interactor.UpdateAnime
import com.user4302.domain.entries.anime.model.hasCustomBackground
import com.user4302.domain.entries.anime.model.hasCustomCover
import com.user4302.domain.entries.anime.model.toSAnime
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.data.cache.AnimeBackgroundCache
import com.user4302.mika.data.cache.AnimeCoverCache
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.source.local.entries.anime.isLocal
import com.user4302.mika.source.local.image.anime.LocalAnimeBackgroundManager
import com.user4302.mika.source.local.image.anime.LocalAnimeCoverManager
import com.user4302.mika.source.local.image.anime.LocalEpisodeThumbnailManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.InputStream
import java.time.Instant
import com.user4302.mika.data.database.models.anime.Episode as SEpisode

/**
 * Call before updating [Anime.thumbnail_url] to ensure old cover can be cleared from cache
 */
fun Anime.prepUpdateCover(coverCache: AnimeCoverCache, remoteAnime: SAnime, refreshSameUrl: Boolean): Anime {
    // Never refresh covers if the new url is null, as the current url has possibly become invalid
    val newUrl = remoteAnime.thumbnail_url ?: return this

    // Never refresh covers if the url is empty to avoid "losing" existing covers
    if (newUrl.isEmpty()) return this

    if (!refreshSameUrl && thumbnailUrl == newUrl) return this

    return when {
        isLocal() -> {
            this.copy(coverLastModified = Instant.now().toEpochMilli())
        }
        hasCustomCover(coverCache) -> {
            coverCache.deleteFromCache(this, false)
            this
        }
        else -> {
            coverCache.deleteFromCache(this, false)
            this.copy(coverLastModified = Instant.now().toEpochMilli())
        }
    }
}

/**
 * Call before updating [Anime.background_url] to ensure old background can be cleared from cache
 */
fun Anime.prepUpdateBackground(
    backgroundCache: AnimeBackgroundCache,
    remoteAnime: SAnime,
    refreshSameUrl: Boolean,
): Anime {
    // Never refresh backgrounds if the new url is null, as the current url has possibly become invalid
    val newUrl = remoteAnime.background_url ?: return this

    // Never refresh covers if the url is empty to avoid "losing" existing backgrounds
    if (newUrl.isEmpty()) return this

    if (!refreshSameUrl && backgroundUrl == newUrl) return this

    return when {
        isLocal() -> {
            this.copy(backgroundLastModified = Instant.now().toEpochMilli())
        }
        hasCustomBackground(backgroundCache) -> {
            backgroundCache.deleteFromCache(this, false)
            this
        }
        else -> {
            backgroundCache.deleteFromCache(this, false)
            this.copy(backgroundLastModified = Instant.now().toEpochMilli())
        }
    }
}

fun Anime.removeCovers(coverCache: AnimeCoverCache = Injekt.get()): Anime {
    if (isLocal()) return this
    return if (coverCache.deleteFromCache(this, true) > 0) {
        return copy(coverLastModified = Instant.now().toEpochMilli())
    } else {
        this
    }
}

fun Anime.removeBackgrounds(backgroundCache: AnimeBackgroundCache): Anime {
    if (isLocal()) return this
    return if (backgroundCache.deleteFromCache(this, true) > 0) {
        return copy(backgroundLastModified = Instant.now().toEpochMilli())
    } else {
        this
    }
}

suspend fun Anime.editCover(
    coverManager: LocalAnimeCoverManager,
    stream: InputStream,
    updateAnime: UpdateAnime = Injekt.get(),
    coverCache: AnimeCoverCache = Injekt.get(),
) {
    if (isLocal()) {
        coverManager.update(toSAnime(), stream)
        updateAnime.awaitUpdateCoverLastModified(id)
    } else if (favorite) {
        coverCache.setCustomCoverToCache(this, stream)
        updateAnime.awaitUpdateCoverLastModified(id)
    }
}

suspend fun Anime.editBackground(
    backgroundManager: LocalAnimeBackgroundManager,
    stream: InputStream,
    updateAnime: UpdateAnime = Injekt.get(),
    backgroundCache: AnimeBackgroundCache = Injekt.get(),
) {
    if (isLocal()) {
        backgroundManager.update(toSAnime(), stream)
        updateAnime.awaitUpdateBackgroundLastModified(id)
    } else if (favorite) {
        backgroundCache.setCustomBackgroundToCache(this, stream)
        updateAnime.awaitUpdateBackgroundLastModified(id)
    }
}

fun SEpisode.editThumbnail(
    anime: Anime,
    thumbnailManager: LocalEpisodeThumbnailManager,
    stream: InputStream,
) {
    if (anime.isLocal()) {
        thumbnailManager.update(anime.toSAnime(), this, stream)
    }
}
