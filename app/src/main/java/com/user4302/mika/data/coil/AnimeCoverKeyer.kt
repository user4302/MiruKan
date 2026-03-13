package com.user4302.mika.data.coil

import coil3.key.Keyer
import coil3.request.Options
import com.user4302.domain.entries.anime.model.hasCustomBackground
import com.user4302.domain.entries.anime.model.hasCustomCover
import com.user4302.mika.data.cache.AnimeCoverCache
import com.user4302.mika.domain.entries.anime.model.AnimeCover
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import com.user4302.mika.domain.entries.anime.model.Anime as DomainAnime

class AnimeKeyer : Keyer<DomainAnime> {
    override fun key(data: DomainAnime, options: Options): String {
        return when {
            options.useBackground && data.hasCustomBackground() -> "anime;${data.id};${data.backgroundLastModified}"
            options.useBackground -> "anime;${data.backgroundUrl};${data.backgroundLastModified}"
            data.hasCustomCover() -> "anime;${data.id};${data.coverLastModified}"
            else -> "anime;${data.thumbnailUrl};${data.coverLastModified}"
        }
    }
}

class AnimeCoverKeyer(
    private val coverCache: AnimeCoverCache = Injekt.get(),
) : Keyer<AnimeCover> {
    override fun key(data: AnimeCover, options: Options): String {
        return if (coverCache.getCustomCoverFile(data.animeId).exists()) {
            "anime;${data.animeId};${data.lastModified}"
        } else {
            "anime;${data.url};${data.lastModified}"
        }
    }
}
