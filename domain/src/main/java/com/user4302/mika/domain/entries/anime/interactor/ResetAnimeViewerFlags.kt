package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.domain.entries.anime.repository.AnimeRepository

class ResetAnimeViewerFlags(
    private val animeRepository: AnimeRepository,
) {
    suspend fun await(): Boolean {
        return animeRepository.resetAnimeViewerFlags()
    }
}
