package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetAnimeFavorites(
    private val animeRepository: AnimeRepository,
) {

    suspend fun await(): List<Anime> {
        return animeRepository.getAnimeFavorites()
    }

    fun subscribe(sourceId: Long): Flow<List<Anime>> {
        return animeRepository.getAnimeFavoritesBySourceId(sourceId)
    }
}
