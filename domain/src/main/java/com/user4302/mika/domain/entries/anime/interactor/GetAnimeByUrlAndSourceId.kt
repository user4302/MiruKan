package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository

class GetAnimeByUrlAndSourceId(
    private val animeRepository: AnimeRepository,
) {
    suspend fun await(url: String, sourceId: Long): Anime? {
        return animeRepository.getAnimeByUrlAndSourceId(url, sourceId)
    }
}
