package com.user4302.mika.domain.history.manga.repository

import com.user4302.mika.domain.history.manga.model.MangaHistory
import com.user4302.mika.domain.history.manga.model.MangaHistoryUpdate
import com.user4302.mika.domain.history.manga.model.MangaHistoryWithRelations
import kotlinx.coroutines.flow.Flow

interface MangaHistoryRepository {

    fun getMangaHistory(query: String): Flow<List<MangaHistoryWithRelations>>

    suspend fun getLastMangaHistory(): MangaHistoryWithRelations?

    suspend fun getTotalReadDuration(): Long

    suspend fun getHistoryByMangaId(mangaId: Long): List<MangaHistory>

    suspend fun resetMangaHistory(historyId: Long)

    suspend fun resetHistoryByMangaId(mangaId: Long)

    suspend fun deleteAllMangaHistory(): Boolean

    suspend fun upsertMangaHistory(historyUpdate: MangaHistoryUpdate)
}
