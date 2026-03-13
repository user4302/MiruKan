package com.user4302.mika.domain.history.manga.interactor

import com.user4302.mika.domain.history.manga.model.MangaHistoryWithRelations
import com.user4302.mika.domain.history.manga.repository.MangaHistoryRepository

class RemoveMangaHistory(
    private val repository: MangaHistoryRepository,
) {

    suspend fun awaitAll(): Boolean {
        return repository.deleteAllMangaHistory()
    }

    suspend fun await(history: MangaHistoryWithRelations) {
        repository.resetMangaHistory(history.id)
    }

    suspend fun await(mangaId: Long) {
        repository.resetHistoryByMangaId(mangaId)
    }
}
