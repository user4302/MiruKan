package com.user4302.mika.domain.history.anime.interactor

import com.user4302.mika.domain.history.anime.model.AnimeHistory
import com.user4302.mika.domain.history.anime.model.AnimeHistoryWithRelations
import com.user4302.mika.domain.history.anime.repository.AnimeHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetAnimeHistory(
    private val repository: AnimeHistoryRepository,
) {

    suspend fun await(animeId: Long): List<AnimeHistory> {
        return repository.getHistoryByAnimeId(animeId)
    }

    fun subscribe(query: String): Flow<List<AnimeHistoryWithRelations>> {
        return repository.getAnimeHistory(query)
    }
}
