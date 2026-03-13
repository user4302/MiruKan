package com.user4302.mika.domain.history.anime.interactor

import com.user4302.mika.domain.history.anime.model.AnimeHistoryUpdate
import com.user4302.mika.domain.history.anime.repository.AnimeHistoryRepository

class UpsertAnimeHistory(
    private val historyRepository: AnimeHistoryRepository,
) {

    suspend fun await(historyUpdate: AnimeHistoryUpdate) {
        historyRepository.upsertAnimeHistory(historyUpdate)
    }
}
