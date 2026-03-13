package com.user4302.mika.domain.history.anime.interactor

import com.user4302.mika.domain.history.anime.model.AnimeHistoryWithRelations
import com.user4302.mika.domain.history.anime.repository.AnimeHistoryRepository

class RemoveAnimeHistory(
    private val repository: AnimeHistoryRepository,
) {

    suspend fun awaitAll(): Boolean {
        return repository.deleteAllAnimeHistory()
    }

    suspend fun await(history: AnimeHistoryWithRelations) {
        repository.resetAnimeHistory(history.id)
    }

    suspend fun await(animeId: Long) {
        repository.resetHistoryByAnimeId(animeId)
    }
}
