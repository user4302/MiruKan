package com.user4302.mika.domain.history.anime.repository

import com.user4302.mika.domain.history.anime.model.AnimeHistory
import com.user4302.mika.domain.history.anime.model.AnimeHistoryUpdate
import com.user4302.mika.domain.history.anime.model.AnimeHistoryWithRelations
import kotlinx.coroutines.flow.Flow

interface AnimeHistoryRepository {

    fun getAnimeHistory(query: String): Flow<List<AnimeHistoryWithRelations>>

    suspend fun getLastAnimeHistory(): AnimeHistoryWithRelations?

    suspend fun resetAnimeHistory(historyId: Long)

    suspend fun getHistoryByAnimeId(animeId: Long): List<AnimeHistory>

    suspend fun resetHistoryByAnimeId(animeId: Long)

    suspend fun deleteAllAnimeHistory(): Boolean

    suspend fun upsertAnimeHistory(historyUpdate: AnimeHistoryUpdate)
}
