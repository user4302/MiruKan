package com.user4302.mika.domain.history.manga.interactor

import com.user4302.mika.domain.history.manga.model.MangaHistoryUpdate
import com.user4302.mika.domain.history.manga.repository.MangaHistoryRepository

class UpsertMangaHistory(
    private val historyRepository: MangaHistoryRepository,
) {

    suspend fun await(historyUpdate: MangaHistoryUpdate) {
        historyRepository.upsertMangaHistory(historyUpdate)
    }
}
