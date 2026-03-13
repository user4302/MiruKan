package com.user4302.mika.domain.history.manga.interactor

import com.user4302.mika.domain.history.manga.model.MangaHistory
import com.user4302.mika.domain.history.manga.model.MangaHistoryWithRelations
import com.user4302.mika.domain.history.manga.repository.MangaHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetMangaHistory(
    private val repository: MangaHistoryRepository,
) {

    suspend fun await(mangaId: Long): List<MangaHistory> {
        return repository.getHistoryByMangaId(mangaId)
    }

    fun subscribe(query: String): Flow<List<MangaHistoryWithRelations>> {
        return repository.getMangaHistory(query)
    }
}
